plugins {
    id("triumph.base")
}

dependencies {
    api(projects.triumphCmdsCore)

    compileOnly(libs.paper)
}