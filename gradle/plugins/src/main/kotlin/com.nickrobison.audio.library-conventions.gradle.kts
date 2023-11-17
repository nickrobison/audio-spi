plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

group = "com.nickrobison.audio"
version = "1.0-SNAPSHOT"

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