package org.isen.carburmap.lib.icon

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class IconManagerTest {

    @Test
    fun getIcon() {
        val icon = IconManager.getInstance().getIcon("/img/missing.png")
        assertEquals("missing.png", icon.path)
        assertEquals("/img/missing.png", icon.name)
    }

    @Test
    fun getSimpleIcon() {
        val icon = IconManager.getInstance().getSimpleIcon("/img/missing.png", 32)
        assertEquals("missing.png", icon.path)
        assertEquals("/img/missing.png", icon.name)
        assertEquals(32, icon.size)
    }
}