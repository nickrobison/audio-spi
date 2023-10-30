plugins {
    id("com.nickrobison.audio.library-conventions")
}

dependencies {
    testImplementation(project(":testing"))
}

publishing {
    publications {
        getByName("maven", MavenPublication::class) {
            artifactId = "jaad-core"
        }
    }
}