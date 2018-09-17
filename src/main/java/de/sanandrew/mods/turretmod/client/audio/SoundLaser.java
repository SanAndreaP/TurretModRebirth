/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.audio;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundLaser
        extends MovingSound
{
    private final ITurretInst turret;

    public SoundLaser(ITurretInst turret) {
        super(Sounds.SHOOT_LASER, SoundCategory.NEUTRAL);
        this.turret = turret;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.00001F;
    }

    @Override
    public void update() {
        EntityLiving turretL = this.turret.get();
        if( turretL.isDead || Minecraft.getMinecraft().isGamePaused() ) {
            this.donePlaying = true;
        } else {
            this.xPosF = (float)turretL.posX;
            this.yPosF = (float)turretL.posY;
            this.zPosF = (float)turretL.posZ;

            if( this.turret.getTargetProcessor().isShooting() && this.turret.getTargetProcessor().hasAmmo() && !Minecraft.getMinecraft().isGamePaused() ) {
                this.volume = 1.0F;
            } else {
                this.volume = 0.0F;
                this.donePlaying = true;
            }
        }
    }
}
