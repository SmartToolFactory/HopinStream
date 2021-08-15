package com.smarttoolfactory.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Wrapper class that contains session token and event id
 */
@Parcelize
data class UserSession(val sessionToken: String, val evenId: Long) : Parcelable
