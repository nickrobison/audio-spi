plugins {
    id("com.nickrobison.audio.library-conventions")
}

dependencies {
    implementation("com.github.albfernandez:juniversalchardet:2.4.0")
    implementation(project(":jlayer"))
    implementation(project(":tritonus:shared"))
}
