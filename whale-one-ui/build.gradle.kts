import com.github.gradle.node.npm.task.NpmInstallTask
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("java")
    alias(libs.plugins.node)
}

node {
    download = false
    npmCommand = project.findProperty("npmCommand") as String? ?: "npm"
}

val npmInstall by tasks.named<NpmInstallTask>("npmInstall") {
    inputs.file(file("package.json"))
    inputs.file(file("package-lock.json"))
    outputs.dir(file("node_modules"))
}

val npmBuild by tasks.registering(NpmTask::class) {
    dependsOn(npmInstall)
    environment.put("VITE_APP_BASE_PATH", "/@replace-base-path-ui@")
    environment.put("VITE_API_BASE_URL", "/@replace-base-path-api@")
    args.set(listOf("run", "build"))
    inputs.file(file("package.json"))
    inputs.file(file("vite.config.ts"))
    inputs.file(file("index.html"))
    inputs.dir(file("src"))
    inputs.dir(file("public"))
    outputs.dir(file("dist"))
}

tasks.processResources {
    dependsOn(npmBuild)
    from(file("dist")) {
        into("static")
    }
}