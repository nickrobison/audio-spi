plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(platform(libs.kotlin.bom))
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}