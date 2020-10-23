package com.showdy.alarm.data



/**
 * Created by <b>Showdy</b> on 2020/10/14 17:50
 *
 */
data class SResult(
    val timestamp: Long = System.currentTimeMillis(),
    val values: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SResult

        if (timestamp != other.timestamp) return false
        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + values.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "$timestamp=${values.joinToString(",")}\n"
    }
}