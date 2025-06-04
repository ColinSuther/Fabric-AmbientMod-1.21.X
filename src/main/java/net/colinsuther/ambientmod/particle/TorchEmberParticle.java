package net.colinsuther.ambientmod.particle;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class TorchEmberParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;
    private final double baseVerticalVelocity;

    private float swirlAngle;
    private float swirlSpeed;
    private float swirlRadius;

    public TorchEmberParticle(ClientWorld clientWorld, double x, double y, double z,
                              SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);
        this.spriteProvider = spriteProvider;

        BlockPos posBelow = BlockPos.ofFloored(x, y - 1, z);
        BlockState blockBelow = world.getBlockState(posBelow);

        //swirl initializations
        this.swirlAngle = (float)(Math.random() * 2 * Math.PI); // Random starting angle
        this.swirlSpeed = 0.2f + random.nextFloat() * 0.1f; // Controls how fast it rotates
        this.swirlRadius = 0.02f + random.nextFloat() * 0.02f; // Controls how wide the swirl is

        double initialYVelocity;
        if (blockBelow.getBlock() instanceof CampfireBlock) {
            this.maxAge = 300;
            initialYVelocity = 0.2;
        } else if (blockBelow.getBlock() instanceof FireBlock) {
            this.maxAge = 300;
            initialYVelocity = 0.2;
        } else if (blockBelow.getBlock() instanceof TorchBlock) {
            this.maxAge = 20;
            initialYVelocity = 0.12;
        } else {
            this.maxAge = 60;
            initialYVelocity = 0.12;
        }

        this.baseVerticalVelocity = initialYVelocity;

        this.velocityX *= 0.5;
        //random boost
        this.velocityY = this.baseVerticalVelocity + ySpeed * 0.5;
        this.velocityZ *= 0.5;

        this.setSpriteForAge(spriteProvider);
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
        this.scale = 0.05f + random.nextFloat() * 0.1f;
        this.gravityStrength = -0.005f;
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880; // max brightness
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
        public @Nullable Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z,
                                                 double velocityX, double velocityY, double velocityZ) {
            return new TorchEmberParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float) this.age / this.maxAge;
        setSpriteForAge(this.spriteProvider);

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

        float flicker = (this.random.nextFloat() * 0.2f) + 0.9f;
        this.alpha = (1.0f - progress) * flicker;

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

        this.swirlAngle += this.swirlSpeed;

        // Compute circular X/Z offset based on swirl
        double swirlOffsetX = Math.cos(swirlAngle) * swirlRadius;
        double swirlOffsetZ = Math.sin(swirlAngle) * swirlRadius;

        // Apply swirl by nudging position directly
        this.x += swirlOffsetX;
        this.z += swirlOffsetZ;

        this.swirlRadius += 0.001f; // Slowly widen swirl

        //dampen
        this.velocityX *= 0.95;
        this.velocityZ *= 0.95;
    }
}
