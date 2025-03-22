package com.example.asmplugin

import java.io.File

fun File.isClassFile(): Boolean {
    return this.isFile && this.name.endsWith(".class")
}