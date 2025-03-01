import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("base")
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    download = false
    npmCommand = project.findProperty("npmCommand") as String? ?: "npm"
}

tasks.register<NpmTask>("npmBuild") {
    dependsOn("npmInstall")
    args = listOf("run", "build")
//    environment = mapOf("VITE_APP_PUBLIC_PATH" to "/new")
}

tasks.register<Jar>("frontendJar") {
    dependsOn("npmBuild")
    archiveBaseName.set("frontend-assets")
    destinationDirectory.set(file(layout.buildDirectory.file("libs")))

    from("dist") {
        into("static")
    }
}

tasks.named("build") {
    dependsOn("npmBuild")
}

artifacts {
    add("default", tasks.named("frontendJar"))
}

tasks.clean {
    delete("node_modules", "dist")
}