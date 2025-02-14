package me.jesforge.proxymanager.utils

import java.time.Duration

class ParseTime {

    fun parseDuration(input: String): Duration {
        val regex = "(\\d+)([dhms])".toRegex()
        var totalDuration = Duration.ZERO

        regex.findAll(input).forEach { match ->
            val value = match.groupValues[1].toLong()
            when (match.groupValues[2]) {
                "d" -> totalDuration = totalDuration.plusDays(value)
                "h" -> totalDuration = totalDuration.plusHours(value)
                "m" -> totalDuration = totalDuration.plusMinutes(value)
                "s" -> totalDuration = totalDuration.plusSeconds(value)
            }
        }
        return totalDuration
    }

}