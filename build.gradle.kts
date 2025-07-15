plugins {
    kotlin("jvm") version "2.2.0"
    jacoco
}

jacoco {
    toolVersion = "0.8.11"
}

group = "io.github.rjhdby"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

kotlin {
    jvmToolchain(11)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(false)
        csv.required.set(false)
    }
    dependsOn(tasks.test)
}
