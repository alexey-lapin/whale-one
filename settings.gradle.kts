import dev.aga.gradle.versioncatalogs.Generator.generate

plugins {
    id("dev.aga.gradle.version-catalog-generator") version "4.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        generate("libs") {
            fromToml("awsBom")
            fromToml("springBootDependencies")
        }
    }
}

rootProject.name = "whale-one"

include("whale-one-api")
include("whale-one-ui")