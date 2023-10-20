plugins {
    id("com.nickrobison.audio.library-conventions")
}

publishing {
    publications {
        getByName("maven", MavenPublication::class) {
            artifactId = "tritonus-shared"
        }
    }
}