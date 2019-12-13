package de.sanandrew.mods.turretmod.client.gui.element;

public interface IGuiProgress
{
    Number getCurrentValue(String progressId);

    Number getMaxValue(String progressId);
}
