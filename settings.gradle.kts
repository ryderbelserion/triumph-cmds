plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "triumph-cmds"

listOf(
    "src/target/core" to "core",

    "src/target/simple" to "simple",

    "src/target/minecraft/bukkit" to "bukkit",
    "src/target/examples/minecraft/bukkit" to "bukkit-example",

    "src/target/discord/common" to "discord",
    "src/target/discord/jda" to "jda",
    "src/target/discord/kord" to "kord",

    "src/target/examples/discord/jda" to "jda-example",
    "src/target/examples/discord/kord" to "kord-example",

    "src/target/kotlin/coroutines" to "coroutines",
    "src/target/kotlin/extensions" to "extensions",

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