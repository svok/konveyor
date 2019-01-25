plugins {
    kotlin("multiplatform") version "1.3.20"
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC12"
}

repositories {
    mavenCentral()
}
group = "codes.spectrum.konveyor"
version = "0.0.1"

apply {
    plugin("maven-publish")
}

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
                // Setup the Kotlin compiler options for the 'main' compilation:
                jvmTarget = "1.8"
            }

            compileKotlinTask // get the Kotlin task 'compileKotlinJvm'
            output // get the main compilation output
        }

        compilations["test"].runtimeDependencyFiles // get the test runtime classpath
    }
    js()
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    linuxX64("linux")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val linuxMain by getting {
        }
        val linuxTest by getting {
        }

//        val testAll by getting {
//            dependsOn(commonTest)
//            dependsOn(jsTest)
//            dependsOn(jvmTest)
//            dependsOn(linuxTest)
//        }

        all {
            languageSettings.apply {
                progressiveMode = true
                languageVersion = "1.3"
                apiVersion = "1.3"
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
//                enableLanguageFeature("InlineClasses") // language feature name
            }
        }
    }
}