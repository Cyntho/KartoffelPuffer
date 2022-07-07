package org.cyntho.fh.kotlin.kartoffelpuffer.net


import java.sql.Timestamp



data class ReservationListEntry(
    val id: Int,
    val layout: Int,
    val start: Long,
    val end: Long,
    val username: String,
    val people: Int,
)