enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "triumph-cmds"

listOf(
    "core",
    "simple"
).forEach(::includeProject)

fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
    }
}

listOf(
    "minecraft/bukkit" to "bukkit"
).forEach {
    includeProjectFolders(it.first, it.second)
}

listOf(
    "examples/minecraft/bukkit" to "bukkit-example"
).forEach {
    includeProjectFolders(it.first, it.second)
}

fun includeProjectFolders(folder: String, name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
        this.projectDir = file(folder)
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}