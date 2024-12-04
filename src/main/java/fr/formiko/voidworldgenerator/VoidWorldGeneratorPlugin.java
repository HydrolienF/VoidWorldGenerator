package fr.formiko.voidworldgenerator;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

/**
 * Generate empty chunks with the config biome.
 */
public class VoidWorldGeneratorPlugin extends JavaPlugin {
    private static Biome EMPTY_CHUNK_BIOME;
    @Override
    public void onEnable() {
        new Metrics(this, 20171);
        saveDefaultConfig();
        try {
            EMPTY_CHUNK_BIOME = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME)
                    .get(NamespacedKey.minecraft(getConfig().getString("emptyChunkBiome", "the_void").toLowerCase()));
        }catch (Exception e) {
            EMPTY_CHUNK_BIOME = null;
        }
        if(EMPTY_CHUNK_BIOME == null) {
            getLogger().warning("Biome not found, using THE_VOID");
            EMPTY_CHUNK_BIOME = Biome.THE_VOID;
        } else {
            getLogger().info("Using biome " + EMPTY_CHUNK_BIOME.getKey().getKey());
        }
    }

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
        public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, getConfig().getInt("spawn.x", 0), getConfig().getInt("spawn.y", 64), getConfig().getInt("spawn.z", 0));
        }
    }
    private class VoidBiomeProvider extends BiomeProvider {

        @Override
        public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) { return EMPTY_CHUNK_BIOME; }

        @Override
        public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) { return List.of(EMPTY_CHUNK_BIOME); }

    }
}
