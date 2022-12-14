package de.sanandrew.mods.turretmod.datagenerator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PatchouliBuilder
{
    private final String name;
    private final ResourceLocation category;
    private final Icon icon;
    private final List<Page<?>> pages = new ArrayList<>();
    private int sortnum;

    private PatchouliBuilder(String name, ResourceLocation category, Icon icon) {
        this.name = name;
        this.category = category;
        this.icon = icon;
    }

    public static PatchouliBuilder withIcon(String name, ResourceLocation category, ResourceLocation icon) {
        return new PatchouliBuilder(name, category, new Icon(icon, -1, null));
    }

    public static PatchouliBuilder withItemIcon(String name, ResourceLocation category, ResourceLocation itemId, int count) {
        return new PatchouliBuilder(name, category, new Icon(itemId, count, null));
    }

    public static PatchouliBuilder withItemIcon(String name, ResourceLocation category, ResourceLocation itemId, int count, CompoundNBT nbt) {
        return new PatchouliBuilder(name, category, new Icon(itemId, count, nbt));
    }

    public ResourceLocation getCategory() {
        return this.category;
    }

    public String getName() {
        return name;
    }


    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", this.name);
        obj.addProperty("category", this.category.toString());
        obj.addProperty("icon", this.icon.toString());

        JsonArray pgArr = new JsonArray();
        if( pages.isEmpty() ) {
            throw new JsonSyntaxException("There must be at least one page present!");
        }

        pages.forEach(p ->);
    }

    private static class Icon
    {
        private final ResourceLocation location;
        private final Integer count;
        private final CompoundNBT nbt;

        Icon(ResourceLocation locationOrItemId, int count, CompoundNBT nbt) {
            this.location = locationOrItemId;
            this.count = count < 0 ? null : count;
            this.nbt = nbt;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(location);
            if( this.count != null ) {
                sb.append("#").append(this.count);
            }
            if( this.nbt != null ) {
                sb.append(this.nbt);
            }

            return sb.toString();
        }
    }

    @SuppressWarnings("unchecked")
    public abstract static class Page<T extends Page<?>>
    {
        final ResourceLocation type;
        ResourceLocation advancement;
        String flag;
        String anchor;

        Page(ResourceLocation type) {
            this.type = type;
        }

        public T advancement(ResourceLocation advancement) {
            this.advancement = advancement;
            return (T) this;
        }

        public T flag(String flag) {
            this.flag = flag;
            return (T) this;
        }

        public T anchor(String anchor) {
            this.anchor = anchor;
            return (T) this;
        }

        public abstract void fillJson(JsonObject obj);

        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", this.type.toString());
            MiscUtils.accept(this.advancement, a -> obj.addProperty("advancement", a.toString()));
            MiscUtils.accept(this.flag, f -> obj.addProperty("flag", f));
            MiscUtils.accept(this.anchor, a -> obj.addProperty("anchor", a));
            this.fillJson(obj);

            return obj;
        }
    }

    private static final ResourceLocation CRAFTING_PAGE = new ResourceLocation("patchouli", "crafting");
    public static class CraftingPage
            extends Page<CraftingPage>
    {
        private final ResourceLocation recipe;
        private final ResourceLocation recipe2;
        private String title;
        private String text;

        public CraftingPage(ResourceLocation recipe) {
            this(recipe, null);
        }

        public CraftingPage(ResourceLocation recipe, ResourceLocation recipe2) {
            super(CRAFTING_PAGE);
            this.recipe = recipe;
            this.recipe2 = recipe2;
        }

        public CraftingPage title(String title) {
            this.title = title;
            return this;
        }

        public CraftingPage text(String text) {
            if( this.recipe2 == null ) {
                this.text = text;
            }
            return this;
        }
    }

    private static final ResourceLocation ASSEMBLY_RECIPE_PAGE = new ResourceLocation(TmrConstants.ID, "assembly");
    public static class AssemblyRecipePage
            extends Page<AssemblyRecipePage>
    {
        private final ResourceLocation recipe;

        public AssemblyRecipePage(ResourceLocation recipe) {
            super(ASSEMBLY_RECIPE_PAGE);
            this.recipe = recipe;
        }
    }

    private static final ResourceLocation AMMO_INFO_PAGE = new ResourceLocation(TmrConstants.ID, "ammo_info");
    public static class AmmoInfoPage
            extends Page<AmmoInfoPage>
    {
        private final String                 name;
        private final List<ResourceLocation> types = new ArrayList<>();
        private       String                 text;
        private String turretLink;

        public AmmoInfoPage(String name) {
            super(AMMO_INFO_PAGE);
            this.name = name;
        }

        public AmmoInfoPage text(String text) {
            this.text = text;
            return this;
        }

        public AmmoInfoPage turret(String url, String name) {
            this.turretLink = String.format("$(l:%s)%s$(/l)", url, name);
            return this;
        }

        public AmmoInfoPage type(ResourceLocation type) {
            this.types.add(type);
            return this;
        }
    }
}
