package org.clean.architecture.generator

import org.clean.architecture.settings.CleanArchitectureSettings
import org.clean.architecture.settings.normalized
import org.clean.architecture.ui.ArchitectureStyle

data class DirectorySpec(
    val name: String,
    val children: List<DirectorySpec> = emptyList()
)

object GenerationPlan {

    fun build(
        root: String?,
        style: ArchitectureStyle,
        settings: CleanArchitectureSettings.State,
        splitSource: Boolean
    ): List<DirectorySpec> {
        val normalizedSettings = settings.normalized()
        val data = buildDataLayer(
            layerName = style.dataName ?: normalizedSettings.dataLayerName,
            settings = normalizedSettings,
            splitSource = splitSource
        )
        val domain = DirectorySpec(
            name = directoryName(style.domainName ?: normalizedSettings.domainLayerName, "domain layer"),
            children = listOf(
                DirectorySpec(directoryName(normalizedSettings.domainRepositoriesName, "domain repositories")),
                DirectorySpec(directoryName(normalizedSettings.domainUseCasesName, "domain use cases")),
                DirectorySpec(directoryName(normalizedSettings.domainEntitiesName, "domain entities"))
            )
        )
        val presentation = DirectorySpec(
            name = directoryName(
                style.presentationName ?: normalizedSettings.presentationLayerName,
                "presentation layer"
            ),
            children = listOf(
                DirectorySpec(directoryName(normalizedSettings.presentationManagerName, "presentation manager")),
                DirectorySpec(directoryName(normalizedSettings.presentationPagesName, "presentation pages")),
                DirectorySpec(directoryName(normalizedSettings.presentationWidgetsName, "presentation widgets"))
            )
        )
        validateUniqueNames(domain.children, "domain directories")
        validateUniqueNames(presentation.children, "presentation directories")
        val customDirectories = normalizedSettings.customDirectories
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { DirectorySpec(directoryName(it, "custom directory")) }

        val roots = listOf(data, domain, presentation) +
            style.extraDirectories.map { DirectorySpec(directoryName(it, "style directory")) } +
            customDirectories
        validateUniqueNames(roots, "top-level directories")

        val rootDirectory = root?.trim()?.takeIf { it.isNotEmpty() }?.let {
            DirectorySpec(directoryName(it, "feature root"), roots)
        }
        return rootDirectory?.let(::listOf) ?: roots
    }

    private fun buildDataLayer(
        layerName: String,
        settings: CleanArchitectureSettings.State,
        splitSource: Boolean
    ): DirectorySpec {
        val dataChildren = if (splitSource) {
            listOf(
                DirectorySpec(directoryName(settings.dataRepositoriesName, "data repositories")),
                DirectorySpec(
                    "local",
                    listOf(
                        DirectorySpec(directoryName(settings.dataModelsName, "local models")),
                        DirectorySpec(directoryName(settings.dataDataSourcesName, "local data sources"))
                    )
                ),
                DirectorySpec(
                    "remote",
                    listOf(
                        DirectorySpec(directoryName(settings.dataModelsName, "remote models")),
                        DirectorySpec(directoryName(settings.dataDataSourcesName, "remote data sources"))
                    )
                )
            )
        } else {
            listOf(
                DirectorySpec(directoryName(settings.dataRepositoriesName, "data repositories")),
                DirectorySpec(directoryName(settings.dataDataSourcesName, "data sources")),
                DirectorySpec(directoryName(settings.dataModelsName, "data models"))
            )
        }

        validateUniqueNames(dataChildren, "data directories")
        dataChildren.forEach { validateUniqueNames(it.children, "${it.name} directories") }
        return DirectorySpec(directoryName(layerName, "data layer"), dataChildren)
    }

    private fun directoryName(value: String, description: String): String {
        val name = value.trim()
        require(name.isNotEmpty()) { "$description cannot be blank" }
        require(name != "." && name != "..") { "$description cannot be a relative path" }
        require(name.none {
            it == '/' || it == '\\' || it.isISOControl() || it in INVALID_DIRECTORY_CHARACTERS
        }) {
            "$description must be a single directory name"
        }
        require(name.last() !in charArrayOf('.', ' ')) {
            "$description cannot end with a dot or space"
        }
        return name
    }

    private fun validateUniqueNames(specs: List<DirectorySpec>, description: String) {
        val duplicates = specs.groupingBy { it.name }.eachCount()
            .filterValues { it > 1 }
            .keys
        require(duplicates.isEmpty()) {
            "Duplicate names in $description: ${duplicates.joinToString()}"
        }
    }

    private val INVALID_DIRECTORY_CHARACTERS = "<>:\"|?*".toSet()
}
