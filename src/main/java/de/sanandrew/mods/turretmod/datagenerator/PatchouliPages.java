package de.sanandrew.mods.turretmod.datagenerator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.compat.patchouli.PatchouliHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "java:S1192", "java:S107"})
public final class PatchouliPages
{
    private static final ResourceLocation TEXT_PAGE            = new ResourceLocation("patchouli", "text");
    private static final ResourceLocation IMAGE_PAGE           = new ResourceLocation("patchouli", "image");
    private static final ResourceLocation CRAFTING_PAGE        = new ResourceLocation("patchouli", "crafting");
    private static final ResourceLocation SPOTLIGHT_PAGE       = new ResourceLocation("patchouli", "spotlight");
    private static final ResourceLocation CUSTOM_CRAFTING      = new ResourceLocation(TmrConstants.ID, "custom_crafting");
    private static final ResourceLocation ASSEMBLY_RECIPE_PAGE = new ResourceLocation(TmrConstants.ID, "assembly_i18n");
    private static final ResourceLocation AMMO_INFO_PAGE    = new ResourceLocation(TmrConstants.ID, "ammo_info_i18n");
    private static final ResourceLocation TURRET_INFO_PAGE  = new ResourceLocation(TmrConstants.ID, "turret_info_i18n");
    private static final ResourceLocation TURRET_RANGE_PAGE = new ResourceLocation(TmrConstants.ID, "turret_range");

    private PatchouliPages() {}

    @SuppressWarnings("java:S1700")
    public static class Text
            extends PatchouliBuilder.Page<Text>
    {
        private final String text;
        private       String title;

        public Text(String text) {
            super(TEXT_PAGE);

            this.text = text;
        }

        public Text title(String title) {
            this.title = title;

            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.addProperty("text", this.text);
            MiscUtils.accept(this.title, t -> obj.addProperty("title", t));
        }
    }

    public static class Image
            extends PatchouliBuilder.Page<Image>
    {
        private final String[] images;
        private       String title;
        private       Boolean border;
        private       String text;

        public Image(String... images) {
            super(IMAGE_PAGE);

            this.images = images;
        }

        public Image title(String title) {
            this.title = title;

            return this;
        }

        public Image text(String text) {
            this.text = text;

            return this;
        }

        public Image border(boolean border) {
            this.border = border;

            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.add("images", PatchouliHelper.toJsonArray(this.images, JsonArray::add));
            MiscUtils.accept(this.title, t -> obj.addProperty("title", t));
            MiscUtils.accept(this.text, t -> obj.addProperty("text", t));
            MiscUtils.accept(this.border, b -> obj.addProperty("border", b));
        }
    }

    public static class Crafting
            extends PatchouliBuilder.Page<Crafting>
    {
        private final ResourceLocation recipe;
        private final ResourceLocation recipe2;
        private       String           title;
        private       String           text;

        public Crafting(ResourceLocation recipe) {
            this(recipe, null);
        }

        public Crafting(ResourceLocation recipe, ResourceLocation recipe2) {
            super(CRAFTING_PAGE);
            this.recipe = recipe;
            this.recipe2 = recipe2;
        }

        public Crafting title(String title) {
            this.title = title;
            return this;
        }

        public Crafting text(String text) {
            if( this.recipe2 == null ) {
                this.text = text;
            }
            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.addProperty("recipe", this.recipe.toString());
            MiscUtils.accept(this.recipe2, r -> obj.addProperty("recipe2", this.recipe2.toString()));
            MiscUtils.accept(this.title, t -> obj.addProperty("title", this.title));
            MiscUtils.accept(this.text, t -> obj.addProperty("text", this.text));
        }
    }

    public static class Spotlight
            extends PatchouliBuilder.Page<Spotlight>
    {
        private final ItemStack[] item;
        private       String      title;
        private       String      text;
        private       Boolean     linkRecipe;

        public Spotlight(ItemStack... item) {
            super(SPOTLIGHT_PAGE);

            this.item = item;
        }

        public Spotlight(Item... item) {
            this(Arrays.stream(item).map(ItemStack::new).toArray(ItemStack[]::new));
        }

        public Spotlight title(String title) {
            this.title = title;

            return this;
        }

        public Spotlight text(String text) {
            this.text = text;

            return this;
        }

        public Spotlight linkRecipe(boolean yes) {
            this.linkRecipe = yes;

            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.addProperty("item", PatchouliHelper.getItemStr(this.item));
            MiscUtils.accept(this.title, t -> obj.addProperty("title", t));
            MiscUtils.accept(this.text, t -> obj.addProperty("text", t));
            MiscUtils.accept(this.linkRecipe, l -> obj.addProperty("link_recipe", l));
        }
    }

    public static class CustomCrafting
            extends PatchouliBuilder.Page<CustomCrafting>
    {
        private final ItemStack[]   output;
        private final ItemStack[][] inputs = new ItemStack[3 * 3][];
        private final boolean       shapeless;
        private       ItemStack[]   output2;
        private       ItemStack[][] inputs2;
        private       boolean       shapeless2;
        private       String        title;
        private       String        title2;
        private       String        text;

        public CustomCrafting(ItemStack[] input11, ItemStack[] input12, ItemStack[] input13,
                              ItemStack[] input21, ItemStack[] input22, ItemStack[] input23,
                              ItemStack[] input31, ItemStack[] input32, ItemStack[] input33,
                              ItemStack[] output)
        {
            this(input11, input12, input13, input21, input22, input23, input31, input32, input33, output, false);
        }

        public CustomCrafting(ItemStack[] input11, ItemStack[] input12, ItemStack[] input13,
                              ItemStack[] input21, ItemStack[] input22, ItemStack[] input23,
                              ItemStack[] input31, ItemStack[] input32, ItemStack[] input33,
                              ItemStack[] output, boolean shapeless)
        {
            super(CUSTOM_CRAFTING);

            this.inputs[0] = input11;
            this.inputs[1] = input12;
            this.inputs[2] = input13;
            this.inputs[3] = input21;
            this.inputs[4] = input22;
            this.inputs[5] = input23;
            this.inputs[6] = input31;
            this.inputs[7] = input32;
            this.inputs[8] = input33;

            this.output = output;
            this.shapeless = shapeless;
        }

        public CustomCrafting recipe2(ItemStack[] input11, ItemStack[] input12, ItemStack[] input13,
                                      ItemStack[] input21, ItemStack[] input22, ItemStack[] input23,
                                      ItemStack[] input31, ItemStack[] input32, ItemStack[] input33,
                                      ItemStack[] output)
        {
            return this.recipe2(input11, input12, input13, input21, input22, input23, input31, input32, input33, output, false);
        }

        public CustomCrafting recipe2(ItemStack[] input11, ItemStack[] input12, ItemStack[] input13,
                                      ItemStack[] input21, ItemStack[] input22, ItemStack[] input23,
                                      ItemStack[] input31, ItemStack[] input32, ItemStack[] input33,
                                      ItemStack[] output, boolean shapeless)
        {
            this.inputs2 = new ItemStack[3 * 3][];
            this.inputs2[0] = input11;
            this.inputs2[1] = input12;
            this.inputs2[2] = input13;
            this.inputs2[3] = input21;
            this.inputs2[4] = input22;
            this.inputs2[5] = input23;
            this.inputs2[6] = input31;
            this.inputs2[7] = input32;
            this.inputs2[8] = input33;

            this.output2 = output;
            this.shapeless2 = shapeless;

            return this;
        }

        public CustomCrafting title(String title) {
            this.title = title;

            return this;
        }

        public CustomCrafting title2(String title) {
            this.title2 = title;

            return this;
        }

        public CustomCrafting text(String text) {
            this.text = text;

            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.addProperty("output", PatchouliHelper.getItemStr(this.output));
            obj.add("inputs", PatchouliHelper.toJsonArray(this.inputs, (a, i) -> a.add(PatchouliHelper.getItemStr(i))));
            if( this.shapeless ) {
                obj.addProperty("shapeless", true);
            }
            MiscUtils.accept(this.title, t -> obj.addProperty("title", t));
            MiscUtils.accept(this.output2, oa -> obj.addProperty("output2", PatchouliHelper.getItemStr(oa)));
            MiscUtils.accept(this.inputs2, ia -> obj.add("inputs2", PatchouliHelper.toJsonArray(ia, (a, i) -> a.add(PatchouliHelper.getItemStr(i)))));
            if( this.shapeless2 ) {
                obj.addProperty("shapeless2", true);
            }
            MiscUtils.accept(this.title2, t -> obj.addProperty("title2", t));
            MiscUtils.accept(this.text, t -> obj.addProperty("text", t));
        }
    }

    public static class AssemblyRecipe
            extends PatchouliBuilder.Page<AssemblyRecipe>
    {
        private final ResourceLocation recipe;
        private       String           title;
        private       String           text;

        public AssemblyRecipe(ResourceLocation recipe) {
            super(ASSEMBLY_RECIPE_PAGE);
            this.recipe = recipe;
        }

        public AssemblyRecipe title(String title) {
            this.title = title;

            return this;
        }

        public AssemblyRecipe text(String text) {
            this.text = text;

            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.addProperty("recipe", this.recipe.toString());
            MiscUtils.accept(this.title, t -> obj.addProperty("title", t));
            MiscUtils.accept(this.text, t -> obj.addProperty("text", t));
        }
    }

    public static class AmmoInfo
            extends PatchouliBuilder.Page<AmmoInfo>
    {
        private final String                 name;
        private final List<ResourceLocation> types = new ArrayList<>();
        private       String                 text;
        private       String                 turret;

        public AmmoInfo(String name) {
            super(AMMO_INFO_PAGE);
            this.name = name;
        }

        public AmmoInfo text(String text) {
            this.text = text;
            return this;
        }

        public AmmoInfo turret(String url, String name) {
            this.turret = String.format("$(l:%s)%s$(/l)", url, name);
            return this;
        }

        public AmmoInfo type(ResourceLocation type) {
            this.types.add(type);
            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.addProperty("name", this.name);
            obj.add("ammo_types", PatchouliHelper.toJsonArray(this.types, (a, r) -> a.add(r.toString())));

            MiscUtils.accept(this.text, t -> obj.addProperty("text", t));
            MiscUtils.accept(this.turret, t -> obj.addProperty("turret", t));
        }
    }

    public static class TurretInfo
            extends PatchouliBuilder.Page<TurretRange>
    {
        private final ResourceLocation turret;
        private       String           title;
        private       String           text;
        private       Float            scale;
        private       Float            offset;

        public TurretInfo(ResourceLocation turret) {
            super(TURRET_INFO_PAGE);
            this.turret = turret;
        }

        public TurretInfo title(String title) {
            this.title = title;
            return this;
        }

        public TurretInfo text(String text) {
            this.text = text;
            return this;
        }

        public TurretInfo scale(float scale) {
            this.scale = scale;
            return this;
        }

        public TurretInfo offset(float offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.addProperty("turret", this.turret.toString());
            MiscUtils.accept(this.title, t -> obj.addProperty("title", t));
            MiscUtils.accept(this.text, t -> obj.addProperty("text", t));
            MiscUtils.accept(this.scale, s -> obj.addProperty("scale", s));
            MiscUtils.accept(this.offset, s -> obj.addProperty("offset", s));
        }
    }

    public static class TurretRange
            extends PatchouliBuilder.Page<TurretRange>
    {
        private final ResourceLocation turret;
        private       String           text;

        public TurretRange(ResourceLocation turret) {
            super(TURRET_RANGE_PAGE);
            this.turret = turret;
        }

        public TurretRange text(String text) {
            this.text = text;
            return this;
        }

        @Override
        public void fillJson(JsonObject obj) {
            obj.addProperty("turret", this.turret.toString());
            MiscUtils.accept(this.text, t -> obj.addProperty("text", t));
        }
    }
}
