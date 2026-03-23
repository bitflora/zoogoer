package net.bitflora.zoogoer.sound;

import net.bitflora.zoogoer.ZooGoerMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, ZooGoerMod.MOD_ID);


    public static final DeferredHolder<SoundEvent, SoundEvent> STEVE_CRIKEY =
            registerSoundEvent("entity.steve_entity.crikey");

    public static final DeferredHolder<SoundEvent, SoundEvent> STEVE_LOOK =
            registerSoundEvent("entity.steve_entity.look");

    public static final DeferredHolder<SoundEvent, SoundEvent> STEVE_HURT =
            registerSoundEvent("entity.steve_entity.hurt");


    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ZooGoerMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
