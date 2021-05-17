/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.tooltip;

import de.sanandrew.mods.turretmod.client.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class LineInfoBar<T>
        implements TooltipLine<T>
{
    Minecraft mc;

    {
        if( this.mc == null ) {
            this.mc = Minecraft.getMinecraft();
        }
    }

    @Override
    public int getWidth(T object) {
        return 60;
    }

    @Override
    public int getHeight(T object) {
        return 9;
    }

    public static class LineHealthBar<T>
            extends LineInfoBar<T>
    {
        private final float health;
        private final float maxHealth;

        public LineHealthBar(float health, float maxHealth) {
            this.health = health;
            this.maxHealth = maxHealth;
        }

        @Override
        public void renderLine(int x, int y, float partTicks) {
            this.mc.renderEngine.bindTexture(Textures.GUI_TCU_HUD.getResource());

            Gui.func_146110_a(x, y, 0, 0, 9, 9, 128, 128);
            Gui.func_146110_a(x + 10, y + 2, 0, 9, 50, 5, 128, 128);
            Gui.func_146110_a(x + 10, y + 2, 0, 14, Math.round(this.health / this.maxHealth * 50.0F), 5, 128, 128);
        }
    }

    public static class LineAmmoBar<T>
            extends LineInfoBar<T>
    {
        private final int ammo;
        private final int maxAmmo;

        public LineAmmoBar(int ammo, int maxAmmo) {
            this.ammo = ammo;
            this.maxAmmo = maxAmmo;
        }

        @Override
        public void renderLine(int x, int y, float partTicks) {
            this.mc.renderEngine.bindTexture(Textures.GUI_TCU_HUD.getResource());

            Gui.func_146110_a(x, y, 9, 0, 9, 9, 128, 128);
            Gui.func_146110_a(x + 10, y + 2, 0, 19, 50, 5, 128, 128);
            Gui.func_146110_a(x + 10, y + 2, 0, 24, Math.round(this.ammo / (float) this.maxAmmo * 50.0F), 5, 128, 128);
        }
    }
}
