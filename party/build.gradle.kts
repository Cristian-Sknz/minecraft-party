plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly(project(":common"))
    compileOnly("org.github.paperspigot:paperspigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.github.azbh111:craftbukkit-1.8.8:R")
    compileOnly(kotlin("reflect"))

    testImplementation("org.mockito:mockito-core:3.12.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}