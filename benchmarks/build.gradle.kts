plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("me.champeau.jmh") version "0.7.2"
}

group = "io.github.robertomike"
version = "2.1.1"

repositories {
    mavenCentral()
}

dependencies {
    // Hefesto module to benchmark (only Criteria Builder to avoid conflicts)
    implementation(project(":hefesto-hibernate"))
    
    // JMH
    jmh("org.openjdk.jmh:jmh-core:1.37")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    
    // Hibernate
    implementation("org.hibernate.orm:hibernate-core:6.0.0.Final")
    
    // H2 Database for benchmarks
    implementation("com.h2database:h2:2.1.214")
    
    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.7")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

jmh {
    iterations.set(10)  // Increased from 3 to 10 for more stable results
    warmupIterations.set(5)  // Increased from 2 to 5 for better warmup
    fork.set(3)  // Increased from 1 to 3 for more reliable results
    threads.set(1)
    benchmarkMode.set(listOf("avgt")) // Average time
    timeUnit.set("ms")
    resultFormat.set("JSON")
}
