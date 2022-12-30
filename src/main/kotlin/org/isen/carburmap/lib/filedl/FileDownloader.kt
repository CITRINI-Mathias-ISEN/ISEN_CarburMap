package org.isen.carburmap.lib.filedl

import java.nio.file.Path
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.io.IOException

class FileDownloader {
    companion object {
        fun a(name : String, link : String) {
            val url = URL(link)
            val path = Paths.get("src/main/resources/$name")
            try {
                Files.delete(path)
                println("Deletion succeeded.")
            } catch (e: IOException) {
                println("Deletion failed.")
                //e.printStackTrace()
            }
            try {
                url.openStream().use { Files.copy(it, path) }
            }
            catch (e: Exception) {
                println("Error: $e")
            }
        }
    }
}