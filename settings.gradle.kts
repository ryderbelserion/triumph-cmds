plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "triumph-cmds"

listOf(
    "src/examples/bukkit" to "bukkit-example",
    "src/minecraft/bukkit" to "bukkit",
    "src/core" to "core",

    "common" to "common"
).forEach {
    val path = it.first

    if (file(path).exists()) {
        includeProject(path, it.second)
    }
}

fun includeProject(name: String) {
    includeProject(name) {
        this.name = "${rootProject.name.lowercase()}-$name"
    }
}

fun includeProject(folder: String, name: String) {
    includeProject(name) {
        this.name = "${rootProject.name.lowercase()}-$name"
        this.projectDir = file(folder)
    }
}

fun includeProject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}