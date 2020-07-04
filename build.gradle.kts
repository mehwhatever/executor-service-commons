buildscript {
    apply(from = "versions.gradle.kts")
}

plugins {
    kotlin("jvm") version "${project.extra["kotlinVersion"]}"
    id("org.jlleitschuh.gradle.ktlint") version "${project.extra["ktlintVersion"]}"
    id("io.gitlab.arturbosch.detekt") version "${project.extra["detektVersion"]}"
    jacoco
}

group = "com.github.mehwhatever"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${project.extra["junit5Version"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.extra["junit5Version"]}")
    testImplementation("io.mockk:mockk:${project.extra["mockKVersion"]}")
    testImplementation("org.awaitility:awaitility:${project.extra["awaitilityVersion"]}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    jvmTarget = "11"
}

detekt {
    parallel = true
    reports {
        html.enabled = true
    }
}
