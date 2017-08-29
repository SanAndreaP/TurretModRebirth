/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoCategory;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TurretInfoCategory
        implements ITurretInfoCategory
{
    public final int index;
    private ResourceLocation catIcon;
    private String title;
    private List<ITurretInfoEntry> entries;

    public TurretInfoCategory(int index, ResourceLocation categoryIcon, String title) {
        this.index = index;
        this.catIcon = categoryIcon;
        this.title = title;
        this.entries = new ArrayList<>();
    }

    @Override
    public ITurretInfoCategory addEntry(ITurretInfoEntry... entry) {
        this.entries.addAll(Arrays.asList(entry));
        return this;
    }

    @Override
    public ResourceLocation getIcon() {
        return this.catIcon;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public ITurretInfoEntry[] getEntries() {
        return this.entries.toArray(new ITurretInfoEntry[this.entries.size()]);
    }

    @Override
    public ITurretInfoEntry getEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    public int getEntryCount() {
        return this.entries.size();
    }

    @Override
    public int getIndex() {
        return this.index;
    }
}
