plugins {
    kotlin("multiplatform") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    `maven-publish`
//    id("com.android.library") version "7.4.2"
}

group = "dev.slimevr"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        jvmToolchain(17)
    }
//    js {
//        nodejs()
//    }
//    ios()
//    androidTarget()
//
//    val hostOs = System.getProperty("os.name")
//    val isArm64 = System.getProperty("os.arch") == "aarch64"
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
//        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
//        hostOs == "Linux" && isArm64 -> linuxArm64("native")
//        hostOs == "Linux" && !isArm64 -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.github.jmdns:jmdns:0e40954468")
                implementation("io.ktor:ktor-server-core:2.3.9")
                implementation("io.ktor:ktor-server-netty:2.3.9")
                implementation("io.ktor:ktor-server-default-headers:2.3.9")
            }
        }
        val jvmTest by getting
//        val jsMain by getting
//        val jsTest by getting
//        val androidMain by getting
//        val androidUnitTest by getting
//        val nativeMain by getting
//        val nativeTest by getting
    }
}

publishing {
    repositories {
        maven {}
    }
}
