package org.isen.carburmap.lib.filedl

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.io.File

internal class FileDownloaderTest {
    @Test
    @Order(1)
    fun download(): Unit {
        val path = "xml/PrixCarburants_instantane.zip"
        val link = "https://donnees.roulez-eco.fr/opendata/instantane"
        FileDownloader.download(path, link)
        val file = File("build/resources/main/$path")
        assertTrue(file.exists())
    }

    @Test
    @Order(2)
    fun unzip() {
        val srcZip = "xml/PrixCarburants_instantane.zip"
        val destDir = "xml/"
        FileDownloader.unzip(srcZip, destDir)
        val file = File("build/resources/main/$destDir/PrixCarburants_instantane.xml")
        assertTrue(file.exists())
    }

    @Test
    @Order(3)
    fun delete(): Unit {
        val path = "xml/PrixCarburants_instantane.xmld"
        FileDownloader.delete(path)
        val file = File("build/resources/main/$path")
        assertFalse(file.exists())
    }
}