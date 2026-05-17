package com.bytesip

import java.time.Duration

/**
 * The three reminder categories ByteSip cycles through.
 *
 * Each value carries:
 *  - a [title] shown as the notification headline,
 *  - an [interval] that controls how often the reminder fires.
 */
enum class BreakType(val title: String, val interval: Duration) {
    WATER(title = "ByteSip — Water break", interval = Duration.ofMinutes(2)),
    EYES(title = "ByteSip — Eye break", interval = Duration.ofMinutes(2)),
    MOVE(title = "ByteSip — Move break", interval = Duration.ofMinutes(2));
}
