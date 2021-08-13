package com.smarttoolfactory.myapplication.model.broadcast

import com.google.gson.annotations.SerializedName

data class VideoChannels(

    @SerializedName("uuid") val uuid: String,
    @SerializedName("stream_url") val streamUrl: String,
    @SerializedName("status") val status: String,
    @SerializedName("delivery_type") val deliveryType: String,
    @SerializedName("broadcast_type") val broadcastType: String
)
