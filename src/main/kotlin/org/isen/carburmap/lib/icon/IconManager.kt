package org.isen.carburmap.lib.icon

import java.awt.Image
import javax.imageio.ImageIO

class IconManager private constructor() {
    private val iconMaps: MutableMap<String, Icon> = mutableMapOf()
    companion object {
        private var INSTANCE: IconManager? = null;
        fun getInstance(): IconManager {
            if (INSTANCE == null) {
                INSTANCE = IconManager()
            }
            return INSTANCE!!;
        }
    }

    init {
        getIcon("./img/missing.png")
        INSTANCE = this
    }

    fun getIcon(path: String): Icon {
        if (iconMaps.containsKey(path)) {
            return iconMaps[path]!!
        }
        try {
            val bufferedImage = ClassLoader.getSystemClassLoader().getResource(path)?.let { ImageIO.read(it) }!!
            val img = bufferedImage.getScaledInstance(48,48, Image.SCALE_SMOOTH)
            val smallImg = bufferedImage.getScaledInstance(32,32, Image.SCALE_SMOOTH)
            val bigImg = bufferedImage.getScaledInstance(72,72, Image.SCALE_SMOOTH)
            iconMaps[path] = Icon(path, path.split('/').last(),bufferedImage, img, smallImg, bigImg)
            return iconMaps[path]!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return iconMaps["./img/missing.png"]!!
    }
}