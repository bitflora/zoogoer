package net.bitflora.zoogoer.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.bitflora.zoogoer.ZooGoerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = ZooGoerMod.MOD_ID)
public class EntityValuesManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityValuesManager.class);
    private static final Gson GSON = new Gson();

    private Map<TagKey<EntityType<?>>, Double> entityTagValues = new HashMap<>();
    private Map<EntityType<?>, Double> individualEntityValues = new HashMap<>();
    private Map<String, Double> modValues = new HashMap<>();


    static public EntityValuesManager BASE_VALUES = new EntityValuesManager("animal_scores/base");
    static public EntityValuesManager BIRD_VALUES = new EntityValuesManager("animal_scores/bord");
    static public EntityValuesManager FISH_VALUES = new EntityValuesManager("animal_scores/fish_lover");
    static public EntityValuesManager HERP_VALUES = new EntityValuesManager("animal_scores/herp_fan");
    static public EntityValuesManager MONSTER_VALUES = new EntityValuesManager("animal_scores/monster_watcher");
    static public EntityValuesManager STAR_VALUES = new EntityValuesManager("animal_scores/star_watcher");

    public EntityValuesManager(String pDirectory) {
        super(GSON, pDirectory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap,
                        ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        entityTagValues.clear();
        individualEntityValues.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            JsonElement jsonElement = entry.getValue();

            try {
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    for (Map.Entry<String, JsonElement> valueEntry : jsonObject.entrySet()) {
                        String identifier = valueEntry.getKey();
                        JsonElement valueElement = valueEntry.getValue();

                        if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
                            var value = valueElement.getAsDouble();

                            // Check if it's a tag (starts with #) or individual entity
                            if (identifier.startsWith("#")) {
                                // It's a tag reference
                                String tagName = identifier.substring(1); // Remove the #
                                ResourceLocation tagLocation = new ResourceLocation(tagName);
                                TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tagLocation);

                                entityTagValues.put(tagKey, value);
                                LOGGER.debug("Added entity tag value: {} = {}", tagName, value);
                            } else if (identifier.endsWith(":")) {
                                modValues.put(identifier, value);
                            } else {
                                // It's an individual entity type
                                try {
                                    ResourceLocation entityLocation = new ResourceLocation(identifier);
                                    EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityLocation);

                                    if (entityType != null) {
                                        individualEntityValues.put(entityType, value);
                                        LOGGER.debug("Added individual entity value: {} = {}", identifier, value);
                                    } else {
                                        LOGGER.warn("Unknown entity type: {}", identifier);
                                    }
                                } catch (Exception e) {
                                    LOGGER.error("Unable to parse entity: {}", identifier);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error parsing entity values data from {}: {} \n {}", resourceLocation, e.getMessage(), e.getStackTrace());
                e.printStackTrace();
            }
        }

        LOGGER.info("Loaded {} entity tag values and {} individual entity values from datapacks",
                   entityTagValues.size(), individualEntityValues.size());
    }

    /**
     * Get the value for an entity based on its tags and individual type
     * Individual entity types take priority over tags
     * Returns the first matching value, or defaultValue if no matches found
     */
    public Optional<Double> getEntityValue(LivingEntity entity) {

        EntityType<?> entityType = entity.getType();

        // Debug logging
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        LOGGER.debug("Checking entity: {} (type: {})", entity.getClass().getSimpleName(), entityId);

        // First check individual entity values (higher priority)
        if (individualEntityValues.containsKey(entityType)) {
            var value = individualEntityValues.get(entityType);
            LOGGER.debug("  Found individual entity value: {}", value);
            return Optional.of(value);
        }

        // Then check tag values
        for (Map.Entry<TagKey<EntityType<?>>, Double> entry : entityTagValues.entrySet()) {
            TagKey<EntityType<?>> tag = entry.getKey();
            boolean hasTag = entityType.is(tag);
            LOGGER.debug("  Tag {} - matches: {}", tag.location(), hasTag);

            if (hasTag) {
                var value = entry.getValue();
                LOGGER.debug("  Found matching tag! Returning value: {}", value);
                return Optional.of(value);
            }
        }

        String modName = entityType.toString().split(":")[0] + ":";
        if (modValues.containsKey(modName)) {
            var value = modValues.get(modName);
            LOGGER.debug("  Found mod entity value: {}", value);
            return Optional.of(value);
        }

        LOGGER.debug("  No matching values found");
        return Optional.empty();
    }

    /**
     * Get the value for a specific tag
     */
    public double getTagValue(TagKey<EntityType<?>> tag, double defaultValue) {
        return entityTagValues.getOrDefault(tag, defaultValue);
    }

    /**
     * Get all tag-value mappings (defensive copy)
     */
    public Map<TagKey<EntityType<?>>, Double> getAllTagValues() {
        return new HashMap<>(entityTagValues);
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(BASE_VALUES);
        event.addListener(BIRD_VALUES);
        event.addListener(FISH_VALUES);
        event.addListener(HERP_VALUES);
        event.addListener(MONSTER_VALUES);
    }
}