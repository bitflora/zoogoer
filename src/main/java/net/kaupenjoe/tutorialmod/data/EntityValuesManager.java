package net.kaupenjoe.tutorialmod.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "tutorialmod")
public class EntityValuesManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityValuesManager.class);
    private static final Gson GSON = new Gson();
    private static EntityValuesManager INSTANCE;

    private Map<TagKey<EntityType<?>>, Integer> entityTagValues = new HashMap<>();

    public EntityValuesManager() {
        super(GSON, "zoo_entity_values");
        INSTANCE = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap,
                        ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        entityTagValues.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            JsonElement jsonElement = entry.getValue();

            try {
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    for (Map.Entry<String, JsonElement> valueEntry : jsonObject.entrySet()) {
                        String tagName = valueEntry.getKey();
                        JsonElement valueElement = valueEntry.getValue();

                        if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
                            ResourceLocation tagLocation = new ResourceLocation(tagName);
                            TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tagLocation);
                            int value = valueElement.getAsInt();

                            entityTagValues.put(tagKey, value);
                            LOGGER.debug("Added entity tag value: {} = {}", tagName, value);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error parsing entity values data from {}: {}", resourceLocation, e.getMessage());
            }
        }

        LOGGER.info("Loaded {} entity tag values from datapacks", entityTagValues.size());
    }

    /**
     * Get the value for an entity based on its tags
     * Returns the first matching tag's value, or defaultValue if no tags match
     */
    public static int getEntityValue(LivingEntity entity, int defaultValue) {
        if (INSTANCE == null) return defaultValue;

        for (Map.Entry<TagKey<EntityType<?>>, Integer> entry : INSTANCE.entityTagValues.entrySet()) {
            if (entity.getType().is(entry.getKey())) {
                return entry.getValue();
            }
        }

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