package org.isen.carburmap.lib.filedl

import java.nio.file.Path
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.io.IOException
import java.util.zip.ZipFile
import javax.print.attribute.standard.Destination

class FileDownloader {
    companion object {
        /**
         * Download a file from a URL to a destination path
         * @param path the destination path of the file in "/ressources/" folder ( ex: "xml/PrixCarburants_instantane.zip" )
         * @param url the URL of the file to download
         */
        fun download(path : String, link : String) {
            val url = URL(link)
            val filePath = Paths.get("src/main/resources/$path")
            this.delete("$path")
            try {
                url.openStream().use { Files.copy(it, filePath) }
            }
            catch (e: Exception) {
                println("Error: $e")
            }
        }

        /**
         * Unzip a file
         * @param srcZip the path of the file to unzip (in "/ressources/" folder) ( ex: "dirA" )
         * @param destDir the path of the destination folder
         */
        fun unzip(srcZip : String, destDir: String) {
            val zipPath = Paths.get("src/main/resources/$srcZip")
            val destPath = Paths.get("src/main/resources/$destDir")
            val zipFile = ZipFile(zipPath.toFile()).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    zip.getInputStream(entry).use { input ->
                        val outPath = Paths.get("src/main/resources/$destDir/${entry.name}")
                        try {
                            Files.copy(input, outPath)
                        }
                        catch (e: Exception) {
                            this.delete("$destDir/${entry.name}")
                            Files.copy(input, outPath)
                        }
                    }
                }
            }
        }

        /**
         * Delete a file
         * @param path the path of the file to delete (in "/ressources/" folder) ( ex: "xml/PrixCarburants_instantane.zip" )
         */
        fun delete(src : String) {
            val filePath = Paths.get("src/main/resources/$src")
            try {
                Files.delete(filePath)
                println("Deletion succeeded.")
            } catch (e: IOException) {
                println("Deletion failed.")
                //e.printStackTrace()
            }
        }
    }
}