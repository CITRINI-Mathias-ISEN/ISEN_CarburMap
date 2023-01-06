package org.isen.carburmap.lib.event

import org.apache.logging.log4j.kotlin.Logging

class Promise(private val promisePool: PromisePool) {
    companion object : Logging
    var result: Any? = null
        set(value) {
            field = value
            promisePool.fulfill(this)
        }
    enum class State {
        PENDING,
        FULFILLED,
        REJECTED
    }

    var state: State = State.PENDING
        set(value) {
            field = value
            if (value == State.FULFILLED) {
                fulfillCallback()
            } else if (value == State.REJECTED) {
                logger.warn("Promise rejected")
                fulfillCallback()
            }
        }

    private fun fulfillCallback(){
        promisePool.fulfill(this)
    }
}