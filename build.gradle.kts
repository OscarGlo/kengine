import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    kotlin("multiplatform") version "1.8.10"
}

group = "me.oscarglo"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

val lwjglVersion = "3.3.2"

val platforms = listOf(
    "natives-linux",
    "natives-linux-arm64",
    "natives-linux-arm32",
    "natives-macos",
    "natives-macos-arm64",
    "natives-windows",
    "natives-windows-arm64",
    "natives-windows-x86"
)

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()

        tasks.register<JavaExec>("main") {
            classpath(
                configurations["runtimeClasspath"],
                configurations["jvmRuntimeClasspath"],
                "build/classes/kotlin/jvm/main"
            )
            main = "demo.Main"
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {}
            }
        }
        binaries.executable()
    }

    fun KotlinDependencyHandler.lwjgl(name: String = "") {
        val n = if (name != "") "-$name" else ""

        implementation("org.lwjgl:lwjgl$n")
        for (platform in platforms)
            runtimeOnly("org.lwjgl:lwjgl$n:$lwjglVersion:$platform")
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-RC")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
                lwjgl()
                lwjgl("glfw")
                lwjgl("openal")
                lwjgl("opengl")
                lwjgl("stb")
            }
        }
        val jvmTest by getting {
            dependencies {}
        }
    }
}
