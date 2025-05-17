package com.harleylizard.deployment

import com.google.gson.GsonBuilder
import com.harleylizard.deployment.Properties.Companion.make
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val options = Options()
    options.addOption("p", "path", true, "Runtime path.")

    val parser = DefaultParser()
    val parsed = parser.parse(options, args)

    val path = parsed.getOptionValue("p", "run")?.let { Paths.get(it).make } ?: throw RuntimeException("No runtime path.")

    val gson = GsonBuilder().registerTypeAdapter(Properties::class.java, Properties.deserializer).create()

    val properties = Files.newBufferedReader(path.resolve("properties.json")).use { gson.fromJson(it, Properties::class.java) }
    val server = properties.spin
    server.createContext(properties.path, Manager(properties, Bucket(path)))
    server.executor = null
    server.start()
}