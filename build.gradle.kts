plugins {
    alias(libs.plugins.release)
}

group = "com.github.alexeylapin.whale-one"
version = scmVersion.version

allprojects {
    project.version = rootProject.version
}