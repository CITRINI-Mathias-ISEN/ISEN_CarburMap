package org.isen.carburmap.lib.icon

import java.awt.Image
import java.awt.image.BufferedImage

data class Icon(val name:String, val path:String, val bufferedImage: BufferedImage, val image: Image, val smallImage: Image, val bigImage: Image)

data class SimpleIcon(val name:String, val path:String, val image: Image, val size: Int)