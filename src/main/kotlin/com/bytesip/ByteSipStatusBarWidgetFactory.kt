package com.bytesip

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class ByteSipStatusBarWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String = ByteSipStatusBarWidget.ID
    override fun getDisplayName(): String = "ByteSip Reminders"
    override fun isAvailable(project: Project): Boolean = true
    override fun createWidget(project: Project): StatusBarWidget = ByteSipStatusBarWidget(project)
    override fun disposeWidget(widget: StatusBarWidget) { widget.dispose() }
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}
