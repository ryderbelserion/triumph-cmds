plugins {
    id("triumph.base")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(project(":triumph-cmd-core"))

    compileOnly(libs.paper)
}