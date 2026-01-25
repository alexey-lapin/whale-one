plugins {
    id("java")
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.4"
    id("pl.allegro.tech.build.axion-release") version "1.18.7"
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    compileOnly("org.projectlombok:lombok")

    implementation(project(":whale-one-ui"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")

    implementation("cz.jirutka.rsql:rsql-parser:2.1.0")
    implementation("de.siegmar:fastcsv:3.6.0")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.postgresql:postgresql")
    implementation("software.amazon.awssdk:s3:2.30.36")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
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