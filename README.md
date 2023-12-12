# VoidWorldGenerator
A simple minecraft plugin to generate a void world.

Compatible with `Paper`, `Spigot` & `Folia` in `1.20, 1.20.1, 1.20.2, 1.20.3, 1.20.4`.

It can be used to prevent world generation outside of the world border.
To prevent that fully generate your world with a plugin as [Chunky](https://www.spigotmc.org/resources/chunky.81534/) then add this plugin & configure your bukkit.yml.

# Installation
Download last .jar on https://github.com/HydrolienF/VoidWorldGenerator/releases
Place the .jar in `plugins/`
Add next lines at the begining of bukkit.yml:
```yml
worlds:
  world:
    generator: VoidWorldGenerator
```

# Build
Run `./gradlew assemble`