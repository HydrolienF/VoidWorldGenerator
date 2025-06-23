plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("maven-publish")
    signing // Add ./gradlew signArchives
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.sonarqube") version "5.0.0.4638"
}

group="fr.formiko.mc.voidworldgenerator"
version="1.3.3"
description="Generate empty world."
val mainMinecraftVersion = "1.21.6"
val supportedMinecraftVersions = "1.20 - 1.21.6"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$mainMinecraftVersion-R0.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withJavadocJar()
    withSourcesJar()
}

tasks {
    shadowJar {
        relocate("org.bstats","${project.group}.bstats")
        archiveFileName.set("${project.name}-${project.version}.jar")
    }
    assemble {
        dependsOn(shadowJar)
    }
    processResources {
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.20",
            "group" to project.group
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion(mainMinecraftVersion)
    }
}

afterEvaluate {
    tasks.withType(PublishToMavenRepository::class.java) {
        dependsOn(tasks.assemble)
    }
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])

      artifactId = project.name.lowercase()
      pom {
        packaging = "jar"
        url.set("https://github.com/HydrolienF/${project.name}")
        inceptionYear.set("2024")
        description = project.description
        licenses {
          license {
            name.set("MIT license")
            url.set("https://github.com/HydrolienF/${project.name}/blob/master/LICENSE.md")
          }
        }
        developers {
          developer {
            id.set("hydrolienf")
            name.set("HydrolienF")
            email.set("hydrolien.f@gmail.com")
          }
        }
        scm {
          connection.set("scm:git:git@github.com:HydrolienF/${project.name}.git")
          developerConnection.set("scm:git:ssh:git@github.com:HydrolienF/${project.name}.git")
          url.set("https://github.com/HydrolienF/${project.name}")
        }
      }
    }
  }
  repositories {
    maven {
        url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()

    }
  }
}

// Custom signing task using gpg -ab
val signWithGpg = tasks.register("signWithGpg") {
    dependsOn("publishMavenJavaPublicationToMavenRepository")
    group = "signing"
    description = "Sign the publication using gpg -ab"
    val filesToSign = fileTree("${buildDir}/staging-deploy/${project.group.toString().lowercase().replace('.', '/')}/${project.name.lowercase()}/${project.version}") {
        include("**/*.jar", "**/*.module", "**/*.pom")
    }
    doFirst {
        filesToSign.forEach { file ->
            val command = listOf("gpg", "-ab", "--output", "${file.absolutePath}.asc", file.absolutePath)
            println("Executing command: ${command.joinToString(" ")}")
            exec {
                commandLine = command
            }
        }
    }
}

tasks.register<Zip>("zipStagingDeploy") {
    dependsOn("signWithGpg")
    dependsOn("publishMavenJavaPublicationToMavenRepository")
    from(layout.buildDirectory.dir("staging-deploy"))
    archiveFileName.set("staging-deploy-${project.name}-${project.version}.zip")
    destinationDirectory.set(layout.buildDirectory)
}