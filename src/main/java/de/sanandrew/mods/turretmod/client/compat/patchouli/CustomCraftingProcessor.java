/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import javax.annotation.Nonnull;

public class CustomCraftingProcessor
        implements IComponentProcessor
{
    private static final String TITLE = "title";
    private static final String TITLE2 = "title2";
    private static final String SHAPELESS2 = "shapeless2";

    private String title;
    private String title2;
    private boolean showText;

    private boolean shapeless2;
    private IVariable[] input;
    private IVariable[] input2;

    @Override
    public void setup(@Nonnull IVariableProvider variables) {
        this.title = variables.has(TITLE)
                     ? variables.get(TITLE).asString()
                     : ClientProxy.getTooltipLines(variables.get("output").as(Ingredient.class).getItems()[0]).get(0).getString();
        this.input = variables.get("inputs").asList().toArray(new IVariable[0]);

        if( variables.has("output2") ) {
            this.title2 = variables.has(TITLE2)
                          ? variables.get(TITLE2).asString()
                          : ClientProxy.getTooltipLines(variables.get("output2").as(Ingredient.class).getItems()[0]).get(0).getString();
            if( this.title.equals(this.title2) ) {
                this.title2 = null;
            }
            this.input2 = variables.has("inputs2") ? variables.get("inputs2").asList().toArray(new IVariable[0]) : null;
            this.shapeless2 = variables.has(SHAPELESS2) && variables.get(SHAPELESS2).asBoolean();
            this.showText = false;
        } else {
            this.shapeless2 = false;
            this.showText = variables.has("text");
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public IVariable process(String key) {
        switch( key ) {
            case TITLE:
                return IVariable.wrap(this.title);
            case TITLE2:
                return MiscUtils.apply(this.title2, IVariable::wrap);
            case "show_text":
                return IVariable.wrap(this.showText);
            case SHAPELESS2:
                return IVariable.wrap(this.shapeless2);
            default:
                IVariable inpV = null;
                if( key.startsWith("input1") ) {
                    inpV = this.input[inputIdx(key)];
                } else if( key.startsWith("input2") && this.input2 != null ) {
                    inpV = this.input2[inputIdx(key)];
                }
                if( inpV != null && !inpV.asString().isEmpty() ) {
                    return inpV;
                }

                return null;
        }
    }

    private static int inputIdx(String key) {
        return Integer.parseInt(key.substring(key.length() - 1));
    }
}
