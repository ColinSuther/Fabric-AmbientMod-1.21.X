package net.colinsuther.ambientmod.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

import static java.lang.Integer.MAX_VALUE;

public class HandheldFlameParticle extends SpriteBillboardParticle {
    public HandheldFlameParticle(ClientWorld clientWorld, double x, double y, double z,
                                 SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, x, y, z, 0, 0.2, 0);

        //THIS IS WHERE WE CHANGE THE PARTICLE PROPERTIES AND PARAMETERS

        //Age of particle before it decays.
        //this.maxAge = MAX_VALUE;

        /*
        //opacity
        this.alpha = 0.8f;
         */

        //
        this.setSpriteForAge(spriteProvider);

        //Changing the colour of the particle.
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;

        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
        this.gravityStrength = 0;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    //FACTORY where we implement particle factory class(of type: simple particle type).
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            //This is where we create our new custom particle(PASSING IN THE WORLD CO-ORDINATES)
            return new HandheldFlameParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }

    //We want this flame particle to be bright. So we want to override the predetermined brightness values.

    @Override
    public int getBrightness(float tint) {
        return 15728880; // Maximum light level
    }


}
