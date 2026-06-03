plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
    id("maven-publish")
    signing // Add ./gradlew signArchives
    id("xyz.jpenilla.run-paper") version "3.0.2" // Paper server for testing/hotloading JVM
    id("org.sonarqube") version "7.3.0.8198" // Advanced code quality checks
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
    id("com.modrinth.minotaur") version "2.+" // cf https://github.com/modrinth/minotaur
    id("org.jreleaser") version "1.24.0"
}

group="fr.formiko.mc.voidworldgenerator"
version="1.3.12"
description="Generate empty world."
val mainMinecraftVersion = "1.21.11" // 26.1.2
val supportedMinecraftVersions = "1.20 - 26.1.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$mainMinecraftVersion-R0.1-SNAPSHOT")
    // compileOnly("io.papermc.paper:paper-api:$mainMinecraftVersion.build.+")
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21)) // 25
    withJavadocJar()
    withSourcesJar()
}

sonar {
  properties {
    property("sonar.projectKey", project.name)
    property("sonar.projectName", project.name)
    property("sonar.host.url", "https://mvndisonar.formiko.fr")
  }
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

// afterEvaluate {
//     tasks.withType(PublishToMavenRepository::class.java) {
//         dependsOn(tasks.assemble)
//     }
// }

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])

      artifactId = project.name.lowercase()
      pom {
        name.set(project.name.lowercase())
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
        // url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        name = "PreDeploy"
        url = uri(layout.buildDirectory.dir("pre-deploy"))

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

tasks.register("echoVersion") {
    doLast {
        println("${project.version}")
    }
}

tasks.register("echoReleaseName") {
    doLast {
        println("${project.version} [${supportedMinecraftVersions}]")
    }
}

val extractChangelog = tasks.register("extractChangelog") {
    group = "documentation"
    description = "Extracts the changelog for the current project version from CHANGELOG.md, including the version header."

    val changelog = project.objects.property(String::class)
    outputs.upToDateWhen { false }

    doLast {
        val version = project.version.toString()
        val changelogFile = project.file("CHANGELOG.md")

        if (!changelogFile.exists()) {
            println("CHANGELOG.md not found.")
            changelog.set("No changelog found.")
            return@doLast
        }

        val lines = changelogFile.readLines()
        val entries = mutableListOf<String>()
        var foundVersion = false

        for (line in lines) {
            when {
                // Include the version line itself
                line.trim().equals("# $version", ignoreCase = true) -> {
                    foundVersion = true
                    entries.add(line)
                }
                // Stop collecting at the next version header
                foundVersion && line.trim().startsWith("# ") -> break
                // Collect lines after the version header
                foundVersion -> entries.add(line)
            }
        }

        val result = if (entries.isEmpty()) {
            "Update to $version."
        } else {
            entries.joinToString("\n").trim()
        }

        // println("Changelog for version $version:\n$result")
        changelog.set(result)
    }

    // Make changelog accessible from other tasks
    extensions.add(Property::class.java, "changelog", changelog)
}

tasks.register("echoLatestVersionChangelog") {
    group = "documentation"
    description = "Displays the latest version change."

    dependsOn(tasks.named("extractChangelog"))

    doLast {
        println((extractChangelog.get().extensions.getByName("changelog") as Property<String>).get())
    }
}

val versionString: String = version as String
val isRelease: Boolean = !versionString.contains("SNAPSHOT")

hangarPublish { // ./gradlew publishPluginPublicationToHangar
    publications.register("plugin") {
        version.set(project.version as String)
        channel.set(if (isRelease) "Release" else "Snapshot")
        id.set(project.name)
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        changelog.set(
            extractChangelog.map {
                (it.extensions.getByName("changelog") as Property<String>).get()
            }
        )
        platforms {
            register(io.papermc.hangarpublishplugin.model.Platforms.PAPER) {
                url = "https://github.com/HydrolienF/"+project.name+"/releases/download/"+versionString+"/"+project.name+"-"+versionString+".jar"

                // Set platform versions from gradle.properties file
                val versions: List<String> = supportedMinecraftVersions.replace(" ", "").split(",")
                platformVersions.set(versions)
            }
        }
    }
}

// Do an array of game versions from supportedMinecraftVersions
fun expandMinecraftVersions(range: String): List<String> {

    val latestPatches = linkedMapOf(
        "1.20" to 6,
        "1.21" to 11,
        "26.1" to 2
    )

    data class Version(
        val base: String,
        val patch: Int
    )

    fun parse(version: String): Version {
        val parts = version.trim().split('.')

        return if (parts.size <= 2) {
            Version(parts.joinToString("."), 0)
        } else {
            Version(
                parts.dropLast(1).joinToString("."),
                parts.last().toInt()
            )
        }
    }

    val (startStr, endStr) = range.split(" - ").map(String::trim)

    val start = parse(startStr)
    val end = parse(endStr)

    val orderedBases = latestPatches.keys.toList()

    val startIndex = orderedBases.indexOf(start.base)
    val endIndex = orderedBases.indexOf(end.base)

    require(startIndex != -1) {
        "Unknown Minecraft version base: ${start.base}"
    }

    require(endIndex != -1) {
        "Unknown Minecraft version base: ${end.base}"
    }

    require(startIndex <= endIndex) {
        "Start version must be before end version"
    }

    val result = mutableListOf<String>()

    for (i in startIndex..endIndex) {

        val base = orderedBases[i]

        val fromPatch =
            if (base == start.base) start.patch else 0

        val toPatch =
            if (base == end.base)
                end.patch
            else
                latestPatches[base]!!

        for (patch in fromPatch..toPatch) {
            result += if (patch == 0) {
                base
            } else {
                "$base.$patch"
            }
        }
    }

    return result
}

tasks.register("echoSupportedMinecraftVersions") {
    group = "documentation"
    description = "Displays the supported Minecraft versions."
    doLast {
        println("${expandMinecraftVersions(supportedMinecraftVersions).joinToString(", ")}")
    }
}


modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) // Remember to have the MODRINTH_TOKEN environment variable set or else this will fail - just make sure it stays private!
    projectId.set("${project.name.lowercase()}") // This can be the project ID or the slug. Either will work!
    versionNumber.set("${project.version}") // You don't need to set this manually. Will fail if Modrinth has this version already
    versionType.set("release") // This is the default -- can also be `beta` or `alpha`
    // uploadFile.set(tasks.jar) // With Loom, this MUST be set to `remapJar` instead of `jar`!
    uploadFile.set(layout.buildDirectory.dir("libs").get().asFile.absolutePath + "/${project.name}-${project.version}.jar")
    gameVersions.addAll(expandMinecraftVersions(supportedMinecraftVersions)) // Must be an array, even with only one version
    loaders.addAll("paper", "folia", "purpur", "spigot", "bukkit") // Must also be an array
    changelog.set(
        extractChangelog.map {
            (it.extensions.getByName("changelog") as Property<String>).get()
        }
    )
    syncBodyFrom = rootProject.file("README.md").readText()
}

tasks.named("modrinth") {
    dependsOn(tasks.named("modrinthSyncBody"))
}

jreleaser {
    project {
        name.set("${project.name}")
        copyright.set("Hydrolien")
        description.set(findProperty("description")?.toString() ?: "Default description")
        website.set("https://github.com/HydrolienF/${project.name}")
    }

    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active.set(org.jreleaser.model.Active.ALWAYS)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    username.set(findProperty("ossrhUsername")?.toString()
                        ?: System.getenv("OSSRH_USERNAME"))
                    password.set(findProperty("ossrhPassword")?.toString()
                        ?: System.getenv("OSSRH_PASSWORD"))
                    stagingRepository("build/pre-deploy")  // call as function

                    applyMavenCentralRules = false
                }
            }
        }
    }

    release {
        github {
            enabled.set(false)
        }
    }
}

signing {
    useGpgCmd() // uses local gpg executable
    sign(publishing.publications["mavenJava"])
}