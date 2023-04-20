import org.lwjgl.Lwjgl.Module.*
import org.lwjgl.lwjgl

plugins {
    kotlin("multiplatform") version "1.8.10"
    id("org.lwjgl.plugin") version "0.0.34"
}

group = "me.oscarglo"
version = "1.0"
description = "KEngine"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }

    val implementation by configurations

    fun deps() = dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")

        lwjgl {
            nativesForEveryPlatform = true
            implementation(glfw, openal, opengl, stb)
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting
        val commonTest by getting
        val jvmMain by getting {
            deps()
        }
        val jvmTest by getting {
            deps()
        }
    }
}
