package com.harleylizard.deployment

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class Library(private val map: MutableMap<String, Path>) {
    val inverse: Map<Path, String> get() = Collections.unmodifiableMap(map.entries.associate { (key, value) -> value to key })

    fun save(path: Path) {
        Files.newBufferedWriter(path).use {
            val json = JsonObject()
            for ((key, value) in map) {
                json.addProperty(key, value.toString().replace("\\", "/"))
            }
            val jsonObject = JsonObject()
            jsonObject.add(NAME, json)
            gson.toJson(jsonObject, it)
        }
    }

    operator fun get(hash: String): Path = map.getNonNull(hash)

    operator fun set(hash: String, path: Path) {
        map[hash] = path
    }

    operator fun contains(hash: String) = hash in map.keys

    companion object {
        private const val NAME = "library"

        private val gson = GsonBuilder().setPrettyPrinting().create()

        fun Path.libraryOf(): Library {
            if (Files.isRegularFile(this)) {
                return Files.newBufferedReader(this).use {
                    val json = gson.fromJson(it, JsonObject::class.java).getAsJsonObject(NAME)

                    val map = mutableMapOf<String, Path>()
                    for ((key, value) in json.entrySet()) {
                        map[key] = Paths.get(value.asString)
                    }
                    Library(map)
                }
            }
            return Library(mutableMapOf())
        }

        fun <K, V> Map<K, V>.getNonNull(k: K): V = get(k)!!
    }
}