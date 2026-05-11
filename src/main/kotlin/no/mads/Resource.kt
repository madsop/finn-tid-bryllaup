package no.mads

import io.smallrye.common.annotation.Blocking
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@ApplicationScoped
@Path("/")
class Resource(
    val service: Service,
    val epostsender: Epostsender,
    val secretFactory: SecretFactory,
) {
    private val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @GET
    @Blocking
    fun finnTid(): List<LedigTid> {
        println(System.getenv("password"))
        println(secretFactory.getEpost())
        val aktuelleDatoer = secretFactory.getDatoer().split(" ").map {
            val splitted = it.split(",")
            val dato = LocalDate.parse(splitted[0], pattern)
            val stedId = UUID.fromString(splitted[1])
            val sted: String = splitted[2]
            Request(stedId, dato, sted)
        }

        val ledigeTider = service.finnTid(aktuelleDatoer)

        if (ledigeTider.isNotEmpty()) {
            epostsender.sendEpost(ledigeTider)
        } else {
            println("Fant ingen ledige tider")
        }

        return ledigeTider
    }
}

@ApplicationScoped
class Service(
    @RestClient val klient: Klient
) {
    fun finnTid(request: List<Request>) = request.flatMap { finnTid(it.stedId, it.sted, it.dag, it.dag) }

    fun finnTid(stedId: UUID, sted: String, fra: LocalDate, til: LocalDate): List<LedigTid> {
        return klient.finnTid(stedId, fra, til).timeslotsByDate.entries
            .map { it.key to it.value.filter { tid -> tid.bookingAllowed || tid.displayAsBlockedButAllowed } }
            .filter { it.second.isNotEmpty() }
            .flatMap { entry -> entry.second.map { LedigTid(entry.first, it.startTime,  stedId, sted, it) } }
    }
}

data class Request(
    val stedId: UUID,
    val dag: LocalDate,
    val sted: String
)

data class LedigTid(
    val dato: LocalDate,
    val starttid: LocalTime,
    val stedId: UUID,
    val sted: String,
    val tidsslot: Timeslot
)