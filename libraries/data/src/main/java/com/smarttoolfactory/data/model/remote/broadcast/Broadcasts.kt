package com.smarttoolfactory.data.model.remote.broadcast

import com.google.gson.annotations.SerializedName

data class Broadcasts(

    @SerializedName("uuid") val uuid: String,
    @SerializedName("stream_url") val streamUrl: String,
    @SerializedName("status") val status: String,
    @SerializedName("broadcast_type") val broadcastType: String,
    @SerializedName("delivery_type") val deliveryType: String
)
