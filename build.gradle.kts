import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    kotlin("jvm") version "1.9.25"
    java
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

// Configure ktlint
ktlint {
    version.set("1.1.1") // Specify ktlint version
    verbose.set(true) // Prints additional information while running
    outputToConsole.set(true) // Print issues to console
    coloredOutput.set(true) // Colored console output
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

// Define source sets
sourceSets {
    create("integrationTest") {
        kotlin {
            srcDir("src/integrationTest/kotlin")
        }
        resources {
            srcDir("src/integrationTest/resources")
        }
    }
}

// Set duplicatesStrategy for all resource processing tasks
tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

group = "com.sealights"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

val logVersion = "2.0.17"
val logbackVersion = "1.5.18"
val mockitoVersion = "5.15.2"
val byteBuddyVersion = "1.15.11"
val springBootVersion = "3.2.6"
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:$logVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testImplementation("net.bytebuddy:byte-buddy-agent:$byteBuddyVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Integration test dependencies
    "integrationTestImplementation"("org.junit.jupiter:junit-jupiter-api")
    "integrationTestImplementation"("org.junit.jupiter:junit-jupiter-engine")
    "integrationTestImplementation"("org.json:json:20240303")
    "integrationTestImplementation"("org.skyscreamer:jsonassert:1.5.1")
    "integrationTestRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Configure integration test task
val integrationTest =
    task<Test>("integrationTest") {
        description = "Runs integration tests."
        group = "verification"

        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath

        useJUnitPlatform()

        // Make sure integration tests run after unit tests
        shouldRunAfter("test")

        // Wait for 2 minutes before running integration tests
        doFirst {
            println("Waiting for 2 minutes before running integration tests...")
            Thread.sleep(120000) // 2 minutes in milliseconds
            println("Wait completed, proceeding with integration tests.")
        }

        // Configure reports
        reports {
            junitXml.required.set(true)
        }

        // Configure test logging to show test execution in the console
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            showExceptions = true
            showCauses = true
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }

        // Add a test listener to print a summary at the end
        addTestListener(
            object : TestListener {
                var totalTests = 0
                var failedTests = 0
                var skippedTests = 0

                override fun beforeSuite(suite: TestDescriptor) {}

                override fun afterSuite(
                    suite: TestDescriptor,
                    result: TestResult,
                ) {
                    if (suite.parent == null) { // Root suite
                        println(
                            "\nTest Summary: ${result.testCount} tests, " +
                                "${result.successfulTestCount} passed, " +
                                "${result.failedTestCount} failed, " +
                                "${result.skippedTestCount} skipped",
                        )
                        println("${result.successfulTestCount} of ${result.testCount} tests completed, ${result.failedTestCount} failed")
                    }
                }

                override fun beforeTest(testDescriptor: TestDescriptor) {}

                override fun afterTest(
                    testDescriptor: TestDescriptor,
                    result: TestResult,
                ) {}
            },
        )

        // Forward system properties from command line to test JVM
        System.getProperties().forEach { key, value ->
            if (key is String && key.startsWith("API_")) {
                systemProperty(key, value)
            }
        }
    }

// Make sure the regular test task doesn't run integration tests
tasks.test {
    // Exclude integration tests from the regular test task
    exclude("**/*IntegrationTest*")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
    }
}
tasks.test {
    val byteBuddyAgentJar = configurations.testRuntimeClasspath.get().find { it.name.contains("byte-buddy-agent-$byteBuddyVersion") }

    // Ensure JAR is available before tests
    dependsOn(tasks.named("jar"))
    doFirst {
        copy {
            from(configurations.testRuntimeClasspath.get().find { it.name.contains("mockito-core-$mockitoVersion") })
            into("${layout.buildDirectory.get().asFile}/libs")
        }
        copy {
            from(byteBuddyAgentJar)
            into("${layout.buildDirectory.get().asFile}/libs")
        }
    }

    // Configure byte-buddy-agent as a Java agent
    jvmArgs("-javaagent:$byteBuddyAgentJar")

    // Disable binary results to avoid generating output.bin files
    reports {
        junitXml.required.set(true)
    }

    // Configure test logging to show test execution in the console
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    // Add a test listener to print a summary at the end
    addTestListener(
        object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}

            override fun afterSuite(
                suite: TestDescriptor,
                result: TestResult,
            ) {
                if (suite.parent == null) { // Root suite
                    println(
                        "\nTest Summary: ${result.testCount} tests, " +
                            "${result.successfulTestCount} passed, " +
                            "${result.failedTestCount} failed, " +
                            "${result.skippedTestCount} skipped",
                    )
                    println("${result.successfulTestCount} of ${result.testCount} tests completed, ${result.failedTestCount} failed")
                }
            }

            override fun beforeTest(testDescriptor: TestDescriptor) {}

            override fun afterTest(
                testDescriptor: TestDescriptor,
                result: TestResult,
            ) {}
        },
    )
}

// Task to clean test directories before tests run
task<Delete>("cleanTestDirsBefore") {
    delete("${layout.buildDirectory.get()}/test-results/test/binary")
    delete("${layout.buildDirectory.get()}/test-results/integrationTest/binary")
}

// Task to clean test directories after tests run
task<Delete>("cleanTestDirsAfter") {
    delete("${layout.buildDirectory.get()}/test-results/test/binary")
    delete("${layout.buildDirectory.get()}/test-results/integrationTest/binary")
}

// Make sure test directories are cleaned before and after tests
tasks.test {
    dependsOn("cleanTestDirsBefore")
    finalizedBy("cleanTestDirsAfter")
}

// Make sure test directories are cleaned before and after integration tests
tasks.named("integrationTest") {
    dependsOn("cleanTestDirsBefore")
    finalizedBy("cleanTestDirsAfter")
}

// Configure JaCoCo for test coverage reporting
jacoco {
    toolVersion = "0.8.11" // Use the latest version of JaCoCo
}

// Configure JaCoCo for unit tests
tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test"))
    }
}

// Configure JaCoCo for integration tests
val jacocoIntegrationTestReport =
    tasks.register<JacocoReport>("jacocoIntegrationTestReport") {
        dependsOn(tasks.named("integrationTest"))

        executionData.setFrom(files("${layout.buildDirectory.get()}/jacoco/integrationTest.exec"))

        sourceDirectories.setFrom(files(sourceSets.main.get().allSource.srcDirs))
        classDirectories.setFrom(files(sourceSets.main.get().output))

        reports {
            xml.required.set(true)
            csv.required.set(false)
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/integrationTest"))
        }
    }

// Configure integration test to generate JaCoCo execution data
tasks.named<Test>("integrationTest") {
    finalizedBy(jacocoIntegrationTestReport)

    // Enable JaCoCo for this test task
    jacoco {
        // JaCoCo will automatically create an execution data file
        // The default location is build/jacoco/integrationTest.exec
    }
}

// Create a task to generate an aggregated JaCoCo report
val jacocoAggregatedReport =
    tasks.register<JacocoReport>("jacocoAggregatedReport") {
        dependsOn(tasks.test, tasks.named("integrationTest"))

        executionData.setFrom(
            files(
                "${layout.buildDirectory.get()}/jacoco/test.exec",
                "${layout.buildDirectory.get()}/jacoco/integrationTest.exec",
            ),
        )

        sourceDirectories.setFrom(files(sourceSets.main.get().allSource.srcDirs))
        classDirectories.setFrom(files(sourceSets.main.get().output))

        reports {
            xml.required.set(true)
            csv.required.set(false)
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregated"))
        }
    }

// Add a task to format code and then build
tasks.register("formatAndBuild") {
    description = "Format Kotlin code according to ktlint style and then build"
    group = "build"

    dependsOn("ktlintFormat", "build")
}

// Make sure ktlintFormat runs before build
tasks.named("build") {
    mustRunAfter("ktlintFormat")
}

// Add ktlint check to the check task
tasks.named("check") {
    dependsOn("ktlintCheck")
}
