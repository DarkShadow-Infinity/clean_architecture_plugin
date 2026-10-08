package org.clean.architecture.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.clean.architecture.settings.CleanArchitectureSettings
import org.clean.architecture.ui.ArchitectureStyle

class GenerationPlanTest {

    @Test
    fun defaultPlanKeepsTheExistingDirectoryStructure() {
        val plan = GenerationPlan.build(
            root = "feature_name",
            style = ArchitectureStyle.DEFAULT,
            settings = CleanArchitectureSettings.State(),
            splitSource = false
        )

        assertEquals(
            listOf(
                "feature_name",
                "feature_name/data",
                "feature_name/data/repositories",
                "feature_name/data/data_sources",
                "feature_name/data/models",
                "feature_name/domain",
                "feature_name/domain/repositories",
                "feature_name/domain/use_cases",
                "feature_name/domain/entities",
                "feature_name/presentation",
                "feature_name/presentation/manager",
                "feature_name/presentation/pages",
                "feature_name/presentation/widgets"
            ),
            plan.paths()
        )
    }

    @Test
    fun customPlanUsesRenamedLayersAndAdditionalDirectories() {
        val settings = CleanArchitectureSettings.State(
            domainLayerName = "core",
            dataLayerName = "storage",
            presentationLayerName = "ui",
            domainUseCasesName = "usecases",
            customDirectories = "common, utils"
        )

        val plan = GenerationPlan.build(
            root = null,
            style = ArchitectureStyle.CUSTOM,
            settings = settings,
            splitSource = false
        )

        assertEquals(
            listOf(
                "storage",
                "storage/repositories",
                "storage/data_sources",
                "storage/models",
                "core",
                "core/repositories",
                "core/usecases",
                "core/entities",
                "ui",
                "ui/manager",
                "ui/pages",
                "ui/widgets",
                "common",
                "utils"
            ),
            plan.paths()
        )
    }

    @Test
    fun splitPlanCreatesLocalAndRemoteDataSources() {
        val plan = GenerationPlan.build(
            root = "feature",
            style = ArchitectureStyle.DEFAULT,
            settings = CleanArchitectureSettings.State(),
            splitSource = true
        )

        assertEquals(
            listOf(
                "feature",
                "feature/data",
                "feature/data/repositories",
                "feature/data/local",
                "feature/data/local/models",
                "feature/data/local/data_sources",
                "feature/data/remote",
                "feature/data/remote/models",
                "feature/data/remote/data_sources",
                "feature/domain",
                "feature/domain/repositories",
                "feature/domain/use_cases",
                "feature/domain/entities",
                "feature/presentation",
                "feature/presentation/manager",
                "feature/presentation/pages",
                "feature/presentation/widgets"
            ),
            plan.paths()
        )
    }

    @Test
    fun invalidNamesAreRejectedBeforeGeneration() {
        val settings = CleanArchitectureSettings.State(customDirectories = "common, ../outside")

        assertFailsWith<IllegalArgumentException> {
            GenerationPlan.build(
                root = "feature",
                style = ArchitectureStyle.CUSTOM,
                settings = settings,
                splitSource = false
            )
        }
    }

    @Test
    fun conflictingTopLevelNamesAreRejectedBeforeGeneration() {
        val settings = CleanArchitectureSettings.State(
            domainLayerName = "data"
        )

        assertFailsWith<IllegalArgumentException> {
            GenerationPlan.build(
                root = null,
                style = ArchitectureStyle.CUSTOM,
                settings = settings,
                splitSource = false
            )
        }
    }

    @Test
    fun platformReservedCharactersAreRejectedBeforeGeneration() {
        val settings = CleanArchitectureSettings.State(customDirectories = "shared:utils")

        assertFailsWith<IllegalArgumentException> {
            GenerationPlan.build(
                root = null,
                style = ArchitectureStyle.CUSTOM,
                settings = settings,
                splitSource = false
            )
        }
    }

    private fun List<DirectorySpec>.paths(prefix: String = ""): List<String> = flatMap { spec ->
        val path = if (prefix.isEmpty()) spec.name else "$prefix/${spec.name}"
        listOf(path) + spec.children.paths(path)
    }
}
