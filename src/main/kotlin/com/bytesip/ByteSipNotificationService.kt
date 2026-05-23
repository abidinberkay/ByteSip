package com.bytesip

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.time.Duration

/**
 * Builds and shows ByteSip's balloon notifications.
 *
 * All notifications:
 *  - use the "ByteSip" BALLOON notification group registered in plugin.xml,
 *  - are non-blocking (no modal dialogs),
 *  - expose a "Done" and a "Snooze 1 min" action.
 *
 * The class is stateless — methods receive everything they need as parameters.
 */
object ByteSipNotificationService {

    private val log = Logger.getInstance(ByteSipNotificationService::class.java)
    private const val GROUP_ID = "ByteSip"
    private val SNOOZE_DURATION = Duration.ofMinutes(1)

    /**
     * Show a reminder for [type] inside [project].
     *
     * The message body comes from [BreakMessages]; the uppercase action line is
     * appended as a second line in the balloon content.
     */
    fun showBreakReminder(project: Project, type: BreakType) {
        val message = BreakMessages.random(type)

        // Two-line content: funny body + uppercase action cue.
        val content = "${message.body}\n<b>${message.action}</b>"

        val notification: Notification = NotificationGroupManager.getInstance()
            .getNotificationGroup(GROUP_ID)
            .createNotification(type.title, content, NotificationType.INFORMATION)

        // "Done" simply dismisses the balloon — the regular schedule continues.
        notification.addAction(object : NotificationAction("Done") {
            override fun actionPerformed(e: AnActionEvent, n: Notification) {
                log.info("ByteSip: Done clicked for $type")
                n.expire()
            }
        })

        // "Snooze 1 min" re-shows the same break type after a short delay.
        notification.addAction(object : NotificationAction("Snooze 1 min") {
            override fun actionPerformed(e: AnActionEvent, n: Notification) {
                log.info("ByteSip: Snooze clicked for $type — next fire in ${SNOOZE_DURATION.seconds} seconds")
                n.expire()
                BreakScheduler.instance.snooze(project, type, SNOOZE_DURATION)
            }
        })

        notification.notify(project)
    }
}
