package org.cyntho.fh.kotlin.kartoffelpuffer.net

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * NetPacket data class:
 *
 * To be serialized by NetManager, indicating what action to take
 * @param time:         TimeStamp (long) of packet
 * @param userToken:    client identifier
 * @param type:         0 = Status message
 *                      1 = registration request
 *                      2 = login request
 *                      3 = update request: Layout
 *                      4 = update request: Dishes
 *                      5 = reservation request
 *
 * @param data:         Actual Data, depends on @type
 */
@Serializable
data class NetPacket(
    val time: Long,
    val userToken: String,
    val type: Int,
    val data: String
)


