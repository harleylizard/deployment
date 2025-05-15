package com.harleylizard.deployment

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Library(private val map: MutableMap<Path, Path>) {

    fun save(path: Path) {
        Files.newBufferedWriter(path).use {
            val json = JsonObject()
            for ((key, value) in map) {
                json.addProperty(key.toString(), value.toString())
            }
            gson.toJson(json, it)
        }
    }

    companion object {
        private val gson = GsonBuilder().setPrettyPrinting().create()

        val Path.libraryOf get() = takeUnless { Files.isRegularFile(it) }?.let {
            Library(mutableMapOf())
        } ?: Files.newBufferedReader(this).use {
            val json = gson.fromJson(it, JsonObject::class.java)

            val map = mutableMapOf<Path, Path>()
            for ((key, value) in json.entrySet()) {
                map[Paths.get(key)] = Paths.get(value.asString)
            }
            Library(map)
        }
    }
}