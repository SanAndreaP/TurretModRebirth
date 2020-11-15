package de.sanandrew.mods.turretmod.client.effect;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.registry.EnumEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class EffectHandler
{
    private EffectHandler() { }

    public static void handle(EnumEffect effect, double x, double y, double z, Tuple data) {
        Minecraft mc = Minecraft.getMinecraft();
        switch( effect ) {
            case ASSEMBLY_SPARK:
                spawnAssemblySpark(mc.effectRenderer, mc.world, x, y, z);
                break;
            case SHOTGUN_SMOKE:
                spawnShotgunSmoke(mc.effectRenderer, mc.world, x, y, z, data.getValue(0), data.getValue(1));
                break;
            case CRYO_VAPOR:
                spawnCryoVapor(mc.effectRenderer, mc.world, x, y, z, data.getValue(0), data.getValue(1), data.getValue(2));
                break;
            case MINIGUN_SMOKE:
                spawnMinigunSmoke(mc.effectRenderer, mc.world, x, y, z, data.getValue(0), data.getValue(1), data.getValue(2));
                break;
            case LEVEL_UP:
                playLevelUpSFX(mc.world, x, y, z);
                break;
            case PROJECTILE_DEATH:
                spawnProjectileDeathSmoke(mc.effectRenderer, mc.world, x, y, z);
                break;
        }
    }

    private static void playLevelUpSFX(World world, double x, double y, double z) {
        world.playSound(x, y, z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
    }

    private static void spawnAssemblySpark(ParticleManager renderer, World world, double x, double y, double z) {
        renderer.addEffect(new ParticleAssemblySpark(world, x, y, z, 0.0D, 0.0D, 0.0D));
    }

    @SuppressWarnings("ConstantConditions")
    private static void spawnShotgunSmoke(ParticleManager renderer, World world, double x, double y, double z, float yaw, float pitch) {
        float rotXZ = -yaw / 180.0F * (float) Math.PI;
        float rotY = -pitch / 180.0F * (float) Math.PI - 0.1F;

        double yShift = Math.sin(rotY) * 0.6F + 1.5F;
        double xShift = Math.sin(rotXZ) * 0.6F * Math.cos(rotY);
        double zShift = Math.cos(rotXZ) * 0.6F * Math.cos(rotY);

        for( int i = 0; i < 8; i++ ) {
            renderer.addEffect(new ParticleSmokeNormal.Factory().createParticle(0, world, x + xShift, y + yShift, z + zShift,
                                                                                xShift * 0.1F + MiscUtils.RNG.randomDouble() * 0.05 - 0.025,
                                                                                yShift * 0.1F + MiscUtils.RNG.randomDouble() * 0.05 - 0.025,
                                                                                zShift * 0.1F + MiscUtils.RNG.randomDouble() * 0.05 - 0.025));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void spawnMinigunSmoke(ParticleManager renderer, World world, double x, double y, double z, float yaw, float pitch, boolean isLeft) {
        float shift = (isLeft ? 45.0F : -45.0F) / 180.0F * (float) Math.PI;
        float rotXZ = -yaw / 180.0F * (float) Math.PI;
        float rotY = -pitch / 180.0F * (float) Math.PI - 0.1F;

        double motionX = Math.sin(rotXZ) * 0.06F * Math.cos(rotY);
        double motionY = Math.sin(rotY) * 0.06F;
        double motionZ = Math.cos(rotXZ) * 0.06F * Math.cos(rotY);

        x += Math.sin(rotXZ + shift) * 0.7F * Math.cos(rotY);
        y += Math.sin(rotY) * 0.6F + 1.5F;
        z += Math.cos(rotXZ + shift) * 0.7F * Math.cos(rotY);

        for( int i = 0; i < 8; i++ ) {
            double xDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
            double yDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
            double zDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
            renderer.addEffect(new ParticleSmokeNormal.Factory().createParticle(0, world, x, y, z, motionX + xDist, motionY + yDist, motionZ + zDist));
        }
    }

    private static void spawnCryoVapor(ParticleManager renderer, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        int max = 10;
        for( int i = 0; i < max; i++ ) {
            double diffMotionX = motionX / max;
            double diffMotionY = motionY / max;
            double diffMotionZ = motionZ / max;

            double partMotionX = diffMotionX + MiscUtils.RNG.randomDouble() * 0.05D - 0.025D;
            double partMotionY = -MiscUtils.RNG.randomDouble() * 0.025D;
            double partMotionZ = diffMotionZ + MiscUtils.RNG.randomDouble() * 0.05D - 0.025D;

            renderer.addEffect(new ParticleCryoTrail(world, x - diffMotionX * i, y - diffMotionY * i, z - diffMotionZ * i, partMotionX, partMotionY, partMotionZ));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void spawnProjectileDeathSmoke(ParticleManager renderer, World world, double x, double y, double z) {
        for( int i = 0; i < 20; i++ ) {
            renderer.addEffect(new ParticleExplosion.Factory().createParticle(0, world,
                                                                              x + MiscUtils.RNG.randomDouble() * 2.0D - 1.0D,
                                                                              y + MiscUtils.RNG.randomDouble(),
                                                                              z + MiscUtils.RNG.randomDouble() * 2.0D - 1.0D,
                                                                              MiscUtils.RNG.randomGaussian() * 0.02D,
                                                                              MiscUtils.RNG.randomGaussian() * 0.02D,
                                                                              MiscUtils.RNG.randomGaussian() * 0.02D));
        }
    }
}
