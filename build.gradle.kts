buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
}

plugins {
    kotlin("multiplatform") version "1.3.20"
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC12"
    jacoco
}

repositories {
    mavenCentral()
    jcenter()
}

group = "codes.spectrum.konveyor"
version = "0.0.1"

apply {
    plugin("maven-publish")
}

val userHome = System.getProperty("user.home")
detekt {
    toolVersion = "1.0.0-RC12"                             // Version of the Detekt CLI that will be used. When unspecified the latest detekt version found will be used. Override to stay on the same version.
    input = files(                                        // The directories where detekt looks for input files. Defaults to `files("src/main/java", "src/main/kotlin")`.
        "src/commonMain/kotlin",
        "src/jvmMain/kotlin",
        "src/jsMain/kotlin",
        "src/linuxMain/kotlin"
    )
    parallel = false                                      // Builds the AST in parallel. Rules are always executed in parallel. Can lead to speedups in larger projects. `false` by default.
    config = files("detekt/config.yml")                  // Define the detekt configuration(s) you want to use. Defaults to the default detekt configuration.
    baseline = file("detekt/baseline.xml")               // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    filters = ""                                          // Regular expression of paths that should be excluded separated by `;`.
    disableDefaultRuleSets = false                        // Disables all default detekt rulesets and will only run detekt with custom rules defined in `plugins`. `false` by default.
//    plugins = "other/optional/ruleset.jar"                // Additional jar file containing custom detekt rules.
    debug = false                                         // Adds debug output during task execution. `false` by default.
    reports {
        xml {
            enabled = true                                // Enable/Disable XML report (default: true)
            destination = file("build/reports/detekt.xml")  // Path where XML report will be stored (default: `build/reports/detekt/detekt.xml`)
        }
        html {
            enabled = true                                // Enable/Disable HTML report (default: true)
            destination = file("build/reports/detekt.html") // Path where HTML report will be stored (default: `build/reports/detekt/detekt.html`)
        }
    }
    idea {
        path = "$userHome/idea"
        codeStyleScheme = "$userHome/idea/idea-code-style.xml"
        inspectionsProfile = "$userHome/idea/inspect.xml"
        report = "$project.projectDir/reports"
        mask = "*.kt,"
    }
}

jacoco {
    toolVersion = "0.8.3"
}

tasks {

    val codeCoverageReport by creating(JacocoReport::class) {
        group = "verification"
        dependsOn()
        executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        classDirectories.setFrom(
            files("${buildDir}/classes/kotlin/js/main"),
            files("${buildDir}/classes/kotlin/jvm/main"),
            files("${buildDir}/classes/kotlin/linux/main")
        )
        reports {
            xml.isEnabled = true
            xml.destination = File("$buildDir/reports/jacoco/report.xml")
            html.isEnabled = false
            html.destination = File("$buildDir/reports/jacoco/report.html")
            csv.isEnabled = false
        }

    }
    check {
        dependsOn(codeCoverageReport)
    }
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