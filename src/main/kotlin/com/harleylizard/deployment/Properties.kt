package com.harleylizard.deployment

import com.google.gson.JsonDeserializer
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern

class Properties private constructor(
    private val address: InetSocketAddress,
    private val username: String,
    private val password: String,
    val path: String) {

    val spin: HttpServer get() = HttpServer.create(address, 0)

    fun verify(headers: Headers): Result {
        if (HEADER in headers) {
            val pattern = Pattern.compile(":")
            val credentials = String(Base64.getDecoder().decode(headers.getFirst(HEADER).substring(6))).split(pattern, 2)

            val requestUsername = credentials[0]
            val requestPassword = credentials[1]
            if (
                requestUsername == username &&
                requestPassword == password
            ) {
                return Result.ok()
            }
        }
        return Result.error("Failed to verify credentials.")
    }

    companion object {
        private const val HEADER = "Authorization"

        val deserializer = JsonDeserializer { json, _, _ ->
            val jsonObject = json.asJsonObject
            val host = jsonObject.getAsJsonPrimitive("host").asString
            val port = jsonObject.getAsJsonPrimitive("port").asInt
            val address = InetSocketAddress(host, port)

            val username = jsonObject.getAsJsonPrimitive("username").asString
            val password = jsonObject.getAsJsonPrimitive("password").asString
            val path = jsonObject.getAsJsonPrimitive("path").asString

            Properties(address, username, password, path)
        }

        val Path.make get() = self {
            parent?.takeUnless { Files.isDirectory(it) }?.let { Files.createDirectories(it) }
        }

        private fun <T> T.self(unit: T.() -> Unit): T {
            unit(this)
            return this
        }

    }
}