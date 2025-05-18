plugins {
	kotlin("jvm") version "1.9.25"
	java
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"

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
val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    useJUnitPlatform()

    // Make sure integration tests run after unit tests
    shouldRunAfter("test")

    // Configure reports
    reports {
        junitXml.required.set(true)
        html.required.set(true)
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
    addTestListener(object : TestListener {
        var totalTests = 0
        var failedTests = 0
        var skippedTests = 0

        override fun beforeSuite(suite: TestDescriptor) {}

        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) { // Root suite
                println("\nTest Summary: ${result.testCount} tests, " +
                        "${result.successfulTestCount} passed, " +
                        "${result.failedTestCount} failed, " +
                        "${result.skippedTestCount} skipped")
                println("${result.successfulTestCount} of ${result.testCount} tests completed, ${result.failedTestCount} failed")
            }
        }

        override fun beforeTest(testDescriptor: TestDescriptor) {}

        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
    })

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
	val mockitoJar = "${layout.buildDirectory.get().asFile}/libs/mockito-core-${mockitoVersion}.jar"

	// Ensure JAR is available before tests
	dependsOn(tasks.named("jar"))
	doFirst {
		copy {
			from(configurations.testRuntimeClasspath.get().find { it.name.contains("mockito-core-${mockitoVersion}") })
			into("${layout.buildDirectory.get().asFile}/libs")
		}
	}

	// Add -javaagent JVM argument
	jvmArgs("-javaagent:${mockitoJar}")
}
