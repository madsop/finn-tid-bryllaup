package no.mads

import jakarta.enterprise.context.Dependent
import jakarta.ws.rs.GET
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@RegisterRestClient
@Dependent
interface Klient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun finnTid(
        @QueryParam("bookableAssetIds") bookableAssetIds: UUID,
        @QueryParam("fromInclusive") fromInclusive: LocalDate,
        @QueryParam("toInclusive") toInclusive: LocalDate,
    ) : RestResponse
}

data class RestResponse(
    val timeslotsByDate: Map<LocalDate, List<Timeslot>>,
    val firstBookableDate: LocalDate,
)

data class Timeslot(
    val booked: Boolean,
    val startTime: LocalTime,
    val duration: Duration,
    val bookingAllowed: Boolean,
    val displayAsBlockedButAllowed: Boolean
)