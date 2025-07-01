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

@Mod.EventBusSubscriber(modid = ZooGoerMod.MOD_ID)
public class EntityValuesManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityValuesManager.class);
    private static final Gson GSON = new Gson();
    private static EntityValuesManager INSTANCE = new EntityValuesManager();

    private Map<TagKey<EntityType<?>>, Integer> entityTagValues = new HashMap<>();
    private Map<EntityType<?>, Integer> individualEntityValues = new HashMap<>();

    public EntityValuesManager() {
        super(GSON, "zoo_entity_values");
        INSTANCE = this;
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
                            int value = valueElement.getAsInt();

                            // Check if it's a tag (starts with #) or individual entity
                            if (identifier.startsWith("#")) {
                                // It's a tag reference
                                String tagName = identifier.substring(1); // Remove the #
                                ResourceLocation tagLocation = new ResourceLocation(tagName);
                                TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tagLocation);

                                entityTagValues.put(tagKey, value);
                                LOGGER.debug("Added entity tag value: {} = {}", tagName, value);
                            } else {
                                // It's an individual entity type
                                ResourceLocation entityLocation = new ResourceLocation(identifier);
                                EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityLocation);

                                if (entityType != null) {
                                    individualEntityValues.put(entityType, value);
                                    LOGGER.debug("Added individual entity value: {} = {}", identifier, value);
                                } else {
                                    LOGGER.warn("Unknown entity type: {}", identifier);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error parsing entity values data from {}: {}", resourceLocation, e.getMessage());
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
    public static int getEntityValue(LivingEntity entity, int defaultValue) {
        if (INSTANCE == null) return defaultValue;

        EntityType<?> entityType = entity.getType();

        // Debug logging
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        LOGGER.debug("Checking entity: {} (type: {})", entity.getClass().getSimpleName(), entityId);

        // First check individual entity values (higher priority)
        if (INSTANCE.individualEntityValues.containsKey(entityType)) {
            int value = INSTANCE.individualEntityValues.get(entityType);
            LOGGER.debug("  Found individual entity value: {}", value);
            return value;
        }

        // Then check tag values
        for (Map.Entry<TagKey<EntityType<?>>, Integer> entry : INSTANCE.entityTagValues.entrySet()) {
            TagKey<EntityType<?>> tag = entry.getKey();
            boolean hasTag = entityType.is(tag);
            LOGGER.debug("  Tag {} - matches: {}", tag.location(), hasTag);

            if (hasTag) {
                int value = entry.getValue();
                LOGGER.debug("  Found matching tag! Returning value: {}", value);
                return value;
            }
        }

        LOGGER.debug("  No matching values found, returning default: {}", defaultValue);
        return defaultValue;
    }

    /**
     * Get the value for a specific tag
     */
    public static int getTagValue(TagKey<EntityType<?>> tag, int defaultValue) {
        if (INSTANCE == null) return defaultValue;
        return INSTANCE.entityTagValues.getOrDefault(tag, defaultValue);
    }

    /**
     * Get all tag-value mappings (defensive copy)
     */
    public static Map<TagKey<EntityType<?>>, Integer> getAllTagValues() {
        return INSTANCE != null ? new HashMap<>(INSTANCE.entityTagValues) : new HashMap<>();
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new EntityValuesManager());
    }
}