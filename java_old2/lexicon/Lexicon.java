/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexicon;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.shader.Shaders;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.util.ResourceLocation;

@de.sanandrew.mods.sanlib.api.client.lexicon.Lexicon
public class Lexicon
        implements ILexicon
{
    @Override
    public String getModId() {
        return TmrConstants.ID;
    }

    @Override
    public int getGuiSizeX() {
        return 192;
    }

    @Override
    public int getGuiSizeY() {
        return 236;
    }

    @Override
    public int getNavButtonOffsetY() {
        return 214;
    }

    @Override
    public int getEntryPosX() {
        return 9;
    }

    @Override
    public int getEntryPosY() {
        return 19;
    }

    @Override
    public int getEntryWidth() {
        return 168;
    }

    @Override
    public int getEntryHeight() {
        return 183;
    }

    @Override
    public int getTitleColor() {
        return 0xFF8A4500;
    }

    @Override
    public int getTextColor() {
        return 0xFF000000;
    }

    @Override
    public int getLinkColor() {
        return 0xFF0080FF;
    }

    @Override
    public int getLinkVisitedColor() {
        return 0xFF808080;
    }

    @Override
    public int getGroupStencilId() {
        return Shaders.categoryButton;
    }

    @Override
    public ResourceLocation getGroupStencilTexture() {
        return Resources.TINFO_GRP_STENCIL.resource;
    }

    @Override
    public ResourceLocation getGroupSearchIcon() {
        return Resources.TINFO_GRP_SRC.resource;
    }

    @Override
    public boolean forceUnicode() {
        return false;
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return Resources.GUI_TURRETINFO.resource;
    }

    @Override
    public void initialize(ILexiconInst iLexiconInst) {
        ClientProxy.lexiconInstance = iLexiconInst;
        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerLexicon(iLexiconInst));
    }
}
