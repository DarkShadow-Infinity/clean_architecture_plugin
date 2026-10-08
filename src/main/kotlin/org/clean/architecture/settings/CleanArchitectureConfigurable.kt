/*
 * Copyright: Copyright (c) 2025 Diego Palomares <>
 * License: GPL-3
 */

package org.clean.architecture.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSeparator

/**
 * Settings configurable for Clean Architecture Generator.
 * Provides a UI panel in Settings > Tools > Clean Architecture.
 */
class CleanArchitectureConfigurable : Configurable {

    private var settingsPanel: JPanel? = null
    
    // Layer name fields
    private val domainLayerField = JBTextField()
    private val dataLayerField = JBTextField()
    private val presentationLayerField = JBTextField()
    
    // Custom directories field
    private val customDirectoriesField = JBTextField()
    
    // Domain subdirectory fields
    private val domainRepositoriesField = JBTextField()
    private val domainUseCasesField = JBTextField()
    private val domainEntitiesField = JBTextField()
    
    // Data subdirectory fields
    private val dataRepositoriesField = JBTextField()
    private val dataDataSourcesField = JBTextField()
    private val dataModelsField = JBTextField()
    
    // Presentation subdirectory fields
    private val presentationManagerField = JBTextField()
    private val presentationPagesField = JBTextField()
    private val presentationWidgetsField = JBTextField()

    override fun getDisplayName(): String = "Clean Architecture"

    override fun createComponent(): JComponent {
        settingsPanel = FormBuilder.createFormBuilder()
            // Main layer names section
            .addComponent(JBLabel("<html><b>Layer Names</b></html>"))
            .addLabeledComponent(JBLabel("Domain layer:"), domainLayerField, 1, false)
            .addLabeledComponent(JBLabel("Data layer:"), dataLayerField, 1, false)
            .addLabeledComponent(JBLabel("Presentation layer:"), presentationLayerField, 1, false)
            .addComponent(JSeparator())
            
            // Domain subdirectories section
            .addComponent(JBLabel("<html><b>Domain Layer Subdirectories</b></html>"))
            .addLabeledComponent(JBLabel("Repositories:"), domainRepositoriesField, 1, false)
            .addLabeledComponent(JBLabel("Use cases:"), domainUseCasesField, 1, false)
            .addLabeledComponent(JBLabel("Entities:"), domainEntitiesField, 1, false)
            .addComponent(JSeparator())
            
            // Data subdirectories section
            .addComponent(JBLabel("<html><b>Data Layer Subdirectories</b></html>"))
            .addLabeledComponent(JBLabel("Repositories:"), dataRepositoriesField, 1, false)
            .addLabeledComponent(JBLabel("Data sources:"), dataDataSourcesField, 1, false)
            .addLabeledComponent(JBLabel("Models:"), dataModelsField, 1, false)
            .addComponent(JSeparator())
            
            // Presentation subdirectories section
            .addComponent(JBLabel("<html><b>Presentation Layer Subdirectories</b></html>"))
            .addLabeledComponent(JBLabel("Manager:"), presentationManagerField, 1, false)
            .addLabeledComponent(JBLabel("Pages:"), presentationPagesField, 1, false)
            .addLabeledComponent(JBLabel("Widgets:"), presentationWidgetsField, 1, false)
            .addComponent(JSeparator())
            
            // Custom directories section
            .addComponent(JBLabel("<html><b>Custom Directories</b></html>"))
            .addLabeledComponent(
                JBLabel("Additional directories:"),
                customDirectoriesField,
                1,
                false
            )
            .addComponent(JBLabel("<html><i>Comma-separated list (e.g., widgets, utils, common)</i></html>"))
            
            .addComponentFillVertically(JPanel(), 0)
            .panel

        reset()
        return settingsPanel!!
    }

    override fun isModified(): Boolean {
        val settings = CleanArchitectureSettings.getInstance()
        val state = settings.state
        
        return domainLayerField.text != state.domainLayerName ||
                dataLayerField.text != state.dataLayerName ||
                presentationLayerField.text != state.presentationLayerName ||
                customDirectoriesField.text != state.customDirectories ||
                domainRepositoriesField.text != state.domainRepositoriesName ||
                domainUseCasesField.text != state.domainUseCasesName ||
                domainEntitiesField.text != state.domainEntitiesName ||
                dataRepositoriesField.text != state.dataRepositoriesName ||
                dataDataSourcesField.text != state.dataDataSourcesName ||
                dataModelsField.text != state.dataModelsName ||
                presentationManagerField.text != state.presentationManagerName ||
                presentationPagesField.text != state.presentationPagesName ||
                presentationWidgetsField.text != state.presentationWidgetsName
    }

    override fun apply() {
        val settings = CleanArchitectureSettings.getInstance()
        settings.loadState(
            settings.state.copy(
                domainLayerName = domainLayerField.text,
                dataLayerName = dataLayerField.text,
                presentationLayerName = presentationLayerField.text,
                customDirectories = customDirectoriesField.text,
                domainRepositoriesName = domainRepositoriesField.text,
                domainUseCasesName = domainUseCasesField.text,
                domainEntitiesName = domainEntitiesField.text,
                dataRepositoriesName = dataRepositoriesField.text,
                dataDataSourcesName = dataDataSourcesField.text,
                dataModelsName = dataModelsField.text,
                presentationManagerName = presentationManagerField.text,
                presentationPagesName = presentationPagesField.text,
                presentationWidgetsName = presentationWidgetsField.text
            ).normalized()
        )
        reset()
    }

    override fun reset() {
        val settings = CleanArchitectureSettings.getInstance()
        val state = settings.state
        
        domainLayerField.text = state.domainLayerName
        dataLayerField.text = state.dataLayerName
        presentationLayerField.text = state.presentationLayerName
        customDirectoriesField.text = state.customDirectories
        
        domainRepositoriesField.text = state.domainRepositoriesName
        domainUseCasesField.text = state.domainUseCasesName
        domainEntitiesField.text = state.domainEntitiesName
        
        dataRepositoriesField.text = state.dataRepositoriesName
        dataDataSourcesField.text = state.dataDataSourcesName
        dataModelsField.text = state.dataModelsName
        
        presentationManagerField.text = state.presentationManagerName
        presentationPagesField.text = state.presentationPagesName
        presentationWidgetsField.text = state.presentationWidgetsName
    }
}
