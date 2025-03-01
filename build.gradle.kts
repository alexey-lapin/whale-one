plugins {
    id("pl.allegro.tech.build.axion-release") version "1.18.7"
}

group = "com.github.alexeylapin.whale-one"
version = scmVersion.version

allprojects {
    project.version = rootProject.version
}