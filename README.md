[download]: https://img.shields.io/github/downloads/HydrolienF/VoidWorldGenerator/total
[downloadLink]: https://hangar.papermc.io/Hydrolien/VoidWorldGenerator
[discord-shield]: https://img.shields.io/discord/728592434577014825?label=discord
[discord-invite]: https://discord.gg/RPNbtRSFqG

[ ![download][] ][downloadLink]
[ ![discord-shield][] ][discord-invite]

[**Discord**](https://discord.gg/RPNbtRSFqG) | [**Hangar**](https://hangar.papermc.io/Hydrolien/VoidWorldGenerator) | [**Spigot**](https://www.spigotmc.org/resources/voidworldgenerator.113931/) | [**GitHub**](https://github.com/HydrolienF/VoidWorldGenerator)

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

## Build

Build with `./gradlew assemble`. Plugin file will be in `build/libs/`.

Build for publication with `./gradlew clean zipStagingDeploy`