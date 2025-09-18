plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

group = "de.obey.crown"
version = "1.0.0"
val targetJavaVersion = 17

val pluginYml = file("src/main/resources/plugin.yml")
val pluginVersion: String by lazy {
    val versionLine = pluginYml.readLines().find { it.trim().startsWith("version:") }
    versionLine?.split("version:")?.getOrNull(1)?.trim()
        ?: error("Could not find version in plugin.yml")
}

val pluginName: String by lazy {
    val versionLine = pluginYml.readLines().find { it.trim().startsWith("name:") }
    versionLine?.split("name:")?.getOrNull(1)?.trim()
        ?: error("Could not find name in plugin.yml")
}

version = pluginVersion

repositories {
    mavenCentral()
    mavenLocal()
    maven ("https://maven.enginehub.org/repo/")
    maven ("https://repo.papermc.io/repository/maven-public/")
}

dependencies {

    compileOnly("de.obey.crown.core:CrownCore:1.0.0")

    compileOnly ("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.9.2")
    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.0.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    toolchain.vendor.set(JvmVendorSpec.ADOPTIUM)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set(pluginName)
    archiveVersion.set(pluginVersion)
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("org.bstats", "${project.group}.noobf.bstats")
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named("build") {
    dependsOn(tasks.shadowJar)
}

