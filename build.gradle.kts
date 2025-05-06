plugins {
	kotlin("jvm") version "1.9.25"
	java
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"

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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.slf4j:slf4j-api:$logVersion")
	implementation("ch.qos.logback:logback-classic:$logbackVersion")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testImplementation("org.mockito:mockito-core:$mockitoVersion")
	testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}



tasks.withType<Test> {
	useJUnitPlatform()
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
