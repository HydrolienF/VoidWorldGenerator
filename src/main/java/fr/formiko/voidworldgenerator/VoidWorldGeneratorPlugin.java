package fr.formiko.voidworldgenerator;

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
 * Generate empty chunks with biome THE_VOID.
 */
public class VoidWorldGeneratorPlugin extends JavaPlugin {
    @Override
    public void onEnable() { new Metrics(this, 20171); }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) { return new VoidChunkGenerator(); }

    private class VoidChunkGenerator extends ChunkGenerator {

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
        public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) { return new VoidBiomeProvider(); }

        @Override
        public boolean canSpawn(World world, int x, int z) { return true; }

        @Override
        public Location getFixedSpawnLocation(World world, Random random) { return new Location(world, 0, 100, 0); }
    }
    private class VoidBiomeProvider extends BiomeProvider {

        @Override
        public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) { return Biome.THE_VOID; }

        @Override
        public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) { return List.of(Biome.THE_VOID); }

    }
}
