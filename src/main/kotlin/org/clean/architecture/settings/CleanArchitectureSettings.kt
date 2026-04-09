/*
 * Copyright: Copyright (c) 2025 Diego Palomares <>
 * License: GPL-3
 */

package org.clean.architecture.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * Persistent settings for Clean Architecture Generator.
 * Stores user preferences for layer names and custom directories.
 */
@Service(Service.Level.APP)
@State(
    name = "CleanArchitectureSettings",
    storages = [Storage("CleanArchitectureSettings.xml")]
)
class CleanArchitectureSettings : PersistentStateComponent<CleanArchitectureSettings.State> {

    private var myState = State()

    /**
     * State class holding all configurable settings
     */
    data class State(
        var domainLayerName: String = "domain",
        var dataLayerName: String = "data",
        var presentationLayerName: String = "presentation",
        var customDirectories: String = "",  // Comma-separated list
        
        // Domain layer subdirectories
        var domainRepositoriesName: String = "repositories",
        var domainUseCasesName: String = "use_cases",
        var domainEntitiesName: String = "entities",
        
        // Data layer subdirectories
        var dataRepositoriesName: String = "repositories",
        var dataDataSourcesName: String = "data_sources",
        var dataModelsName: String = "models",
        
        // Presentation layer subdirectories
        var presentationManagerName: String = "manager",
        var presentationPagesName: String = "pages",
        var presentationWidgetsName: String = "widgets"
    )

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    /**
     * Returns the list of custom directories parsed from the comma-separated string
     */
    fun getCustomDirectoriesList(): List<String> {
        return myState.customDirectories
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    companion object {
        /**
         * Gets the singleton instance of the settings
         */
        fun getInstance(): CleanArchitectureSettings {
            return ApplicationManager.getApplication().getService(CleanArchitectureSettings::class.java)
        }
    }
}
