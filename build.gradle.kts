plugins {
    kotlin("jvm") version "1.9.0-Beta"
}


repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.mojang:brigadier:1.0.18")
    implementation(kotlin("reflect"))
}