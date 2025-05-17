package com.harleylizard.deployment

import com.dynatrace.hash4j.hashing.Hashing
import com.harleylizard.deployment.html.Page
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class Manager(
    private val properties: Properties, private val bucket: Bucket) : HttpHandler {

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
        val uri = exchange.requestURI.getPath()
        val path = Paths.get(uri.substring(uri.indexOf("/") + 1))

        val page = Page(exchange)
        val body = page.body

        val has = bucket.has(path)
        if (!has) {
            body.header("404")
            body.paragraph("File not found $path")
            page.send(MISSING_FILE)
            return
        }
        val result = bucket.get(path)
        if (Files.isRegularFile(result)) {
            val headers = exchange.responseHeaders
            headers.set("Content-Disposition", "attachment; filename=\"${path.fileName}\"")
            headers.set("Content-Type", "application/octet-stream")

            exchange.sendResponseHeaders(OK, Files.size(result))
            Files.newInputStream(result).use {
                exchange.responseBody.use { body ->
                    body.write(it.readAllBytes())
                    body.flush()
                }
            }
        } else {
            exchange.sendResponseHeaders(NOT_ALLOWED, -1)
        }
    }

    private fun put(exchange: HttpExchange) {
        if (properties.verify(exchange.requestHeaders).ok) {
            val path = Paths.get(exchange.requestURI.path.substring(1))

            val temporary = Files.createTempFile("deployment", ".temp")
            Files.copy(exchange.requestBody, temporary, StandardCopyOption.REPLACE_EXISTING)

            exchange.sendResponseHeaders(OK, -1)

            val hash = Files.newInputStream(temporary).hash
            Files.newInputStream(temporary).use {
                bucket.add(path, it, hash)
            }
        } else {
            exchange.sendResponseHeaders(NOT_ALLOWED, -1)
        }
    }

    companion object {
        private const val OK = 200
        private const val NOT_ALLOWED = 405
        private const val MISSING_FILE = 404

        private const val GET = "GET"
        private const val PUT = "PUT"

        val InputStream.hash get() = use { stream ->
            val hashed = Hashing.murmur3_128().hashBytesTo128Bits(stream.readAllBytes())
            val bytes = hashed.toByteArray()

            val builder = StringBuilder()
            bytes.forEach { builder.append(String.format("%02X", it)) }

            builder.toString()
        }
    }
}