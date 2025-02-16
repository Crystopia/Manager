package me.jesforge.proxymanager.utils

import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher
import java.util.regex.Pattern

class ParseTime {

    fun parseDuration(input: String): Duration {
        val instant = Instant.parse(input)
        val now = OffsetDateTime.now()

        return Duration.between(instant, now)
    }

    val TIME_PATTERN: Pattern = Pattern.compile("(\\d+)([smhdwMy])")

    fun parseTimeString(input: String?): Duration {
        var duration = Duration.ZERO
        val matcher: Matcher = TIME_PATTERN.matcher(input)

        while (matcher.find()) {
            val value: Int = matcher.group(1).toInt()
            val unit: String = matcher.group(2)

            duration = when (unit) {
                "s" -> duration.plusSeconds(value.toLong())
                "m" -> duration.plusMinutes(value.toLong())
                "h" -> duration.plusHours(value.toLong())
                "d" -> duration.plusDays(value.toLong())
                "w" -> duration.plusDays((value * 7).toLong())
                "M" -> duration.plusDays((value * 30).toLong())
                "y" -> duration.plusDays((value * 365).toLong())
                else -> throw IllegalArgumentException("Ungültige Einheit: $unit")
            }
        }
        return duration
    }
}