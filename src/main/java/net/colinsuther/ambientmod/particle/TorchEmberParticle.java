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

        // Color transitions:
        if (progress < 0.25f) {
            // White → Orange
            float t = progress / 0.25f;
            this.red = 1.0f;
            this.green = lerp(1.0f, 0.5f, t);
            this.blue = lerp(1.0f, 0.0f, t);
        } else if (progress < 0.7f) {
            // Orange → Red
            float t = (progress - 0.25f) / 0.45f;
            this.red = 1.0f;
            this.green = lerp(0.5f, 0.0f, t);
            this.blue = 0.0f;
        } else {
            // Red → Dark Gray (shorter)
            float t = (progress - 0.7f) / 0.3f;
            this.red = lerp(1.0f, 0.2f, t);
            this.green = lerp(0.0f, 0.2f, t);
            this.blue = lerp(0.0f, 0.2f, t);
        }
        /*
        float flicker = (this.random.nextFloat() * 0.2f) + 0.5f; // Range: ~0.5 – 1.1

        this.red *= flicker;
        this.green *= flicker;
        this.blue *= flicker;
        */
        // Optional fade out alpha
        this.alpha = 1.0f - progress;

        Vec3d playerVelocity = MinecraftClient.getInstance().player.getVelocity();

        // Get player and velocity
        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            double dx = this.x - player.getX();
            double dz = this.z - player.getZ();
            double distanceSq = dx * dx + dz * dz;

            // Only affect particles near the player
            if (distanceSq < 2.5) {
                Vec3d velocity = player.getVelocity();

                // Simulate wind push
                this.velocityX += velocity.x * 0.1;
                this.velocityZ += velocity.z * 0.1;
            }
        }

        // Optional: dampen to prevent flying away forever
        this.velocityX *= 0.96;
        this.velocityY *= 0.96;
        this.velocityZ *= 0.96;
    }

}
