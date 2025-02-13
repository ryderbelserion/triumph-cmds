plugins {
    id("triumph.base")
}

dependencies {
    compileOnlyApi(libs.jetbrains)

    compileOnly(libs.bundles.adventure)

    compileOnly(libs.guava)
}