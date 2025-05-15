package com.harleylizard.deployment

import com.harleylizard.deployment.Library.Companion.libraryOf
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class Bucket(private val path: Path) {
    private val file = path.resolve("library.json")

    fun add(mavenFile: Path, body: InputStream, hash: String) {
        val library = file.libraryOf()
        library[hash] = mavenFile
        Files.copy(body, path.resolve(hash), StandardCopyOption.REPLACE_EXISTING)
        library.save(file)
    }

    fun get(hash: String): Path = file.libraryOf()[hash].let { path.resolve(it) }

    fun has(hash: String) = file.libraryOf().takeIf { hash in it }?.let { Files.isRegularFile(path.resolve(it[hash])) } == true
}