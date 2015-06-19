/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraft.util.ResourceLocation;

public enum EnumTextures
{
    TURRET_T1_CROSSBOW("textures/entities/turrets/t1_crossbow.png"),
    TURRET_T1_CROSSBOW_GLOW("textures/entities/turrets/t1_crossbow_glow.png"),
    TURRET_T2_REVOLVER("textures/entities/turrets/t2_revolver.png"),
    TURRET_T2_REVOLVER_GLOW("textures/entities/turrets/t2_revolver_glow.png"),

    PROJECTILE_BULLET("textures/entities/projectiles/bullet.png"),

    GUI_BUTTONS("textures/gui/buttons.png"),
    GUI_TCU_TARGETS("textures/gui/tcu/page_targets.png"),
    GUI_TCU_UPGRADES("textures/gui/tcu/page_upgrades.png"),
    GUI_TCU_INFO("textures/gui/tcu/page_info.png"),

    TURRET_FORCEFIELD_P1("textures/entities/shield_1.png"),
    TURRET_FORCEFIELD_P2("textures/entities/shield_2.png"),
    TURRET_FORCEFIELD_P3("textures/entities/shield_3.png");

    private final ResourceLocation location;

    EnumTextures(String texture) {
        this.location = new ResourceLocation(TurretMod.MOD_ID, texture);
    }

    public ResourceLocation getResource() {
        return this.location;
    }

    public String getTexture() {
        return this.location.toString();
    }
}
