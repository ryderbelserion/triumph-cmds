plugins {
    `java-library`
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

repositories {
    maven("https://repo.crazycrew.us/releases/")

    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(25)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    test {
        isEnabled = false
    }
}