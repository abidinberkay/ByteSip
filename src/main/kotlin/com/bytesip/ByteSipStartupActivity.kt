package com.bytesip

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * Boots [BreakScheduler] the first time a project finishes loading.
 *
 * Registered as a `postStartupActivity` in plugin.xml, so it runs automatically
 * when IntelliJ opens a project. The scheduler itself is idempotent — opening
 * more projects will not stack duplicate timers.
 */
class ByteSipStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        BreakScheduler.instance.startFor(project)
    }
}
