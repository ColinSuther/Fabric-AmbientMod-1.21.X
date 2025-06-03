package net.colinsuther.ambientmod.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class TorchEmberParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    public TorchEmberParticle(ClientWorld clientWorld, double x, double y, double z,
                              SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);
        this.velocityX *= 0.5;
        this.velocityY *= 0.6;
        this.velocityZ *= 0.5;
        this.spriteProvider = spriteProvider;

        this.maxAge = 60;
        this.setSpriteForAge(spriteProvider);

        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880; // Maximum light level
    }
        @Override
        public ParticleTextureSheet getType() {
            return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
        }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new TorchEmberParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }

    //Helper method for the tick() method below it.
    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float) this.age / this.maxAge;
        setSpriteForAge(this.spriteProvider); // Important for animated textures

        // === Color transitions ===
        final float stage1 = 0.25f;
        final float stage2 = 0.7f;

        if (progress < stage1) {
            float t = progress / stage1;
            this.red = 1.0f;
            this.green = lerp(1.0f, 0.5f, t);
            this.blue = lerp(1.0f, 0.0f, t);
        } else if (progress < stage2) {
            float t = (progress - stage1) / (stage2 - stage1);
            this.red = 1.0f;
            this.green = lerp(0.5f, 0.0f, t);
            this.blue = 0.0f;
        } else {
            float t = (progress - stage2) / (1.0f - stage2);
            this.red = lerp(1.0f, 0.2f, t);
            this.green = lerp(0.0f, 0.2f, t);
            this.blue = lerp(0.0f, 0.2f, t);
        }

        // === Flicker effect ===
        float flicker = (this.random.nextFloat() * 0.2f) + 0.9f;
        this.alpha = (1.0f - progress) * flicker;

        // === Wind push from player ===
        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            double dx = this.x - player.getX();
            double dz = this.z - player.getZ();
            double distanceSq = dx * dx + dz * dz;

            if (distanceSq < 4.0) {
                Vec3d velocity = player.getVelocity();
                this.velocityX += velocity.x * 0.07;
                this.velocityZ += velocity.z * 0.07;
            }
        }

        // === Damping to prevent drifting ===
        this.velocityX *= 0.95;
        this.velocityY *= 0.95;
        this.velocityZ *= 0.95;

        this.scale = 0.2f + random.nextFloat() * 0.1f;
        this.gravityStrength = -0.005f; // Gentle upward lift
        /*
        // === Dynamic scaling based on distance from player ===
        if (player != null) {
            double dx = this.x - player.getX();
            double dy = this.y - player.getY();
            double dz = this.z - player.getZ();
            double distanceSq = dx * dx + dy * dy + dz * dz;

            double distance = Math.sqrt(distanceSq);

            // Example scaling logic: closer = small, farther = large (clamped)
            float minScale = 0.1f;
            float maxScale = 1.5f;
            float scaleAtFarDistance = 32f; // distance at which particle reaches max scale

            float t = (float)Math.min(distance / scaleAtFarDistance, 1.0); // normalized [0,1]
            this.scale = lerp(minScale, maxScale, t);
        }

         */
    }

}
