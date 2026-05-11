package no.mads

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import io.quarkus.arc.profile.IfBuildProfile
import io.quarkus.arc.profile.UnlessBuildProfile
import jakarta.annotation.PostConstruct
import jakarta.annotation.Priority
import jakarta.enterprise.context.Dependent
import org.eclipse.microprofile.config.inject.ConfigProperty

interface SecretFactory {
    fun getEpost(): String
    fun getEpostpassord(): String
    fun getDatoer(): String
}

@Dependent
@IfBuildProfile("dev")
class EnvSecretFactory(
    @ConfigProperty(name = "quarkus.epost")
    private var epost: String,
    @ConfigProperty(name = "quarkus.epostpassord")
    private var epostpassord: String,
    @ConfigProperty(name = "datoer")
    private var datoer: String,
) : SecretFactory {
    override fun getEpost() = epost
    override fun getEpostpassord() = epostpassord
    override fun getDatoer() = datoer
}

@Dependent
@Priority(100)
@UnlessBuildProfile("dev")
class GCPSecretFactory(
    @ConfigProperty(name = "secretManagerProjectId", defaultValue = "")
    var secretManagerProjectId: String,
) : SecretFactory {
    private lateinit var client: SecretManagerServiceClient

    @PostConstruct
    fun setup() {
        client = SecretManagerServiceClient.create()
    }


    override fun getEpost() = getSecretFromSecretManager(GCPSecretManagerKey.EPOST)

    override fun getEpostpassord() = getSecretFromSecretManager(GCPSecretManagerKey.EPOSTPASSORD)

    override fun getDatoer() = getSecretFromSecretManager(GCPSecretManagerKey.DATOER)

    private fun getSecretFromSecretManager(secretName: GCPSecretManagerKey): String {
        val secretVersionName = SecretVersionName.of(secretManagerProjectId, secretName.key, "latest")
        return client.accessSecretVersion(secretVersionName).payload.data.toStringUtf8()
    }
}

private enum class GCPSecretManagerKey(val key: String) {
    EPOST("epost"),
    EPOSTPASSORD("epostpassord"),
    DATOER("datoer");
}