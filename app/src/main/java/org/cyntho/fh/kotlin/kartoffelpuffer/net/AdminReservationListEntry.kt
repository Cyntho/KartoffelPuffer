package org.cyntho.fh.kotlin.kartoffelpuffer.net


import java.sql.Timestamp

data class AdminReservationListEntry(
    val id: Int,
    val start: Timestamp,
    val username: String
)

