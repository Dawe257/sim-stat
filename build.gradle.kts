plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.liquibase.gradle") version "2.2.1"
}

group = "com.dzhenetl"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.spring.actuator)
	implementation(libs.spring.web)
	implementation(libs.spring.data)
	implementation(libs.spring.security)
	implementation(libs.spring.security.oauth2.resource.server)
	implementation(libs.postgresql)
	implementation(libs.liquibase)
	runtimeOnly(libs.postgresql)
	compileOnly(libs.lombok)
	annotationProcessor(libs.lombok)
	developmentOnly(libs.spring.docker.compose)
	testImplementation(libs.spring.test)
	testRuntimeOnly(libs.junit)
	liquibaseRuntime(libs.liquibase)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
