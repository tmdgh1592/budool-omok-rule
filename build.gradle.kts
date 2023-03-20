plugins {
    kotlin("jvm") version "1.8.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("maven-publish")
}

group = "com.github.tmdgh1592"
version = "v1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.8.2")
    testImplementation("org.assertj", "assertj-core", "3.22.0")
    testImplementation("io.kotest", "kotest-runner-junit5", "5.2.3")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    test {
        useJUnitPlatform()
    }
    ktlint {
        verbose.set(true)
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.tmdgh1592"
            artifactId = "budool-omok-rule"
            version = "1.0.3"
        }
    }
}
