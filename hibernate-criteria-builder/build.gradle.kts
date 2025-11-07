plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    kotlin("jvm") version "1.9.22"
}

group = "io.github.robertomike"
version = "3.0.0"

repositories {
    mavenCentral()
}

val artifactId = "hefesto-hibernate"
val jdkCompileVersion = 17

java {
    withJavadocJar()
    withSourcesJar()
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
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

// Exclude Java source files from main compilation to avoid duplicates with Kotlin
tasks.named<JavaCompile>("compileJava") {
    exclude("**/*.java")
}

// Ensure test Java compilation happens after Kotlin compilation
tasks.named<JavaCompile>("compileTestJava") {
    dependsOn(tasks.named("compileKotlin"))
}

// Configure jar task to handle duplicates by preferring Kotlin-compiled classes
tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation(kotlin("stdlib"))
    
    implementation("org.hibernate:hibernate-core:6.0.0.Final")
    api("org.hibernate:hibernate-core:6.0.0.Final")

    implementation(project(":hefesto-hibernate-base"))
    api(project(":hefesto-hibernate-base"))

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("mysql:mysql-connector-java:8.0.28")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])

            groupId = group.toString()
            artifactId = artifactId
            version = project.version.toString()

            pom {
                name.set("HefestoSql")
                description.set("HefestoSql is an open-source Java library for creation of query with hibernate")
                url.set("https://github.com/RobertoMike/HefestoSql")
                inceptionYear.set("2024")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        name.set("Roberto Micheletti")
                        email.set("rmworking@hotmail.com")
                        organization.set("Roberto Micheletti")
                        organizationUrl.set("https://github.com/RobertoMike")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/RobertoMike/HefestoSql.git")
                    developerConnection.set("scm:git:ssh://github.com:RobertoMike/HefestoSql.git")
                    url.set("https://github.com/RobertoMike/HefestoSql")
                }
            }
        }
    }
    repositories {
        maven {
            name = "central_repository_ossrh"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
            metadataSources {
                gradleMetadata()
            }
        }
    }
}

tasks.named<Javadoc>("javadoc") {
    source = sourceSets["main"].allJava
    classpath = configurations.compileClasspath.get()
}

if (!project.hasProperty("local")) {
    signing {
        setRequired { !version.toString().endsWith("SNAPSHOT") }
        sign(publishing.publications["library"])
    }
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}
