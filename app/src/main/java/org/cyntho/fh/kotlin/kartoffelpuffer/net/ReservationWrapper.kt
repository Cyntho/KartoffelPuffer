package org.cyntho.fh.kotlin.kartoffelpuffer.net

import java.sql.Timestamp

data class ReservationWrapper(
    val id: Int,
    val layout: Int,
    val x: Int,
    val y: Int,
    val time: Long
)
