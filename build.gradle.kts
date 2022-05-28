import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jacocoVersion = "0.8.7"
val jacocoFolder = "coverage"

group = "com.template"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("info.solidsoft.pitest") version "1.7.4"
	id("io.gitlab.arturbosch.detekt") version "1.20.0"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
	jacoco
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("com.zaxxer:HikariCP:5.0.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}
}

tasks.register<io.gitlab.arturbosch.detekt.Detekt>("myDetekt") {
	description = "Runs a custom detekt build."
	setSource(files("src/main/kotlin", "src/test/kotlin"))
	config.setFrom(files("$rootDir/config.yml"))
	debug = true
	reports {
		html.destination = file("build/reports/detekt.html")
	}
	include("**/*.kt")
	include("**/*.kts")
	exclude("resources/")
	exclude("build/")
}

plugins.withId("info.solidsoft.pitest") {
	configure<info.solidsoft.gradle.pitest.PitestPluginExtension> {
		targetClasses.set(setOf("**.usecase.*"))
		pitestVersion.set("1.7.4")
		threads.set(4)
		timestampedReports.set(false)
		testPlugin.set("junit5")
		outputFormats.set(setOf("HTML"))
	}
}

jacoco {
	toolVersion = jacocoVersion
}

detekt {
	toolVersion = "1.20.0"
	parallel = true
	config = files("./config/detekt.yml")
	buildUponDefaultConfig = false
	disableDefaultRuleSets = false
	debug = false
	ignoreFailures = false
}
