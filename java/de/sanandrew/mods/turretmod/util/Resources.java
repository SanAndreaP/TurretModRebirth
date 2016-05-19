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

public enum Resources
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
    GUI_TCU_HUD("textures/gui/tcu/hud.png"),
    GUI_TOOLTIP_HOLOGRAPH("textures/gui/holo_tooltip.png"),
    GUI_ASSEMBLY_CRF("textures/gui/turretassembly/manual.png"),
    GUI_ASSEMBLY_FLT("textures/gui/turretassembly/filter.png"),
    GUI_POTATOGEN("textures/gui/potatogen.png"),
    GUI_TURRETINFO("textures/gui/turretinfo/backg.png"),

    TINFO_GRP_AMMO("textures/gui/turretinfo/groups/ammo.png"),
    TINFO_GRP_INFO("textures/gui/turretinfo/groups/info.png"),
    TINFO_GRP_MISC("textures/gui/turretinfo/groups/misc.png"),
    TINFO_GRP_TURRET("textures/gui/turretinfo/groups/turrets.png"),
    TINFO_GRP_UPGRADE("textures/gui/turretinfo/groups/upgrades.png"),
    TINFO_GRP_STENCIL("textures/gui/turretinfo/stencil_grp.png"),

    TURRET_FORCEFIELD_P1("textures/entities/shield_1.png"),
    TURRET_FORCEFIELD_P2("textures/entities/shield_2.png"),
    TURRET_FORCEFIELD_P3("textures/entities/shield_3.png"),
    TILE_ITEM_TRANSMITTER("textures/blocks/item_transmitter.png"),
    TILE_ITEM_TRANSMITTER_GLOW("textures/blocks/item_transmitter_glow.png"),
    TILE_TURRET_ASSEMBLY("textures/blocks/turret_assembly.png"),

    SHADER_CATEGORY_BUTTON_FRAG("shader/categorybtn.frag");

    private final ResourceLocation location;

    Resources(String texture) {
        this.location = new ResourceLocation(TurretModRebirth.ID, texture);
    }

    public ResourceLocation getResource() {
        return this.location;
    }

    @Override
    public String toString() {
        return this.location.toString();
    }
}
