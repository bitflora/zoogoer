package net.bitflora.zoogoer.item;

import net.bitflora.zoogoer.entity.ai.SteveWanderGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedSpawnEggItem extends Item {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnimatedSpawnEggItem.class);
    private final Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier;
    private final int backgroundColor;
    private final int highlightColor;
    private static final int ANIMATION_DURATION = 300; // 15 seconds (300 ticks)

    public AnimatedSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType,
                                int backgroundColor, int highlightColor, Properties properties) {
        super(properties);
        this.entityTypeSupplier = entityType;
        this.backgroundColor = backgroundColor;
        this.highlightColor = highlightColor;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos spawnPos = blockPos.relative(direction);
        Vec3 spawnVec = Vec3.atCenterOf(spawnPos);
        Player player = context.getPlayer();

        EntityType<? extends Mob> entityType = this.entityTypeSupplier.get();

        // Play initial sound
        level.playSound(null, spawnPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.NEUTRAL, 1.0F, 0.8F);

        // Create animation task that runs every tick
        SpawnAnimationTask task = new SpawnAnimationTask(serverLevel, spawnVec, entityType, spawnPos, player);
        scheduleNextTick(serverLevel, task);

        // Consume the item
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.CONSUME;
    }

    private void scheduleNextTick(ServerLevel level, SpawnAnimationTask task) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + 1,
                    () -> {
                        if (task.tick()) {
                            // Continue animation - schedule next tick
                            scheduleNextTick(level, task);
                        }
                        // If tick() returns false, animation is complete and mob is spawned
                    }
                ));
            }
        }, 50);


    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getHighlightColor() {
        return this.highlightColor;
    }

    // Inner class to track animation state
    private static class SpawnAnimationTask {
        private final ServerLevel level;
        private final Vec3 pos;
        private final EntityType<? extends Mob> entityType;
        private final BlockPos spawnPos;
        private final Player player;
        private int currentTick = 0;

        public SpawnAnimationTask(ServerLevel level, Vec3 pos, EntityType<? extends Mob> entityType,
                                 BlockPos spawnPos, Player player) {
            this.level = level;
            this.pos = pos;
            this.entityType = entityType;
            this.spawnPos = spawnPos;
            this.player = player;
        }

        // Returns true to continue animation, false when complete
        public boolean tick() {
            currentTick++;

            if (currentTick < ANIMATION_DURATION) {
                float progress = currentTick / (float)ANIMATION_DURATION;
                // Continue animation
                createBuildupAnimation(progress);

                // Play periodic sounds
                if (progress <= 0.75) {
                    if (currentTick % 60 == 0) { // Every 3 seconds
                        level.playSound(null, spawnPos, SoundEvents.PORTAL_AMBIENT, SoundSource.NEUTRAL, 0.5F, 1.0F + (currentTick / 300.0F));
                    } else if ((currentTick + 30) % 60 == 0) {
                        level.playSound(null, spawnPos, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.NEUTRAL, 0.5F, 1.0F + (currentTick / 300.0F));
                    }
                }

                // Crescendo sound at 75% through
                // if (currentTick == (int)(ANIMATION_DURATION * 0.75)) {
                //     level.playSound(null, spawnPos, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.NEUTRAL, 0.8F, 1.2F);
                // }

                return true; // Continue animation
            } else {
                // Animation complete - spawn the mob in 2 seconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        level.getServer().tell(new net.minecraft.server.TickTask(
                            level.getServer().getTickCount(),
                            () -> {
                                spawnMobWithFinalAnimation();
                            }
                        ));
                    }
                }, 2000);
                return false; // Stop animation
            }
        }

        private void createBuildupAnimation(float progress) {
            int particleCount = (int)(5 + progress * 15); // Increase particles over time

            // Swirling portal particles
            for (int i = 0; i < particleCount; i++) {
                double angle = (currentTick * 0.1) + (i * Math.PI * 2 / particleCount);
                double radius = 1.5 - (progress * 0.5); // Spiral inward
                double height = progress * 3.0;

                double offsetX = Math.cos(angle) * radius;
                double offsetZ = Math.sin(angle) * radius;

                level.sendParticles(
                    ParticleTypes.PORTAL,
                    pos.x + offsetX,
                    pos.y + height,
                    pos.z + offsetZ,
                    1,
                    0, 0, 0,
                    0.1
                );
            }

            // Rising particles
            if (currentTick % 5 == 0) {
                for (int i = 0; i < 3; i++) {
                    double offsetX = (level.random.nextDouble() - 0.5) * 1.0;
                    double offsetZ = (level.random.nextDouble() - 0.5) * 1.0;

                    SimpleParticleType particle = level.random.nextBoolean() ? ParticleTypes.END_ROD : ParticleTypes.SOUL_FIRE_FLAME;

                    level.sendParticles(
                        particle,
                        pos.x + offsetX,
                        pos.y,
                        pos.z + offsetZ,
                        1,
                        0, 0.3, 0,
                        0.05
                    );
                }
            }

            // Intensifying light beams as we get closer to spawn
            if (progress > 0.3 && currentTick % 2 == 0) {
                int beamCount = (int)((progress - 0.3) * 30);
                for (int i = 0; i < beamCount; i++) {
                    double angle = level.random.nextDouble() * Math.PI * 2;
                    double distance = level.random.nextDouble() * 2.0;

                    double startX = pos.x + Math.cos(angle) * distance;
                    double startZ = pos.z + Math.sin(angle) * distance;

                    // Create beam shooting toward center
                    for (int j = 0; j < 5; j++) {
                        double beamProgress = j / 5.0;
                        double beamX = startX + (pos.x - startX) * beamProgress;
                        double beamZ = startZ + (pos.z - startZ) * beamProgress;

                        SimpleParticleType particle = level.random.nextBoolean() ? ParticleTypes.END_ROD : ParticleTypes.SOUL_FIRE_FLAME;

                        level.sendParticles(
                            particle,
                            beamX,
                            pos.y + 0.5 + level.random.nextDouble() * 2.0,
                            beamZ,
                            1,
                            (pos.x - startX) * 0.1,
                            0.1,
                            (pos.z - startZ) * 0.1,
                            0.01
                        );
                    }
                }
            }

            // Intensifying dragon breath as we get closer to spawn
            if (progress > 0.5 && currentTick % 3 == 0) {
                int breathCount = (int)((progress - 0.5) * 20);
                for (int i = 0; i < breathCount; i++) {
                    renderDragonBreath(progress);
                }
            }

            if (progress < 0.7) {
                if (level.random.nextInt(10) == 1) {
                    renderExplosion(1);
                }
            }else if (progress >= 0.7 && progress <= 0.85) {
                renderExplosion(1);
            }

            if (progress >= 0.8 && progress <= 0.9) {
                renderFirework(1);
            }
        }


        private void spawnMobWithFinalAnimation() {
            // renderExplosion();
            renderVerticalLightBeams(level.random);
            renderRadialLightBeams(level.random);
            // renderFirework();

            // Final dramatic sound
            //level.playSound(null, spawnPos, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.NEUTRAL, 1.5F, 1.0F);
            level.playSound(null, spawnPos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.NEUTRAL, 0.8F, 1.2F);

            // Finally spawn the mob
            Mob entity = entityType.create(level, null, null, spawnPos, MobSpawnType.SPAWN_EGG, true, false);
            if (entity != null) {
                level.addFreshEntity(entity);
                level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos);
            }
        }

        private void renderDragonBreath(float progress) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.0;
            double offsetY = level.random.nextDouble() * progress * 2.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.0;

            level.sendParticles(
                ParticleTypes.DRAGON_BREATH,
                pos.x + offsetX,
                pos.y + offsetY,
                pos.z + offsetZ,
                1,
                0, 0.05, 0,
                0.05
            );
        }

        private void renderExplosion(int amount) {
            // Create massive explosion effect
            for (int i = 0; i < amount; i++) {
                double offsetX = (level.random.nextDouble() - 0.5) * 3.0;
                double offsetY = level.random.nextDouble() * 3.0;
                double offsetZ = (level.random.nextDouble() - 0.5) * 3.0;

                double velocityX = (level.random.nextDouble() - 0.5) * 0.5;
                double velocityY = level.random.nextDouble() * 0.5;
                double velocityZ = (level.random.nextDouble() - 0.5) * 0.5;

                level.sendParticles(
                    ParticleTypes.EXPLOSION,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    pos.z + offsetZ,
                    1,
                    velocityX,
                    velocityY,
                    velocityZ,
                    0.1
                );
            }
        }

        private void renderVerticalLightBeams(RandomSource random) {
            // Massive vertical light beams shooting up
            for (int i = 0; i < 25; i++) {
                double height = i * 0.3;
                double angle = level.random.nextDouble() * Math.PI * 2;
                double radius = 0.2 + level.random.nextDouble() * 0.4;

                double offsetX = Math.cos(angle) * radius;
                double offsetZ = Math.sin(angle) * radius;

                SimpleParticleType particle = random.nextBoolean() ? ParticleTypes.DRAGON_BREATH : ParticleTypes.SOUL_FIRE_FLAME;

                level.sendParticles(
                    particle,
                    pos.x + offsetX,
                    pos.y + height,
                    pos.z + offsetZ,
                    1,
                    0, 0.3, 0,
                    0.02
                );
            }
        }

        private void renderRadialLightBeams(RandomSource random) {
            // Radial light beams shooting outward
            for (int i = 0; i < 8; i++) {
                double angle = (Math.PI * 2 * i) / 8;
                for (int j = 0; j < 10; j++) {
                    double distance = j * 0.3;
                    double offsetX = Math.cos(angle) * distance;
                    double offsetZ = Math.sin(angle) * distance;

                    SimpleParticleType particle = random.nextBoolean() ? ParticleTypes.END_ROD : ParticleTypes.SOUL_FIRE_FLAME;
                    level.sendParticles(
                        particle,
                        pos.x + offsetX,
                        pos.y + 1.0,
                        pos.z + offsetZ,
                        1,
                        Math.cos(angle) * 0.3,
                        0,
                        Math.sin(angle) * 0.3,
                        0.05
                    );
                }
            }
        }

        private void renderFirework(int amount) {
            // Firework burst
            for (int i = 0; i < amount; i++) {
                double velocityX = (level.random.nextDouble() - 0.5) * 0.7;
                double velocityY = level.random.nextDouble() * 0.7;
                double velocityZ = (level.random.nextDouble() - 0.5) * 0.7;

                level.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    pos.x,
                    pos.y + 1.5,
                    pos.z,
                    1,
                    velocityX,
                    velocityY,
                    velocityZ,
                    0.1
                );
            }
        }
    }
}