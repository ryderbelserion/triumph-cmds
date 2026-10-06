plugins {
    id("com.ryderbelserion.feather.patcher")

    `java-plugin`
}

rootProject.version = rootProject.property("version") as String
rootProject.group = rootProject.property("group") as String

tasks.register("publishLocally") {
    description = "Publishes the library to the local repository!"
    group = "fusion"

    dependsOn(subprojects.filter { !it.name.contains("example") }.map { it.tasks.matching { it.name == "publishToMavenLocal" } })
}

tasks.register("publish") {
    description = "Publishes the library to the remote repository!"
    group = "fusion"

    dependsOn(subprojects.filter { !it.name.contains("example") }.map { it.tasks.matching { it.name == "publish" } })
}

val path = projectDir.resolve("src")

patcher {
    patchesDirectory.set(projectDir.resolve("patches"))
    targetDirectory.set(path.resolve("target"))
    workingDirectory.set(path)

    url.set("git@github.com:TriumphTeam/triumph-cmds.git")
    sha.set("0fbe4718942b39c0ac067ae4dcc0b2f68e495f12")

    group = "feather-patcher"
}

tasks.withType<Test> {
    isEnabled = false
}