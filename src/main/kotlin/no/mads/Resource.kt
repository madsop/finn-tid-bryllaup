package no.mads

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.util.UUID

@ApplicationScoped
@Path("/")
class Resource(val service: Service) {
    @GET
    fun finnTid(): List<LedigTid> {
        val muligheter = listOf<Request>(
        )
        val ledigeTider = service.finnTid(muligheter)
        if (ledigeTider.isNotEmpty()) {
            // Varsle
        }
        return ledigeTider
    }
}

@ApplicationScoped
class Service(
    @RestClient val klient: Klient
) {
    fun finnTid(request: List<Request>) = request.flatMap { finnTid(it.rom, it.fra, it.til) }

    fun finnTid(rom: UUID, fra: LocalDate, til: LocalDate): List<LedigTid> {
        return klient.finnTid(rom, fra, til).timeslotsByDate.entries
            .map { it.key to it.value.filter { tid -> tid.bookingAllowed || tid.displayAsBlockedButAllowed } }
            .filter { it.second.isNotEmpty() }
            .flatMap { entry -> entry.second.map { LedigTid(entry.first, it.startTime, it) } }
    }
}

data class Request(
    val rom: UUID,
    val fra: LocalDate,
    val til: LocalDate,
)

data class LedigTid(
    val dato: LocalDate,
    val starttid: LocalTime,
    val tidsslot: Timeslot
)