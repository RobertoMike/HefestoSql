plugins {
    kotlin("jvm")
    id("java-library")
    id("com.vanniktech.maven.publish")
}

group = "io.github.robertomike"
version = "2.0.3"

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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = jdkCompileVersion.toString()
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

sourceSets {
    main {
        java {
            exclude("**/*")
        }
    }
}

dependencies {
    implementation("org.hibernate:hibernate-core:6.0.0.Final")
    api("org.hibernate:hibernate-core:6.0.0.Final")

    implementation(project(":hefesto-hibernate-base"))
    api(project(":hefesto-hibernate-base"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib")

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

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    
    coordinates(
        groupId = project.group.toString(),
        artifactId = "hefesto-hibernate-hql",
        version = project.version.toString()
    )
    
    pom {
        name.set("HefestoSql - Hibernate Query Language")
        description.set("HefestoSql Hibernate Query Language support - an open-source Kotlin/Java library for creating queries with Hibernate")
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
            developerConnection.set("scm:git:ssh://git@github.com/RobertoMike/HefestoSql.git")
            url.set("https://github.com/RobertoMike/HefestoSql")
        }
    }
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}
