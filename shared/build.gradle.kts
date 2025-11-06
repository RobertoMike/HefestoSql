plugins {
    id("java-library")
    kotlin("jvm") version "1.9.22"
    id("com.vanniktech.maven.publish")
}

group = "io.github.robertomike"
version = "1.1.3"

repositories {
    mavenCentral()
}

val jdkCompileVersion = 17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jdkCompileVersion))
    }
}

kotlin {
    jvmToolchain(jdkCompileVersion)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = jdkCompileVersion.toString()
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")

    implementation(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
}

// Exclude Java source files from compilation to avoid duplicates with Kotlin
tasks.named<JavaCompile>("compileJava") {
    exclude("**/*.java")
}

// Configure jar task to handle duplicates by preferring Kotlin-compiled classes
tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    
    coordinates(
        groupId = project.group.toString(),
        artifactId = "hefesto-base",
        version = project.version.toString()
    )
    
    pom {
        name.set("HefestoSql - Base")
        description.set("Base classes for HefestoSql - an open-source Kotlin/Java library for creating queries with Hibernate")
        url.set("https://github.com/RobertoMike/HefestoSql")
        inceptionYear.set("2024")
        
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        
        developers {
            developer {
                id.set("robertomike")
                name.set("Roberto Micheletti")
                email.set("rmworking@hotmail.com")
                url.set("https://github.com/RobertoMike")
            }
        }
        
        scm {
            connection.set("scm:git:git://github.com/RobertoMike/HefestoSql.git")
            developerConnection.set("scm:git:ssh://git@github.com:RobertoMike/HefestoSql.git")
            url.set("https://github.com/RobertoMike/HefestoSql")
        }
    }
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}
