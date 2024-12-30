package fr.formiko.mc.voidworldgenerator;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Generate empty chunks with the config biome.
 */
public class VoidWorldGeneratorPlugin extends JavaPlugin {
    private ConfigSettings configSettings;
    @Override
    public void onEnable() {
        new Metrics(this, 20171);
        saveDefaultConfig();
        configSettings = new ConfigSettings();
    }

    public static VoidWorldGeneratorPlugin getInstance() { return getPlugin(VoidWorldGeneratorPlugin.class); }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) { return new VoidChunkGenerator(worldName); }

    private class VoidChunkGenerator extends ChunkGenerator {
        private final String worldName;
        private VoidChunkGenerator(String worldName) { this.worldName = worldName; }

        @Override
        public List<BlockPopulator> getDefaultPopulators(World world) { return List.of(); }

        @Override
        public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                @NotNull ChunkData chunkData) {
            // No need to generate noise, we want an empty world
        }
        @Override
        public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                @NotNull ChunkData chunkData) {
            // No need to generate surface, we want an empty world
        }
        @Override
        public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                @NotNull ChunkData chunkData) {
            // No need to generate bedrock, we want an empty world
        }
        @Override
        public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                @NotNull ChunkData chunkData) {
            // No need to generate caves, we want an empty world
        }

        @Override
        @Nullable
        public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) { return new VoidBiomeProvider(worldName); }

        @Override
        public boolean canSpawn(World world, int x, int z) { return true; }

        @Override
        public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, configSettings.getSpawnX(worldName), configSettings.getSpawnY(worldName),
                    configSettings.getSpawnZ(worldName));
        }
    }
    private class VoidBiomeProvider extends BiomeProvider {
        private final String worldName;
        private VoidBiomeProvider(String worldName) { this.worldName = worldName; }

        @Override
        public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) { return configSettings.getBiome(worldName); }

        @Override
        public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) { return List.of(configSettings.getBiome(worldName)); }

    }
}
