import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
}

val artifactName = "common-api-server"
val artifactGroup = "kr.jadekim"
val artifactVersion = "1.0.8"
group = artifactGroup
version = artifactVersion

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    val jLoggerVersion: String by project
    val commonUtilVersion: String by project
    val kotlinxSerializationVersion: String by project

    implementation("kr.jadekim:j-logger:$jLoggerVersion")
    api("kr.jadekim:common-util:$commonUtilVersion")

    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}

tasks.withType<KotlinCompile> {
    val jvmTarget: String by project

    kotlinOptions.jvmTarget = jvmTarget
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            groupId = artifactGroup
            artifactId = artifactName
            version = artifactVersion
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")

    publish = true

    setPublications("lib")

    pkg.apply {
        repo = "maven"
        name = rootProject.name
        setLicenses("MIT")
        setLabels("kotlin")
        vcsUrl = "https://github.com/jdekim43/common-api-server.git"
        version.apply {
            name = artifactVersion
            released = Date().toString()
        }
    }
}