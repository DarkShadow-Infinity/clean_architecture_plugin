/*
 * Copyright: Copyright (c) 2025 Diego Palomares <>
 * License: GPL-3
 */

package org.clean.architecture.ui

/**
 * Predefined architecture style presets selectable from the generation dialog.
 * - DEFAULT and CORE_UI use fixed layer names.
 * - CUSTOM reads layer names from Settings > Tools > Clean Architecture.
 * - Extra directories can be added at the top level for any style.
 */
enum class ArchitectureStyle(
    val label: String,
    val domainName: String?,
    val dataName: String?,
    val presentationName: String?,
    val extraDirectories: List<String> = emptyList()
) {
    DEFAULT(
        label = "Default Clean Architecture",
        domainName = "domain",
        dataName = "data",
        presentationName = "presentation"
    ),
    CORE_UI(
        label = "Core / UI / Widgets",
        domainName = "core",
        dataName = "data",
        presentationName = "ui",
        extraDirectories = listOf("widgets")
    ),
    CUSTOM(
        label = "Custom (from Settings)",
        domainName = null,  // Will use Settings value
        dataName = null,
        presentationName = null
    );

    override fun toString(): String = label
}
