package kengine.util

import java.io.File

object Reflect {
    fun getAllClasses(): List<Class<*>> {
        val loader = ClassLoader.getSystemClassLoader()
        val roots = loader.getResources("")

        val classes = mutableListOf<Class<*>>()
        roots.iterator().forEach {
            classes += getPackageClasses(it.path, loader)
        }
        return classes
    }

    fun getPackageClasses(path: String, loader: ClassLoader, prefix: String = ""): List<Class<*>> {
        val files = File(path).listFiles() ?: emptyArray()

        val classes = mutableListOf<Class<*>>()
        files.forEach {
            if (it.isDirectory) {
                classes += getPackageClasses(it.path, loader, prefix + it.name + ".")
            } else if (it.isFile && it.extension == "class") {
                classes.add(loader.loadClass(prefix + it.nameWithoutExtension))
            }
        }
        return classes
    }
}