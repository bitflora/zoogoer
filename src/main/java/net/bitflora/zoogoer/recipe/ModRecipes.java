package net.bitflora.zoogoer.recipe;

import net.bitflora.zoogoer.ZooGoerMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ZooGoerMod.MOD_ID);

    // GemPolishingRecipe serializer disabled pending 1.21.1 Recipe API rewrite

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
