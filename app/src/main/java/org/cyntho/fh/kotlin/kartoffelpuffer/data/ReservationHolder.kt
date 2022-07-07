package org.cyntho.fh.kotlin.kartoffelpuffer.data

import java.sql.Timestamp

data class ReservationHolder(
    var layout: Int,
    var x: Int,
    var y: Int,
    var time: Long,
    var pplCurrent: Int,
    var pplMax: Int,
    var dishes: MutableMap<Int, Int>?
)
