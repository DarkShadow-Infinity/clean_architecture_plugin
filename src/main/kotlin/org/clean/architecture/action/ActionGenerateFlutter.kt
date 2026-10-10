/*
 * Copyright: Copyright (c) 2025 Diego Palomares <>
 * License: GPL-3
 * Last Edited: 16.04.125, 23:26
 */

package org.clean.architecture.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import org.clean.architecture.generator.Generator
import org.clean.architecture.generator.GenerationPlan
import org.clean.architecture.settings.CleanArchitectureSettings
import org.clean.architecture.ui.ArchitectureStyle
import org.clean.architecture.ui.FeatureDialog
import org.clean.architecture.ui.Notifier

/**
 * Flutter action in the context menu
 *
 * This class will call the dialog and generate the Flutter Clean-Architecture structure
 */
class ActionGenerateFlutter : AnAction() {
    /**
     * Is called by the context action menu entry with an [actionEvent]
     */
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val dialog = FeatureDialog(actionEvent.project)
        if (dialog.showAndGet()) {
            generate(actionEvent.dataContext, dialog.getName(), dialog.splitSource(), dialog.getArchitectureStyle())
        }
    }

    /**
     * Generates the Flutter Clean-Architecture structure in a [dataContext].
     * If a [root] String is provided, it will create the structure in a new folder.
     * The [style] preset defines layer names and any extra directories.
     */
    private fun generate(dataContext: DataContext, root: String?, splitSource: Boolean, style: ArchitectureStyle) {
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val selected = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return
        val folder = if (selected.isDirectory) selected else selected.parent ?: run {
            Notifier.error(project, "The selected file has no parent directory")
            return
        }

        val settings = CleanArchitectureSettings.getInstance()
        val plan = try {
            GenerationPlan.build(
                root = root,
                style = style,
                settings = settings.state,
                splitSource = splitSource
            )
        } catch (e: IllegalArgumentException) {
            Notifier.error(project, e.message ?: "Invalid directory configuration")
            return
        }

        WriteCommandAction.runWriteCommandAction(project) {
            Generator.createTree(project, folder, plan)
        }
    }
}
