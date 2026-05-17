package com.bytesip

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.AppExecutorUtil
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Application-level service that schedules ByteSip break reminders.
 *
 * One [ScheduledFuture] per [BreakType] runs at that type's interval. When a
 * timer fires it dispatches to the IntelliJ EDT and asks
 * [ByteSipNotificationService] to show the balloon.
 *
 * The scheduler is lazy: the first call to [startFor] kicks everything off.
 * Subsequent calls are no-ops, so opening additional projects does not stack
 * duplicate timers.
 */
@Service(Service.Level.APP)
class BreakScheduler : Disposable {

    private val log = Logger.getInstance(BreakScheduler::class.java)

    // One running task per break type. ConcurrentHashMap so snooze can swap
    // entries safely from any thread.
    private val tasks = ConcurrentHashMap<BreakType, ScheduledFuture<*>>()

    @Volatile
    private var started = false

    /** Start the regular schedule. Idempotent. */
    fun startFor(project: Project) {
        if (started) return
        synchronized(this) {
            if (started) return
            log.info("ByteSip: Starting scheduler")
            BreakType.entries.forEach { type -> scheduleRegular(project, type) }
            started = true
        }
    }

    /**
     * Snooze [type] for [duration] — cancel its current task and re-arm it once
     * after the snooze interval. Once it fires, regular cadence resumes.
     */
    fun snooze(project: Project, type: BreakType, duration: Duration) {
        tasks[type]?.cancel(false)
        val future = AppExecutorUtil.getAppScheduledExecutorService().schedule(
            { fire(project, type) },
            duration.toMillis(),
            TimeUnit.MILLISECONDS,
        )
        tasks[type] = future
    }

    /** Re-arm [type] at its standard cadence. */
    private fun scheduleRegular(project: Project, type: BreakType) {
        tasks[type]?.cancel(false)
        val intervalMinutes = type.interval.toMinutes()
        log.info("ByteSip: Scheduled $type to fire every $intervalMinutes minutes")
        val future = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
            { fire(project, type) },
            intervalMinutes,
            intervalMinutes,
            TimeUnit.MINUTES,
        )
        tasks[type] = future
    }

    /**
     * Show the balloon for [type]. After a one-shot (snoozed) fire, restore the
     * regular cadence so reminders keep flowing.
     */
    private fun fire(project: Project, type: BreakType) {
        if (project.isDisposed) return
        log.info("ByteSip: Firing reminder for $type")
        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                ByteSipNotificationService.showBreakReminder(project, type)
            }
        }
        // If the current scheduled future is a one-shot (from snooze), replace
        // it with the regular fixed-delay task. scheduleWithFixedDelay futures
        // are not "done" until cancelled, so this only matches one-shots.
        val current = tasks[type]
        if (current != null && current.isDone) {
            log.info("ByteSip: Restoring regular cadence for $type after snooze")
            scheduleRegular(project, type)
        }
    }

    override fun dispose() {
        tasks.values.forEach { it.cancel(false) }
        tasks.clear()
    }

    companion object {
        val instance: BreakScheduler
            get() = ApplicationManager.getApplication().getService(BreakScheduler::class.java)
    }
}
