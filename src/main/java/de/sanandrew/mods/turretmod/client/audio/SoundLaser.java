/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.audio;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundLaser
        extends MovingSound
{
    private final EntityTurretLaser turret;

    public SoundLaser(EntityTurretLaser laser) {
        super(Sounds.shoot_laser, SoundCategory.NEUTRAL);
        this.turret = laser;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.00001F;
    }

    @Override
    public void update() {
        if( this.turret.isDead || Minecraft.getMinecraft().isGamePaused() ) {
            this.donePlaying = true;
        } else {
            this.xPosF = (float)this.turret.posX;
            this.yPosF = (float)this.turret.posY;
            this.zPosF = (float)this.turret.posZ;

            if( this.turret.getTargetProcessor().isShooting() && this.turret.getTargetProcessor().hasAmmo() && !Minecraft.getMinecraft().isGamePaused() ) {
                this.volume = 1.0F;
            } else {
                this.volume = 0.0F;
                this.donePlaying = true;
            }
        }
    }
}
