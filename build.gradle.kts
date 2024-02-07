import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0" apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
}

allprojects {
    group = "me.sknz.minecraft"
    version = "2023.0727.0"

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"

        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "kotlin")
}