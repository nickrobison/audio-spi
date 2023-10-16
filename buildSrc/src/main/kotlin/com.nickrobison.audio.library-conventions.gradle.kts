import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

group = "com.nickrobison.audio"
version = "1.0-SNAPSHOT"

plugins {
    id("java-library")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}