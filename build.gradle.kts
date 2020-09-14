import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date

plugins {
    kotlin("jvm") version "1.4.10"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

val artifactName = "common-api-server"
val artifactGroup = "kr.jadekim"
val artifactVersion = "1.0.6"
group = artifactGroup
version = artifactVersion

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    val jLoggerVersion: String by project
    val commonUtilVersion: String by project

    implementation("kr.jadekim:j-logger:$jLoggerVersion")
    api("kr.jadekim:common-util:$commonUtilVersion")
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