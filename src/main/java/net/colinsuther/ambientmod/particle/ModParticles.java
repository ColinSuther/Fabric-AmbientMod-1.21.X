package net.colinsuther.ambientmod.particle;

import net.colinsuther.ambientmod.AmbientMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    //Torch ember particle
    public static final SimpleParticleType TORCH_EMBER_PARTICLE =
            registerParticle("torch_ember_particle", FabricParticleTypes.simple());

    //Now that we've registered our particle, we need to register our factory in our client class
    // so that we can use the factory class we've made in TorchEmberParticle.java.

    //Helper method. Passing in the name and particle type.
    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(AmbientMod.MOD_ID, name), particleType);
    }
    //This is where we REGISTER OUR CUSTOM PARTICLES.
    public static void registerParticles() {
        //Logger isn't neccessary.
        AmbientMod.LOGGER.info("Registering particles for " + AmbientMod.MOD_ID);
    }
    //We'll call the registerParticles() method in our AmbientMod constructor. //
}
