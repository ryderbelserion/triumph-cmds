plugins {
    id("triumph.base")

    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
}

dependencies {
    api(projects.triumphCmdsBukkit)

    compileOnly(libs.paper)
}

tasks {
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion("1.21.4")
    }
}