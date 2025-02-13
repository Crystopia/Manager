plugins {
    kotlin("jvm") version "2.0.20-Beta1"
    id("com.gradleup.shadow") version "9.0.0-beta8"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    kotlin("plugin.serialization") version "2.1.10"

}

group = "me.jesforge"
version = "1.0.0"


dependencies {
    // Luckperms
    compileOnly("net.luckperms:api:5.4")

    // Velocity
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // CommandAPI
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.7.0")
    implementation("dev.jorel:commandapi-velocity-shade:9.7.1-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.18.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveBaseName.set("ProxyManager")
    }
    assemble {
        dependsOn(shadowJar)
    }
}

