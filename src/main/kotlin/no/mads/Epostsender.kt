package no.mads

import io.quarkus.mailer.Mail
import io.quarkus.mailer.Mailer
import jakarta.enterprise.context.Dependent
import org.eclipse.microprofile.config.inject.ConfigProperty

@Dependent
class Epostsender(
    val mailer: Mailer,
    @ConfigProperty(name = "quarkus.epost")
    private var epost: String) {
    fun sendEpost(ledigeTider: List<LedigTid>) {
        val text = ledigeTider.prettyPrint()
        epost.split(" ").forEach { epost ->
            val mail = Mail.withText(
                epost,
                "Ledig tid for bryllup",
                text
            )
            println("Sender epost")
            mailer.send(mail)
            println("Sendte epost")
        }
    }
}

private fun List<LedigTid>.prettyPrint() = joinToString(System.lineSeparator()) {
    """-----
            Dato: ${it.dato}, klokka ${it.starttid}, sted ${it.sted}.
            
            Detaljer:
                $it

                
        """.trimIndent()
} + System.lineSeparator() + System.lineSeparator() + "Dette er en automatisk generert epost"