package com.harleylizard.deployment

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

class Manager(
    private val properties: Properties,
    private val bucket: Bucket) : HttpHandler {

    override fun handle(exchange: HttpExchange) {
        when (exchange.requestMethod) {
            GET -> get(exchange)
            PUT -> put(exchange)
            else -> {
                exchange.sendResponseHeaders(NOT_ALLOWED, -1)
            }
        }
    }

    private fun get(exchange: HttpExchange) {
        exchange.sendResponseHeaders(OK, -1)
    }

    private fun put(exchange: HttpExchange) {
        if (properties.verify(exchange.requestHeaders).ok) {

            exchange.sendResponseHeaders(OK, -1)
        }
    }

    companion object {
        private const val OK = 200
        private const val NOT_ALLOWED = 405

        private const val GET = "GET"
        private const val PUT = "PUT"

    }
}