package no.mads

import io.quarkus.mailer.Mail
import io.quarkus.mailer.Mailer
import jakarta.enterprise.context.Dependent

@Dependent
class Epostsender(
    val mailer: Mailer,
    val secretFactory: SecretFactory) {
    fun sendEpost(ledigeTider: List<LedigTid>) {
        val text = ledigeTider.prettyPrint()
        secretFactory.getEpost().split(" ").forEach { epost ->
            val mail = Mail.withText(
                epost,
                "Ledig tid for bryllup",
                text
            )
            println("Sender epost om ${ledigeTider.size} ledige tider")
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