plugins {
    id("java")
}

group = "com.rms.mapping"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.albfernandez:juniversalchardet:2.4.0")
    implementation(project(":jlayer"))
    implementation(project(":tritonus:shared"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}