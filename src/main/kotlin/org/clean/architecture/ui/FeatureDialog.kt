/*
 * Copyright: Copyright (c) 2025 Diego Palomares <>
 * License: GPL-3
 * Last Edited: 16.04.125, 23:26
 */

package org.clean.architecture.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Creates a [DialogWrapper] to get the feature name and architecture style.
 * Replaces the previous .form-based implementation with a fully programmatic UI.
 */
class FeatureDialog(project: Project?) : DialogWrapper(project) {

    private val nameTextField = JBTextField().apply {
        toolTipText = "Feature name goes here (e.g. number_trivia). Leave blank to create in current directory."
    }

    private val styleComboBox = ComboBox(ArchitectureStyle.entries.toTypedArray())

    private val splitDataSource = JBCheckBox("Split data_sources to local and remote").apply {
        toolTipText = "Check if you want the data → data_sources folder to be split for local and remote"
    }

    /**
     * @return feature name entered by the user, or null if blank
     */
    fun getName(): String? = nameTextField.text.trim().takeIf { it.isNotEmpty() }

    /**
     * @return whether data sources should be split into local/remote
     */
    fun splitSource(): Boolean = splitDataSource.isSelected

    /**
     * @return the architecture style selected by the user
     */
    fun getArchitectureStyle(): ArchitectureStyle = styleComboBox.item

    override fun createCenterPanel(): JComponent {
        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Feature name:"), nameTextField, 1, false)
            .addVerticalGap(8)
            .addLabeledComponent(JBLabel("Architecture style:"), styleComboBox, 1, false)
            .addVerticalGap(8)
            .addComponent(splitDataSource)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun getPreferredFocusedComponent(): JComponent = nameTextField

    init {
        init()
        title = "Clean Architecture Generator"
    }
}