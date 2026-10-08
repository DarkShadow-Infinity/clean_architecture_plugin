package org.clean.architecture.generator

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.file.Files
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GeneratorTest : BasePlatformTestCase() {

    @Test
    fun existingDirectoryStopsGenerationBeforeWritingChildren() {
        withVirtualRoot { root ->
            inWriteAction {
                root.createChildDirectory(this@GeneratorTest, "existing")
            }

            val result = inWriteAction {
                Generator.createTree(
                    project = project,
                    folder = root,
                    roots = listOf(
                        DirectorySpec("existing", listOf(DirectorySpec("child"))),
                        DirectorySpec("new")
                    )
                )
            }

            assertFalse(result)
            assertNotNull(root.findChild("existing"))
            assertNull(root.findChild("existing")?.findChild("child"))
            assertNull(root.findChild("new"))
        }
    }

    @Test
    fun failedCreationRollsBackDirectoriesCreatedEarlierInTheTransaction() {
        withVirtualRoot { root ->
            val result = inWriteAction {
                Generator.createTree(
                    project = project,
                    folder = root,
                    roots = listOf(
                        DirectorySpec("created-before-failure"),
                        DirectorySpec("invalid/name")
                    )
                )
            }

            assertFalse(result)
            assertNull(root.findChild("created-before-failure"))
            assertNull(root.findChild("invalid"))
        }
    }

    private fun withVirtualRoot(block: (VirtualFile) -> Unit) {
        val path = Files.createTempDirectory("clean-architecture-generator-test")
        try {
            val root = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile())
                ?: error("Temporary VFS root was not found: $path")
            block(root)
        } finally {
            path.toFile().deleteRecursively()
        }
    }

    private fun <T> inWriteAction(action: () -> T): T =
        ApplicationManager.getApplication().runWriteAction(Computable { action() })
}
