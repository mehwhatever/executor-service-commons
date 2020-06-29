buildscript {
    apply(from = "versions.gradle.kts")
}

plugins {
    kotlin("jvm") version "${project.extra["kotlinVersion"]}"
    id("org.jlleitschuh.gradle.ktlint") version "${project.extra["ktlintVersion"]}"
    jacoco
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${project.extra["junit5Version"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.extra["junit5Version"]}")
    testImplementation("io.mockk:mockk:${project.extra["mockKVersion"]}")
    testImplementation("org.awaitility:awaitility:4.0.3")
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
