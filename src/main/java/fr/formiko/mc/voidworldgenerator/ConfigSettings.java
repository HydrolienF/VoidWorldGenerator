package fr.formiko.mc.voidworldgenerator;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigSettings {
    private Registry<Biome> biomeRegistry;
    private Map<String, BiomeAndSpawn> worldsSettings;
    public ConfigSettings() {
        initBiomeRegistry();

        worldsSettings = new HashMap<>();

        FileConfiguration config = VoidWorldGeneratorPlugin.getInstance().getConfig();

        try {
            config.getConfigurationSection("worlds").getKeys(false).forEach(world -> {
                Biome biome = biomeRegistry == null ? Biome.THE_VOID
                        : biomeRegistry.get(NamespacedKey
                                .minecraft(config.getString("worlds." + world + ".emptyChunkBiome", "the_void").toLowerCase()));
                if (biome == null) {
                    VoidWorldGeneratorPlugin.getInstance().getLogger()
                            .warning(() -> "Biome not found: " + config.getString("worlds." + world + ".emptyChunkBiome"));
                    biome = Biome.THE_VOID;
                }
                int x = config.getInt("worlds." + world + ".spawn.x", 0);
                int y = config.getInt("worlds." + world + ".spawn.y", 64);
                int z = config.getInt("worlds." + world + ".spawn.z", 0);
                worldsSettings.put(world, new BiomeAndSpawn(biome, x, y, z));
            });
        } catch (Exception e) {
            VoidWorldGeneratorPlugin.getInstance().getLogger().warning("Fail to read config, using default settings.");
            worldsSettings.clear();
            worldsSettings.put("*", defaultBiomeAndSpawn());
        }
        if (!worldsSettings.containsKey("*")) {
            worldsSettings.put("*", defaultBiomeAndSpawn());
        }

        VoidWorldGeneratorPlugin.getInstance().getLogger().info("Config loaded: " + worldsSettings);
    }

    private BiomeAndSpawn defaultBiomeAndSpawn() { return new BiomeAndSpawn(Biome.THE_VOID, 0, 64, 0); }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initBiomeRegistry() {
        try {
            // biomeRegistry =
            // io.papermc.paper.registry.RegistryAccess.registryAccess().getRegistry(io.papermc.paper.registry.RegistryKey.BIOME);
            Class registryAccessClass = Class.forName("io.papermc.paper.registry.RegistryAccess");
            Class registryKeyClass = Class.forName("io.papermc.paper.registry.RegistryKey");
            Object biome = registryKeyClass.getField("BIOME").get(null);
            Object registryAccess = registryAccessClass.getMethod("registryAccess").invoke(null);
            biomeRegistry = (Registry<Biome>) registryAccess.getClass().getMethod("getRegistry", registryKeyClass).invoke(registryAccess,
                    biome);
        } catch (Exception e) {
            biomeRegistry = null;
            VoidWorldGeneratorPlugin.getInstance().getLogger().info("Biome registry not found, using default biome.");
        }
    }

    public Biome getBiome(String world) { return worldsSettings.getOrDefault(world, worldsSettings.get("*")).biome; }
    public int getSpawnX(String world) { return worldsSettings.getOrDefault(world, worldsSettings.get("*")).x; }
    public int getSpawnY(String world) { return worldsSettings.getOrDefault(world, worldsSettings.get("*")).y; }
    public int getSpawnZ(String world) { return worldsSettings.getOrDefault(world, worldsSettings.get("*")).z; }

    private record BiomeAndSpawn(Biome biome, int x, int y, int z) {}
}
