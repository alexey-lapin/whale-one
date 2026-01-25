plugins {
    id("java")
    alias(libs.plugins.graalvm.native)
    alias(libs.plugins.release)
    alias(libs.plugins.spring.boot)
}

dependencies {
    annotationProcessor(libs.projectlombok.lombok)
    annotationProcessor(libs.mapstructProcessor)

    compileOnly(libs.projectlombok.lombok)

    implementation(project(":whale-one-ui"))

    implementation(libs.spring.springBootStarterActuator)
    implementation(libs.spring.springBootStarterDataJdbc)
    implementation(libs.spring.springBootStarterFlyway)
    implementation(libs.spring.springBootStarterOauth2AuthorizationServer)
    implementation(libs.spring.springBootStarterOauth2ResourceServer)
    implementation(libs.spring.springBootStarterValidation)
    implementation(libs.spring.springBootStarterWeb)

    implementation(libs.awssdk.s3)
    implementation(libs.fastcsv)
    implementation(libs.flywaydb.flywayDatabasePostgresql)
    implementation(libs.mapstruct)
    implementation(libs.postgresql.postgresql)
    implementation(libs.rsqlParser)

    developmentOnly(libs.spring.springBootDevtools)

    testImplementation(libs.spring.springBootStarterDataJdbcTest)
    testImplementation(libs.spring.springBootStarterFlywayTest)
    testImplementation(libs.spring.springBootStarterSecurityOauth2ResourceServerTest)
    testImplementation(libs.spring.springBootStarterTest)
    testImplementation(libs.spring.springBootStarterValidationTest)
    testImplementation(libs.spring.springBootStarterWebmvcTest)
}

tasks.test {
    useJUnitPlatform()
}

springBoot {
    buildInfo {
        properties {
            artifact = rootProject.name
        }
    }
}

graalvmNative {
    toolchainDetection.set(true)
    binaries {
        named("main") {
            imageName.set(rootProject.name)
            buildArgs.add("--verbose")
        }
    }
    metadataRepository {
        version = "0.3.17"
    }
}

tasks.bootBuildImage {
    val registry = System.getenv("CR_REGISTRY")!!
    val namespace = System.getenv("CR_NAMESPACE")!!
    imageName = "${registry}/${namespace}/${rootProject.name}:${project.version}"
    publish = true
    tags = setOf("${registry}/${namespace}/${rootProject.name}:latest")
    docker {
        publishRegistry {
            url = System.getenv("CR_REGISTRY")
            username = System.getenv("CR_USERNAME")
            password = System.getenv("CR_PASSWORD")
        }
    }
}
