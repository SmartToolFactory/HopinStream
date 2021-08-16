package com.smarttoolfactory.data.model.remote.broadcast

import com.google.gson.annotations.SerializedName

data class Stage(

    @SerializedName("name") val name: String,
    @SerializedName("stream_provider") val streamProvider: String,
    @SerializedName("video_url") val videoUrl: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("external_id") val externalId: String,
    @SerializedName("is_usable") val isUsable: Boolean,
    @SerializedName("is_live") val isLive: Boolean,
    @SerializedName("stage_channel_name") val stageChannelName: String
)
