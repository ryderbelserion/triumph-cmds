plugins {
    `config-publish`
    `java-plugin`
}

dependencies {
    compileOnly(libs.guava)

    api(libs.jspecify)
}