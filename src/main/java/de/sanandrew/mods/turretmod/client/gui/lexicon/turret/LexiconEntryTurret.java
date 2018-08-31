/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.turret;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.client.gui.lexicon.info.LexiconGroupInfo;
import de.sanandrew.mods.turretmod.client.gui.lexicon.info.LexiconRenderInfo;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class LexiconEntryTurret
        implements ILexiconEntry
{
    private final String id;
    private final ItemStack icon;
    final ITurret turret;

    public LexiconEntryTurret(ITurret turret) {
        this.icon =  TurretRegistry.INSTANCE.getTurretItem(turret);
        this.turret = turret;
        this.id = turret.getName();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getGroupId() {
        return LexiconGroupTurret.NAME;
    }

    @Override
    public String getPageRenderId() {
        return LexiconRenderTurret.ID;
    }

    @Nonnull
    @Override
    public ItemStack getEntryIcon() {
        return this.icon;
    }

    @Override
    public String getTitleLangKey(String modId) {
        return Lang.TURRET_NAME.get(this.turret.getName());
    }

    @Nonnull
    @Override
    public String getSrcTitle() {
        return ClientProxy.lexiconInstance.getTranslatedTitle(this);
    }

    @Nonnull
    @Override
    public String getSrcText() {
        return ClientProxy.lexiconInstance.getTranslatedText(this);
    }
}
