package com.harleylizard.deployment

import com.harleylizard.deployment.Library.Companion.libraryOf
import java.nio.file.Path

class Bucket(private val path: Path) {
    private val file = path.resolve("library.json")

    fun save() {
        val library = file.libraryOf

        library.save(file)
    }

    fun get() {
        val library = file.libraryOf

    }
}