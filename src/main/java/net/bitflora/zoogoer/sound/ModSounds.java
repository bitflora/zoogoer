package net.bitflora.zoogoer.sound;

import net.bitflora.zoogoer.ZooGoerMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ZooGoerMod.MOD_ID);


    public static final RegistryObject<SoundEvent> STEVE_CRIKEY =
            registerSoundEvent("entity.steve_entity.crikey");

    public static final RegistryObject<SoundEvent> STEVE_LOOK =
            registerSoundEvent("entity.steve_entity.look");

    public static final RegistryObject<SoundEvent> STEVE_HURT =
            registerSoundEvent("entity.steve_entity.hurt");


    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(ZooGoerMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
