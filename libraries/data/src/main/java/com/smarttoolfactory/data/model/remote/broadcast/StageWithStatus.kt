package com.smarttoolfactory.myapplication.model.broadcast

import com.google.gson.annotations.SerializedName
import com.smarttoolfactory.data.model.remote.broadcast.Broadcasts

data class StageWithStatus(

    @SerializedName("name") val name: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("external_id") val externalId: String,
    @SerializedName("stream_provider") val streamProvider: String,
    @SerializedName("video_url") val videoUrl: String,
    @SerializedName("is_usable") val isUsable: Boolean,
    @SerializedName("is_live") val isLive: Boolean,
    @SerializedName("broadcast_channel_name") val broadcastChannelName: String,
    @SerializedName("stage_channel_name") val stageChannelName: String,
    @SerializedName("broadcasts") val broadcasts: List<Broadcasts>,
    @SerializedName("additional_information") val additionalInformation: String,
    @SerializedName("video_channels") val videoChannels: List<VideoChannels>
)
