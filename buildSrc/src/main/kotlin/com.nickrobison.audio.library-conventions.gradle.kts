group = "com.nickrobison.audio"
version = "1.0-SNAPSHOT"

plugins {
    id("java-library")
    `maven-publish`
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.nickrobison.audio"
            version = "0.1-SNAPSHOT"

            from(components["java"])
        }
    }
}