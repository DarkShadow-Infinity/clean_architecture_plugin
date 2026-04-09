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
import com.intellij.openapi.vfs.VirtualFile
import org.clean.architecture.generator.Generator
import org.clean.architecture.settings.CleanArchitectureSettings
import org.clean.architecture.ui.ArchitectureStyle
import org.clean.architecture.ui.FeatureDialog

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

        val settings = CleanArchitectureSettings.getInstance()
        val state = settings.state

        // Resolve layer names: preset value takes priority, falls back to Settings when null
        val domainName       = style.domainName       ?: state.domainLayerName
        val dataName         = style.dataName         ?: state.dataLayerName
        val presentationName = style.presentationName ?: state.presentationLayerName

        var folder = if (selected.isDirectory) selected else selected.parent
        WriteCommandAction.runWriteCommandAction(project) {
            if (!root.isNullOrBlank()) {
                val result = Generator.createFolder(
                    project, folder, root
                ) ?: return@runWriteCommandAction
                folder = result[root]
            }

            // Generate data layer
            if (splitSource) {
                val mapOrFalse = Generator.createFolder(
                    project, folder,
                    dataName,
                    state.dataRepositoriesName
                ) ?: return@runWriteCommandAction
                mapOrFalse[dataName]?.let { data: VirtualFile ->
                    Generator.createFolder(
                        project, data,
                        "local",
                        state.dataModelsName, state.dataDataSourcesName
                    )
                    Generator.createFolder(
                        project, data,
                        "remote",
                        state.dataModelsName, state.dataDataSourcesName
                    )
                }
            } else {
                Generator.createFolder(
                    project, folder,
                    dataName,
                    state.dataRepositoriesName, state.dataDataSourcesName, state.dataModelsName
                )
            }

            // Generate domain layer
            Generator.createFolder(
                project, folder,
                domainName,
                state.domainRepositoriesName, state.domainUseCasesName, state.domainEntitiesName
            )

            // Generate presentation layer
            Generator.createFolder(
                project, folder,
                presentationName,
                state.presentationManagerName, state.presentationPagesName, state.presentationWidgetsName
            )

            // Generate extra directories from selected architecture style
            for (extraDir in style.extraDirectories) {
                Generator.createFolder(project, folder, extraDir)
            }

            // Generate custom directories from Settings
            for (customDir in settings.getCustomDirectoriesList()) {
                Generator.createFolder(project, folder, customDir)
            }
        }
    }
}