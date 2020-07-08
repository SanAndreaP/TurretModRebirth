package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;

public class ComponentCustomCrafting
        implements ICustomComponent
{
    @SerializedName("recipe_pattern_1")
    public  String recipePattern1;
    @SerializedName("recipe_pattern_2")
    public  String recipePattern2;
    @SerializedName("recipe_mappings_1")
    public JsonArray recipeMappings1;
    @SerializedName("recipe_mappings_2")
    public JsonArray recipeMappings2;

    @Override
    public void build(int i, int i1, int i2) {

    }

    @Override
    public void render(IComponentRenderContext iComponentRenderContext, float v, int i, int i1) {

    }
}
