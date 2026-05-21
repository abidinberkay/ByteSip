package com.bytesip

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import java.awt.Component
import java.awt.event.MouseEvent

class ByteSipStatusBarWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {

    override fun ID(): String = ID
    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this
    override fun install(bar: StatusBar) {}
    override fun dispose() {}

    override fun getText(): String =
        BreakType.entries.joinToString("   ") { "${it.icon} ${it.interval.toMinutes()}" }

    override fun getTooltipText(): String =
        BreakType.entries.joinToString("\n") { type ->
            "${type.icon}  ${type.title.removePrefix("ByteSip — ")} — every ${type.interval.toMinutes()} min"
        }

    override fun getClickConsumer(): Consumer<MouseEvent>? = null
    override fun getAlignment(): Float = Component.LEFT_ALIGNMENT

    companion object {
        const val ID = "ByteSipStatusBar"
    }
}
