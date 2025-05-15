package com.harleylizard.deployment.html

import com.sun.net.httpserver.HttpExchange

// temporary
class Page(private val exchange: HttpExchange) {
    private val builder = StringBuilder()

    val body = Body(builder)

    init {
        builder.append("<!DOCTYPE html><html><head><style>h1 { font-family: \"Montserrat Medium\", Arial, sans-serif; } p { font-family: \"Lato\", Arial, sans-serif;}</style></head><body>")
    }

    fun send(opcode: Int) {
        builder.append("</body></html>")
        val result = builder.toString()
        exchange.sendResponseHeaders(opcode, result.length.toLong())
        exchange.responseBody.use {
            it.write(result.toByteArray())
            it.flush()
        }
    }

    class Body(private val builder: StringBuilder) {

        fun header(header: String) {
            builder.append("<h1>$header</h1>")
        }

        fun paragraph(p: String) {
            builder.append("<p>$p</p>")
        }
    }
}