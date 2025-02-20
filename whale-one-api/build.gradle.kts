plugins {
    id("java")
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.4"
    id("gg.jte.gradle") version "3.1.15"
    id("pl.allegro.tech.build.axion-release") version "1.18.7"
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    compileOnly("org.projectlombok:lombok")

    jteGenerate("gg.jte:jte-native-resources:3.1.15")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
//    implementation("org.springframework.session:spring-session-jdbc")

    implementation("org.mapstruct:mapstruct:1.6.3")

    implementation("gg.jte:jte:3.1.15")
    implementation("gg.jte:jte-native-resources:3.1.15")
    implementation("gg.jte:jte-spring-boot-starter-3:3.1.15")

    implementation(project(":whale-one-ui"))

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.postgresql:postgresql")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
}

jte {
    generate()
    targetDirectory.set(file("build/classes/java/main").toPath())
    jteExtension("gg.jte.nativeimage.NativeResourcesExtension")
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