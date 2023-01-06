package org.isen.carburmap.lib.event

import org.apache.logging.log4j.kotlin.Logging
import org.isen.carburmap.data.StationsList

class PromisePool(private val finalCall: (List<StationsList>) -> Unit) {
    companion object : Logging
    private val promises = ArrayDeque<Promise>()

    private val fulfilledPromises = mutableListOf<Promise>()
    fun fulfill(promise: Promise) {
        fulfilledPromises.add(promise)
        promises.remove(promise)
        if (promises.isEmpty()) {
            finalCall(fulfilledPromises.mapNotNull { if(it.result is StationsList) it.result as StationsList else null })
            logger.info("All promises fulfilled total: ${fulfilledPromises.size}")
        } else {
            logger.debug("Promise fulfilled, ${promises.size} remaining")
        }
    }

    fun add(promise: Promise) {
        promises.add(promise)
    }

    fun createPromise(): Promise {
        val promise = Promise(this)
        add(promise)
        return promise
    }
}