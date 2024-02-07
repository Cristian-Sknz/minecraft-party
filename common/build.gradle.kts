plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(files("../libs/jnbt-1.5.jar"))

    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.13.0")
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.13.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    api("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    // api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2") relocate yaml

    compileOnly("org.github.paperspigot:paperspigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.github.azbh111:craftbukkit-1.8.8:R")

    // testes
    testImplementation("org.github.paperspigot:paperspigot-api:1.8.8-R0.1-SNAPSHOT")
    testImplementation("org.mockito:mockito-core:3.12.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}