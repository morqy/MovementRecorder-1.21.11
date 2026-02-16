plugins {
    id("fabric-loom") version "1.15-SNAPSHOT"
}

group = "xyz.yuro"
version = "2.0.0"

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings("net.fabricmc:yarn:1.21.11+build.4:v2")
    modImplementation("net.fabricmc:fabric-loader:0.18.4")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.141.3+1.21.11")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}
