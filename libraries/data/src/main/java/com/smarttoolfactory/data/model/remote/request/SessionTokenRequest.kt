package com.smarttoolfactory.data.model.remote.request

import com.google.gson.annotations.SerializedName

data class SessionTokenRequest(@SerializedName("event_slug") val eventSlug: String)
