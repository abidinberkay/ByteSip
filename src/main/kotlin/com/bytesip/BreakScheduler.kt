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

@Service(Service.Level.APP)
class BreakScheduler : Disposable {

    private val log = Logger.getInstance(BreakScheduler::class.java)

    private val tasks = ConcurrentHashMap<BreakType, ScheduledFuture<*>>()

    // Tracks which types are waiting on a one-shot snooze so fire() can restore regular cadence.
    private val snoozedTypes = ConcurrentHashMap.newKeySet<BreakType>()

    @Volatile
    private var started = false

    fun startFor(project: Project) {
        if (started) return
        synchronized(this) {
            if (started) return
            log.info("ByteSip: Starting scheduler")
            BreakType.entries.forEach { type -> scheduleRegular(project, type) }
            started = true
        }
    }

    fun snooze(project: Project, type: BreakType, duration: Duration) {
        tasks[type]?.cancel(false)
        snoozedTypes.add(type)
        val future = AppExecutorUtil.getAppScheduledExecutorService().schedule(
            { fire(project, type) },
            duration.toMillis(),
            TimeUnit.MILLISECONDS,
        )
        tasks[type] = future
    }

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

    private fun fire(project: Project, type: BreakType) {
        try {
            if (project.isDisposed) return
            log.info("ByteSip: Firing reminder for $type")
            ApplicationManager.getApplication().invokeLater {
                if (!project.isDisposed) {
                    ByteSipNotificationService.showBreakReminder(project, type)
                }
            }
            // snoozedTypes.remove returns true only for one-shot snooze fires — restore regular cadence.
            // Regular scheduleWithFixedDelay fires never add to snoozedTypes, so this is a no-op for them.
            if (snoozedTypes.remove(type)) {
                log.info("ByteSip: Restoring regular cadence for $type after snooze")
                scheduleRegular(project, type)
            }
        } catch (e: Exception) {
            log.error("ByteSip: Error firing reminder for $type", e)
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
