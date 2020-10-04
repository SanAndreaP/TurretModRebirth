package de.sanandrew.mods.turretmod.client.particle;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.registry.EnumEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ParticleHandler
{
    private ParticleHandler() { }

    @SuppressWarnings("ConstantConditions")
    public static void handle(EnumEffect effect, double x, double y, double z, Tuple data) {
        Minecraft mc = Minecraft.getMinecraft();
        switch( effect ) {
            case ASSEMBLY_SPARK:
                mc.effectRenderer.addEffect(new ParticleAssemblySpark(mc.world, x, y, z, 0.0D, 0.0D, 0.0D));
                break;
            case SHOTGUN_SHOT: {
                float rotXZ = -data.<Float>getValue(0) / 180.0F * (float) Math.PI;
                float rotY = -data.<Float>getValue(1) / 180.0F * (float) Math.PI - 0.1F;

                double yShift = Math.sin(rotY) * 0.6F + 1.5F;
                double xShift = Math.sin(rotXZ) * 0.6F * Math.cos(rotY);
                double zShift = Math.cos(rotXZ) * 0.6F * Math.cos(rotY);

                for( int i = 0; i < 8; i++ ) {
                    Particle fx = new ParticleSmokeNormal.Factory().createParticle(0, mc.world,
                                                                                   x + xShift,
                                                                                   y + yShift,
                                                                                   z + zShift,
                                                                                   xShift * 0.1F + MiscUtils.RNG.randomDouble() * 0.05 - 0.025,
                                                                                   yShift * 0.1F + MiscUtils.RNG.randomDouble() * 0.05 - 0.025,
                                                                                   zShift * 0.1F + MiscUtils.RNG.randomDouble() * 0.05 - 0.025);
                    mc.effectRenderer.addEffect(fx);
                }
                break;
            }
            case CRYO_PARTICLE: {
                int max = 10;
                for( int i = 0; i < max; i++ ) {
                    double diffMotionX = (double) data.getValue(0) / max;
                    double diffMotionY = (double) data.getValue(1) / max;
                    double diffMotionZ = (double) data.getValue(2) / max;

                    double partMotionX = diffMotionX + MiscUtils.RNG.randomDouble() * 0.05D - 0.025D;
                    double partMotionY = -MiscUtils.RNG.randomDouble() * 0.025D;
                    double partMotionZ = diffMotionZ + MiscUtils.RNG.randomDouble() * 0.05D - 0.025D;
                    mc.effectRenderer.addEffect(new ParticleCryoTrail(mc.world, x - diffMotionX * i, y - diffMotionY * i, z - diffMotionZ * i, partMotionX, partMotionY, partMotionZ));
                }
                break;
            }
            case MINIGUN_SHOT: {
                boolean isLeft = data.getValue(2);
                float shift = (isLeft ? 45.0F : -45.0F) / 180.0F * (float) Math.PI;
                float rotXZ = -(float) data.getValue(0) / 180.0F * (float) Math.PI;
                float rotY = -(float) data.getValue(1) / 180.0F * (float) Math.PI - 0.1F;

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
                    Particle fx = new ParticleSmokeNormal.Factory().createParticle(0, mc.world, x, y, z, motionX + xDist, motionY + yDist, motionZ + zDist);
                    mc.effectRenderer.addEffect(fx);
                }
                break;
            }
            case LEVEL_UP: {
                mc.world.playSound(x, y, z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
            }
            case PROJECTILE_DEATH: {
                for( int i = 0; i < 20; i++ ) {
                    Particle fx = new ParticleExplosion.Factory().createParticle(0, mc.world,
                                                                                 x + MiscUtils.RNG.randomDouble() * 2.0D - 1.0D,
                                                                                 y + MiscUtils.RNG.randomDouble(),
                                                                                 z + MiscUtils.RNG.randomDouble() * 2.0D - 1.0D,
                                                                                 MiscUtils.RNG.randomGaussian() * 0.02D,
                                                                                 MiscUtils.RNG.randomGaussian() * 0.02D,
                                                                                 MiscUtils.RNG.randomGaussian() * 0.02D);
                    mc.effectRenderer.addEffect(fx);
                }
            }
        }
    }
}
