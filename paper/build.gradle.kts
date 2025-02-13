import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm") version "2.0.20-Beta1"
    id("com.gradleup.shadow") version "9.0.0-beta8"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    kotlin("plugin.serialization") version "2.1.10"
    id("io.papermc.paperweight.userdev") version "1.7.5"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.0"
}

group = "me.jesforge"
version = "1.0.0"



dependencies {
    // Paper
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("gg.flyte:twilight:1.1.17")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // CommandAPI
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.7.0")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.7.0")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.7.0")

    // Adventure API
    implementation("net.kyori:adventure-api:4.18.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveBaseName.set("ServerManager")
    }
    assemble {
        dependsOn(shadowJar)
    }
}

bukkit {
    name = "ServerManager"
    version= "1.0.0"
    main = "me.jesforge.servermanager.Main"
    foliaSupported = false
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("jesforge")

}