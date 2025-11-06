plugins {
    id("java-library")
    kotlin("jvm") version "1.9.22" apply false
}

group = "io.github.robertomike"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
