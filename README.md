# konveyor ETL Processor

## Description

Konveyor is a processor which realizes the conveyor belt software design pattern in a kotlin DSL-like 
style.

## What is the conveyor belt design pattern

The purpose of this pattern is designing of applications with complex but elastic logics. The elasticity 
of the pattern allows easily add/remove functionality without redesigning of entire application.

The central structure of the conveyor belt pattern is a Context containing all states of the workflow 
process. Suppose we have to handle a user request. Then, our Context must include the following entities.

```kotlin
data class MyContext(
    /**
     * User query in a form it came to us 
     */
    var rawQuery: UserQuery = UserQuery.EMPTY,
    
    /**
     * Result of validation, normalization and enrichement with default values 
     */
    var validatedQuery: UserQuery = UserQuery(),
    
    /**
     * A query to tha database to be performed
     */
    var dbQuery: DatabaseQuery = DatabaseQuery.EMPTY,
    
    /**
     * The result to be sent back to the requester
     */
    var dbResult: QueryResult = QueryResult()
)
```

Than, the workflow of the request handling is described with a Processor class and a set of Handlers.

```kotlin
val processor = MyProcessor(
    object: IHandler {
        override fun exec(context: MyContext) = with(context) {
            validatedQuery.field = makeValidationOnField(rawQuery.field)
        }
    },
    HandlerValidation2(),
    HandlerNormalization(),
    HandlerPrepareDbQuery(),
    HandlerPrepareResult()
)

val context = MyContext(rawQuery = aQueryFromUser)
processor.exec(context)
val result = context.result
```

You can notice that management of the workflow with such conveyor is as easy as adding/removing handlers.

### Using conveyor belt patterin with konveyor module

First of all, you need to configure your conveyor instance.

```kotlin
val conv  = conveyor<MyContext> {
    timeout { Duration.ofMillis(200) }

    // A simple handler
    exec { // this: MyContext -> 
        timeStart = Instant.now() 
    }

    // Detailed description of the handler
    handle { 
        timeout { Duration.ofMillis(10) }
        on { // this: MyContext -> 
            mode == MyContextMode.PROPER 
        }
        exec { registerQuery() }
    }

    // A processor containing a set of nested handlers
    process { // Standard logics: perform all where match = true
        timeout { Duration.ofMilliseconds(150) }
        exec { someProp = someFun() }
        handle {
            on { isOk() }
            exec { someFun() }
        }
    }

    processFirst { // Perform only first handler with match = true
        timeout { Duration.ofMilliseconds(150) }
        handle {
            on { isSomething() }
            exec { someFun() }
        }
        handle {
            on { isOk() }
            exec { someFun() }
        }
    }

    processParallel { // Perform all handlers with match = true in parallel
        timeout { Duration.ofMilliseconds(150) }
        repeatWhile { someCondition() } // Repeat starting all handlers while `someCondition()` is true
        handle {
            on { isSomething() }
            exec { someFun() }
        }
        handle {
            on { isOk() }
            exec { someFun() }
        }
    }


    // Some custom processor
    processFiles {
        readBy = ReadBy.STREAM

        setPackage { // this: MyContext ->
            PackageHandlerData(
                filePath = this.packageName, // "my/path/source_file.zip"
                fileMask = "*.xml"
            )
        }
        
        handleFile { /* this: MyContext*/ file: FileHandlerData ->
            this.inputStream = file.inputStream
            this.fileName = file.fileName
        }
        exec { someFun() }
        handleFile { /* this: MyContext */ file: FileHandlerData ->
            this.inputStream = file.inputStream
            this.fileName = file.fileName
        }


    }
    exec { // this: MyContext ->
        timeStop = Instant.now()
    }
}
```

Having that configured, you just process all data like following;
```kotlin
val context = MyContext(rawQuery = aQueryFromUser)
conv.exec(context)
val result = context.result
```

## Using in your projects

The libraries are published to kotlinx bintray repository, linked to JCenter and pushed to Maven Central.

### Maven

Add dependencies (you can also add other modules that you need):

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>1.1.1</version>
</dependency>
```
And make sure that you use the latest Kotlin version:

```xml
<properties>
    <kotlin.version>1.3.20</kotlin.version>
</properties>
```
### Gradle

Add dependencies (you can also add other modules that you need):

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1'
}
```
And make sure that you use the latest Kotlin version:

```groovy
buildscript {
    ext.kotlin_version = '1.3.20'
}
```
Make sure that you have either jcenter() or mavenCentral() in the list of repositories:

```groovy
repository {
    jcenter()
}
```

### Gradle Kotlin DSL
Add dependencies (you can also add other modules that you need):

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
}
```
And make sure that you use the latest Kotlin version:

```kotlin
plugins {
    kotlin("jvm") version "1.3.20"
}
```
Make sure that you have either jcenter() or mavenCentral() in the list of repositories.
