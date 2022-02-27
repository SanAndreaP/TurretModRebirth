package de.sanandrew.mods.turretmod.client.gui.element.tcu.levels;

import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.Stage;
import net.minecraft.entity.ai.attributes.Attribute;

public class Modifier
        extends ElementParent<String>
{
    private static final String BACKGROUND = "background";
    private static final String LABEL = "label";
    private static final String VALUE = "value";

    private final Attribute attrib;
}
