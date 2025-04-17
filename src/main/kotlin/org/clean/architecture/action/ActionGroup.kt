/*
 * Copyright: Copyright (c) 2025 Diego Palomares <>
 * License: GPL-3
 * Last Edited: 16.04.125, 23:26
 */

package org.clean.architecture.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DefaultActionGroup
import groovyjarjarantlr4.v4.runtime.misc.NotNull


/**
 * Defines the ActionGroup to be only visible when parent is a [PSI_ELEMENT]
 */
class ActionGroup : DefaultActionGroup() {
    override fun update(event: AnActionEvent) {
        // Enable/disable depending on whether user is editing
        val psi = event.getData(CommonDataKeys.PSI_ELEMENT)
        //event.presentation.isEnabled = project != null
        // Always make visible.
        event.presentation.isVisible = psi != null
        // Take this opportunity to set an icon for the menu entry.
        event.presentation.icon = AllIcons.Actions.AddDirectory
    }

    @NotNull
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}