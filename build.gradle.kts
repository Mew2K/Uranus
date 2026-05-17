import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "network.warzone"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://maven.maxhenkel.de/repository/public")
    maven("https://repo.pgm.fyi/snapshots")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
//    implementation("org.incendo:cloud-kotlin-coroutines-annotations:2.0.0")

    implementation("de.maxhenkel.voicechat:voicechat-api:2.5.31")
    compileOnly("tc.oc.pgm:core:0.16-SNAPSHOT") {
        exclude(group = "fr.mrmicky", module = "FastBoard")
    }
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21")
    }

    withType<ShadowJar> {
        archiveClassifier.set("") // so the final jar does not have "-all"
        mergeServiceFiles() // merge META-INF files
        minimize() // optional: reduces size by removing unused code
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}
