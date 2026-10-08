/*
 * Copyright: Copyright (c) 2025 Diego Palomares <>
 * License: GPL-3
 * Last Edited: 16.04.125, 23:26
 */

package org.clean.architecture.generator

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.clean.architecture.ui.Notifier
import java.io.IOException

/**
 * Generator Factory to create structure
 */
interface Generator {
    companion object {
        private val logger = Logger.getInstance(Generator::class.java)

        fun createTree(
            project: Project?,
            folder: VirtualFile,
            roots: List<DirectorySpec>
        ): Boolean {
            val conflicts = roots.flatMap { findConflicts(folder, it) }
            if (conflicts.isNotEmpty()) {
                Notifier.warning(
                    project,
                    "Directories already exist: ${conflicts.take(MAX_REPORTED_CONFLICTS).joinToString()}"
                )
                return false
            }

            val created = mutableListOf<VirtualFile>()
            return try {
                roots.forEach { createTree(folder, it, created) }
                true
            } catch (e: IOException) {
                rollback(created)
                logger.warn("Couldn't create Clean Architecture directory tree", e)
                Notifier.error(project, "Couldn't create the Clean Architecture directory tree")
                false
            }
        }

        /**
         * Creates a [parent] folder and its [children] in a given [folder].
         * [project] is needed for the notifications if there is an error or a warning situation.
         * @return null if an error occurred or the a map of all virtual files created
         */
        fun createFolder(
            project: Project,
            folder: VirtualFile,
            parent: String,
            vararg children: String
        ): Map<String, VirtualFile>? {
            try {
                for (child in folder.children) {
                    if (child.name == parent) {
                        Notifier.warning(project, "Directory [$parent] already exists")
                        return null
                    }
                }
                val mapOfFolder = mutableMapOf<String, VirtualFile>()
                mapOfFolder[parent] = folder.createChildDirectory(folder, parent)
                for (child in children) {
                    mapOfFolder[child] =
                        mapOfFolder[parent]?.createChildDirectory(mapOfFolder[parent], child) ?: throw IOException()
                }
                return mapOfFolder
            } catch (e: IOException) {
                Notifier.warning(project, "Couldn't create $parent directory")
                logger.warn("Couldn't create $parent directory", e)
                return null
            }
        }

        private fun createTree(
            parent: VirtualFile,
            spec: DirectorySpec,
            created: MutableList<VirtualFile>
        ) {
            val directory = parent.createChildDirectory(parent, spec.name)
            created += directory
            spec.children.forEach { child -> createTree(directory, child, created) }
        }

        private fun findConflicts(parent: VirtualFile, spec: DirectorySpec): List<String> {
            val child = parent.children.firstOrNull { it.name == spec.name }
                ?: return emptyList()
            val path = spec.name
            return listOf(path) + spec.children.flatMap { findConflicts(child, it) }
                .map { "$path/$it" }
        }

        private fun rollback(created: List<VirtualFile>) {
            created.asReversed().forEach { directory ->
                if (directory.isValid) {
                    runCatching { directory.delete(Generator) }
                }
            }
        }

        private const val MAX_REPORTED_CONFLICTS = 5
    }
}
