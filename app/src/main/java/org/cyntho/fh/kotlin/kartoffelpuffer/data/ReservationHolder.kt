package org.cyntho.fh.kotlin.kartoffelpuffer.data

import java.sql.Timestamp

data class ReservationHolder(
    var x: Int,
    var y: Int,
    var time: Timestamp,
    var pplCurrent: Int,
    var pplMax: Int,
    var dishes: MutableMap<Int, Int>?
)
