import com.google.gson.annotations.SerializedName

data class JWTPayload(

    @SerializedName("jti") val jti: String,
    @SerializedName("sub") val sub: Int,
    @SerializedName("persona_id") val personaId: Int,
    @SerializedName("registration_id") val registrationId: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("role") val role: String,
    @SerializedName("multiple_conn") val multipleConn: Boolean
)
