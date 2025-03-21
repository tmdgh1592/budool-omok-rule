val groupName = "com.github.tmdgh1592"
val projectArtifactId = "budool-omok-rule"
val currentVersion = "v2.0.0-alpha"

plugins {
    java
    kotlin("jvm") version "1.8.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    `maven-publish`
}

group = groupName
version = currentVersion

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
        register<MavenPublication>("maven") {
            groupId = groupName
            artifactId = projectArtifactId
            version = currentVersion
            from(components["kotlin"])
        }
    }
}
