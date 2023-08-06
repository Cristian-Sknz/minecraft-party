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
    api(platform("io.projectreactor:reactor-bom:2022.0.9"))
    api("io.projectreactor:reactor-core:3.5.8")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.15.2")

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