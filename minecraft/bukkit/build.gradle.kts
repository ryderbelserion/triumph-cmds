plugins {
    id("triumph.base")

    alias(libs.plugins.shadow)
}

dependencies {
    api(projects.triumphCmdsCore)

    compileOnly(libs.paper)
}