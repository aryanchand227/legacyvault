package com.timecapsule.app.data.model

data class Capsule(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "Personal",
    val accessType: String = "Personal",
    val openTime: Long = 0L,
    var isOpened: Boolean = false,
    var emotion: Int? = null,
    val unlockKey: String? = null,
    val sharedUsers: List<String> = emptyList()
)