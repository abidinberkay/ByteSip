package com.bytesip

/**
 * Static pools of funny English messages used by the notifications.
 *
 * Each entry is a [Message] containing the body text plus a short uppercase
 * action line (rendered as a secondary line inside the notification balloon).
 */
object BreakMessages {

    data class Message(val body: String, val action: String)

    private val water = listOf(
        Message("Code can wait. Kidneys cannot.", "DRINK WATER"),
        Message("Hydrate before your brain starts returning null.", "TAKE 3 SIPS"),
        Message("Your keyboard is thirsty for your attention, not your tears.", "SIP SOME WATER"),
        Message("Coffee is a lie your soul tells your bladder.", "GLASS OF WATER NOW"),
        Message("Even ducks drink water and they live in it.", "HYDRATE LIKE A DUCK"),
    )

    private val eyes = listOf(
        Message("Your eyes are too pretty for endless stack traces.", "CLOSE EYES 10 SECONDS"),
        Message("Look away for a moment. The bugs will survive.", "LOOK FAR AWAY"),
        Message("Pixels are not nutrients. Give your retinas a snack.", "FOCUS ON THE HORIZON"),
        Message("Twenty feet, twenty seconds, twenty thank-yous from your eyeballs.", "DO THE 20-20-20 RULE"),
        Message("Your monitor is not your therapist. Stop staring at it.", "BLINK INTENTIONALLY"),
    )

    private val move = listOf(
        Message("Stand up. Your chair is getting emotionally attached.", "STAND UP AND MOVE"),
        Message("Your posture is entering legacy support mode.", "ROLL YOUR SHOULDERS"),
        Message("If you sit any longer, IT will mistake you for a server.", "STRETCH YOUR LEGS"),
        Message("Your spine filed a bug report. Severity: critical.", "WALK FOR 2 MINUTES"),
        Message("Be the kernel of your own movement loop.", "DO 10 SQUATS"),
    )

    /** Pick a random message for the given [type]. */
    fun random(type: BreakType): Message = when (type) {
        BreakType.WATER -> water.random()
        BreakType.EYES -> eyes.random()
        BreakType.MOVE -> move.random()
    }
}
