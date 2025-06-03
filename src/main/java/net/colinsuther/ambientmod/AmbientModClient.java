package net.colinsuther.ambientmod;

import net.colinsuther.ambientmod.particle.HandheldFlameParticle;
import net.colinsuther.ambientmod.particle.ModParticles;
import net.colinsuther.ambientmod.particle.TorchEmberParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.TorchBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class AmbientModClient implements ClientModInitializer{
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.HANDHELD_FLAME_PARTICLE, HandheldFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.TORCH_EMBER_PARTICLE, TorchEmberParticle.Factory::new);
        //Now we need to define what the handheld flame particle looks like. We do this in our asset folder.

        //Initializing the particle placement on top of torches.
        TorchParticleSpawner.register();
        TorchEmberParticleSpawner.register();
    }

    public class TorchEmberParticleSpawner {
        public static void register() {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (client.world == null || client.player == null) return;

                BlockPos playerPos = client.player.getBlockPos();
                for (BlockPos pos : BlockPos.iterate(
                        playerPos.add(-8, -2, -8),
                        playerPos.add(8, 2, 8))) {

                    if (client.world.getBlockState(pos).getBlock() instanceof TorchBlock) {
                        if (client.world.random.nextFloat() < 0.05f) {
                            double x = pos.getX() + 0.5;
                            double y = pos.getY() + 0.7;
                            double z = pos.getZ() + 0.5;

                            double dx = (client.world.random.nextDouble() - 0.5) * 0.02;
                            double dy = client.world.random.nextDouble() * 0.02;
                            double dz = (client.world.random.nextDouble() - 0.5) * 0.02;

                            //for (int i = 0; i < 3; i++) { // Spawn 3 particles per tick
                                client.world.addParticle(ModParticles.TORCH_EMBER_PARTICLE, x, y, z, 0, 0.01, 0);
                            //}
                        }
                    }
                }
            });
        }
    }
    //Spawn handheld flame on top of torches

    public class TorchParticleSpawner {
        //Making only one particle spawn per torch.
        private static final Set<BlockPos> spawnedTorchParticles = new HashSet<>();

        public static void register() {

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (client.world == null || client.player == null) return;

                BlockPos playerPos = client.player.getBlockPos();
                for (BlockPos pos : BlockPos.iterate(
                        playerPos.add(-8, -2, -8),
                        playerPos.add(8, 2, 8))) {

                    if (client.world.getBlockState(pos).getBlock() instanceof TorchBlock) {
                        if (!spawnedTorchParticles.contains(pos)) {
                            double x = pos.getX() + 0.5;
                            double y = pos.getY() + 0.7;
                            double z = pos.getZ() + 0.5;

                            client.world.addParticle(ModParticles.HANDHELD_FLAME_PARTICLE, x, y, z, 0, 0, 0);
                            spawnedTorchParticles.add(pos); // ✅ Track this torch
                        }
                    }
                }
            });
        }
    }

}
