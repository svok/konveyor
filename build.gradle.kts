import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import java.util.*

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
}

plugins {
    kotlin("multiplatform") version "1.3.20"
    `build-scan`
    `maven-publish`
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC12"
    id("com.jfrog.bintray") version "1.8.4"
    id("maven-publish")
    jacoco
//    id("org.ajoberstar.github-pages")
    id("org.jetbrains.dokka") version "0.9.17"
    maven
}

repositories {
    mavenCentral()
    jcenter()
}

group = "codes.spectrum"
version = "0.0.1"

//apply {
//    plugin("maven-publish")
//}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    publishAlways()
}

val userHome = System.getProperty("user.home")
detekt {
    toolVersion =
        "1.0.0-RC12"                             // Version of the Detekt CLI that will be used. When unspecified the latest detekt version found will be used. Override to stay on the same version.
    input =
        files(                                        // The directories where detekt looks for input files. Defaults to `files("src/main/java", "src/main/kotlin")`.
            "src/commonMain/kotlin",
            "src/jvmMain/kotlin",
            "src/jsMain/kotlin"
            // "src/linuxMain/kotlin"
        )
    parallel =
        false                                      // Builds the AST in parallel. Rules are always executed in parallel. Can lead to speedups in larger projects. `false` by default.
    config =
        files("detekt/config.yml")                  // Define the detekt configuration(s) you want to use. Defaults to the default detekt configuration.
    baseline =
        file("detekt/baseline.xml")               // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    filters =
        ""                                          // Regular expression of paths that should be excluded separated by `;`.
    disableDefaultRuleSets =
        false                        // Disables all default detekt rulesets and will only run detekt with custom rules defined in `plugins`. `false` by default.
//    plugins = "other/optional/ruleset.jar"                // Additional jar file containing custom detekt rules.
    debug =
        false                                         // Adds debug output during task execution. `false` by default.
    reports {
        xml {
            enabled = true                                // Enable/Disable XML report (default: true)
            destination =
                file("build/reports/detekt.xml")  // Path where XML report will be stored (default: `build/reports/detekt/detekt.xml`)
        }
        html {
            enabled = true                                // Enable/Disable HTML report (default: true)
            destination =
                file("build/reports/detekt.html") // Path where HTML report will be stored (default: `build/reports/detekt/detekt.html`)
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

    val dokkaOut = "$buildDir/docs"
    dokka {
        outputFormat = "html"
        outputDirectory = dokkaOut

        // This will force platform tags for all non-common sources e.g. "JVM"
        impliedPlatforms = mutableListOf("Common", "JVM")

        doFirst {
            println("Cleaning doc directory $dokkaOut...")
            project.delete(fileTree(dokkaOut))
        }

        // dokka fails to retrieve sources from MPP-tasks so they must be set empty to avoid exception
        kotlinTasks(closureOf<Any?> { emptyList<Any?>() })

        sourceDirs = listOf(
            "src/commonMain/kotlin",
            "src/jvmMain/kotlin"
        ).map { projectDir.resolve(it) }

//        samples = listOf("src/samples/java", "src/samples/kotlin")

//        includes = projectDir.resolve("src/main/docs").walkTopDown()
//            .filter { it.isFile }
//            .toList()
    }

    val dokkaJar by creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        dependsOn(dokka) // not needed; dependency automatically inferred by from(tasks.dokka)
        from(dokka.get().outputDirectory)
    }

    val javaDoc by registering(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles a jar archive containing the Javadoc API documentation."
        archiveClassifier.set("javadoc")
        dependsOn(dokka)
        from(dokka.get().outputDirectory)
    }

    // Create sources Jar from main kotlin sources
    val sourcesJar by creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles sources JAR"
        archiveClassifier.set("sources")
        from(
            listOf(
                "src/commonMain/kotlin",
                "src/jvmMain/kotlin"
            ).map { projectDir.resolve(it) }
        )
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                pom {
                    name.set("Konveyor")
                    description.set("Conveyor belt software design pattern in a kotlin DSL-like style")
                    url.set("https://github.com/spectrum-project/konveyor")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("svok")
                            name.set("Sergey Okatov")
                            email.set("sokatov@gmail.com")
                        }
                    }
//                    scm {
//                        connection.set("scm:git:git://example.com/my-library.git")
//                        developerConnection.set("scm:git:ssh://example.com/my-library.git")
//                        url.set("http://example.com/my-library/")
//                    }
                }
//                from(components)
                artifact(sourcesJar)
                artifact(dokkaJar)
            }
        }
        repositories {
            maven {
                url = uri("$buildDir/repository")
            }
        }
    }

    bintray {
//        user = project.findProperty("bintrayUser")?.toString() ?: ""
//        key = project.findProperty("bintrayKey")?.toString() ?: ""
        user = System.getenv("bintrayUser")?.toString() ?: ""
        key = System.getenv("bintrayKey")?.toString() ?: ""
        override = true
        println("User = $user")
        setPublications("mavenJava")

        pkg( closureOf<BintrayExtension.PackageConfig> {
            repo = "konveyor"
            name = "konveyor"
            desc = "Conveyor belt software design pattern in a kotlin DSL-like style"
            userOrg = "spectrum-project"
            websiteUrl = "https://github.com/spectrum-project/konveyor"
            issueTrackerUrl = "https://github.com/spectrum-project/konveyor/issues"
            vcsUrl = "git@github.com:spectrum-project/konveyor.git"
            githubRepo = "spectrum-project/konveyor"
            githubReleaseNotesFile = "CHANGELOG.md"
            setLicenses("Apache-2.0")
            setLabels(
                "conveyor",
                "conveyor-belt",
                "processor",
                "workflow",
                "kotlin",
                "kotlin dsl",
                "handler"
            )
            publish = true
            setPublications("mavenJava")
            version(closureOf<BintrayExtension.VersionConfig> {
                this.name = project.version.toString()
                released = Date().toString()
            })
        })
    }

    val bintrayUpload by existing(BintrayUploadTask::class) {
        dependsOn("build")
        dependsOn("generatePomFileForMavenJavaPublication")
        dependsOn(sourcesJar)
        dependsOn(dokkaJar)
    }

//    named<Task>("afterReleaseBuild") {
//        dependsOn(bintrayUpload)
//    }
    publish {
        dependsOn(bintrayUpload)
        dependsOn(bintrayPublish)
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
    //js()
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    // linuxX64("linux")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.1.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.1.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation(kotlin("stdlib-js"))
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
//            }
//        }
        // val linuxMain by getting {
        // }
        // val linuxTest by getting {
        // }

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
