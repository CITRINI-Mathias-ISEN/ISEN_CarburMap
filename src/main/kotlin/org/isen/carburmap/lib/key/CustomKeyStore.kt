package org.isen.carburmap.lib.key

import com.github.kittinunf.fuel.core.FuelManager
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

class CustomKeyStore {
    companion object {
        fun customKeyStore() {
            val certFile : Certificate = ClassLoader.getSystemClassLoader().getResource("data.economie.gouv.fr.crt")?.let { CertificateFactory.getInstance("X.509").generateCertificate(it.openStream()) }!!
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            keyStore.setCertificateEntry("data.economie.gouv.fr", certFile)
            FuelManager.instance.keystore = keyStore
        }
    }
}