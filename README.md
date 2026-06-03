[download]: https://img.shields.io/github/downloads/HydrolienF/VoidWorldGenerator/total
[downloadLink]: https://hangar.papermc.io/Hydrolien/VoidWorldGenerator
[discord-shield]: https://img.shields.io/discord/728592434577014825?label=discord
[discord-invite]: https://discord.gg/RPNbtRSFqG

[ ![download][] ][downloadLink]
[ ![discord-shield][] ][discord-invite]

[**Discord**](https://discord.gg/RPNbtRSFqG) | [**Modrinth**](https://modrinth.com/plugin/voidworldgenerator) | [**Hangar**](https://hangar.papermc.io/Hydrolien/VoidWorldGenerator) | [**Spigot**](https://www.spigotmc.org/resources/voidworldgenerator.113931/) | [**GitHub**](https://github.com/HydrolienF/VoidWorldGenerator)

# VoidWorldGenerator
A simple minecraft plugin to generate a void world.

Compatible with `Paper`, `Spigot` & `Folia` in all version since `1.20`.
1.1.2 is the last Java 17 compatible version. Update to [Java 21](https://adoptium.net/temurin/releases/?version=21) or use [1.1.2](https://github.com/HydrolienF/VoidWorldGenerator/releases/tag/1.1.2).

It can be used to prevent world generation outside of the world border.
To prevent that fully generate your world with a plugin as [Chunky](https://www.spigotmc.org/resources/chunky.81534/) then add this plugin & configure your bukkit.yml.

# Installation
Download latest .jar on [releases tab](https://github.com/HydrolienF/VoidWorldGenerator/releases).

Place the .jar in `plugins/`.

Add next lines at the begining of bukkit.yml:
```yml
worlds:
  world:
    generator: VoidWorldGenerator
```

If your world is not called `world`, replace `world` by your world name. You can set VoidWorldGenerator as generator for more than 1 world.
```yml
worlds:
  myCustomWorld1:
    generator: VoidWorldGenerator
  a2ndCustomWorld:
    generator: VoidWorldGenerator
```

Biome to use for empty chunks & the default spawn coordinates can be edit in the plugin config in `plugins/VoidWorldGenerator/config.yml`.

## Statistics
[![bStats Graph Data](https://bstats.org/signatures/bukkit/VoidWorldGenerator.svg)](https://bstats.org/plugin/bukkit/VoidWorldGenerator/20171)

# Build, Test & deploy

Feature requests or pull requests are welcome. Concider creating an issue first to talk about your new feature before sending a pull request.

## Build

Clone the [repo](https://github.com/HydrolienF/VoidWorldGenerator) `git clone git@github.com:HydrolienF/VoidWorldGenerator.git`

Build with `./gradlew assemble`. The plugin .jar file will be in `build/libs/`.

## Test

You can test the plugin directly in a Minecraft server with `./gradlew runServer`.
You can also try it on an other server by placing the .jar in `plugins/`.
You might want to update the plugin config to fit your needs.

## Deploy

### To Github releases, Hangar & Modrinth

Push tag to git to trigger a github action release that will create a new Github release and publish to Hangar & Modrinth.
```sh
git tag 1.2.3
git push --tags
```

### to Maven central

*Require ossrhUsername & ossrhPassword in ~/.gradle/gradle.properties*

```sh
./gradlew clean publish
./gradlew jreleaserDeploy
```
The published lib will be available [on maven central](https://central.sonatype.com/artifact/fr.formiko.mc.voidworldgenerator/voidworldgenerator).
