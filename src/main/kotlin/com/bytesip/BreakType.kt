package com.bytesip

import java.time.Duration

/**
 * The three reminder categories ByteSip cycles through.
 *
 * Each value carries:
 *  - a [title] shown as the notification headline,
 *  - an [interval] that controls how often the reminder fires.
 */
enum class BreakType(val title: String, val interval: Duration, val icon: String) {
    WATER(title = "ByteSip — Water break", interval = Duration.ofMinutes(25), icon = "💧"),
    EYES(title  = "ByteSip — Eye break",   interval = Duration.ofMinutes(50), icon = "👀"),
    MOVE(title  = "ByteSip — Move break",  interval = Duration.ofMinutes(90), icon = "🚶");
}
