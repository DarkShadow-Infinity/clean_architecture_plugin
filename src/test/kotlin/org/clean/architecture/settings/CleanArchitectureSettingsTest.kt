package org.clean.architecture.settings

import kotlin.test.Test
import kotlin.test.assertEquals

class CleanArchitectureSettingsTest {

    @Test
    fun normalizedStateAppliesDefaultsAndCanonicalizesCustomDirectories() {
        val normalized = CleanArchitectureSettings.State(
            domainLayerName = "  ",
            customDirectories = " widgets, utils, widgets "
        ).normalized()

        assertEquals("domain", normalized.domainLayerName)
        assertEquals("widgets,utils", normalized.customDirectories)
    }
}
