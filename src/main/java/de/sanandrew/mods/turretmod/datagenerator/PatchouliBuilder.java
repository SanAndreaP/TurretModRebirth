package de.sanandrew.mods.turretmod.datagenerator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.client.compat.patchouli.PatchouliHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class PatchouliBuilder
{
    private final String           name;
    private final ResourceLocation category;
    private final Icon             icon;
    private final List<Page<?>>    pages = new ArrayList<>();
    private       Integer          sortnum;
    private       Boolean          priority;

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

    public PatchouliBuilder page(Page<?> p) {
        this.pages.add(p);

        return this;
    }

    public PatchouliBuilder sort(int index) {
        this.sortnum = index;

        return this;
    }

    public PatchouliBuilder priority(boolean yes) {
        this.priority = yes;

        return this;
    }

    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", this.name);
        obj.addProperty("category", this.category.toString());
        obj.addProperty("icon", this.icon.toString());

        if( pages.isEmpty() ) {
            throw new JsonSyntaxException("There must be at least one page present!");
        }
        obj.add("pages", PatchouliHelper.toJsonArray(pages, (a, p) -> a.add(p.toJson())));
        MiscUtils.accept(this.sortnum, n -> obj.addProperty("sortnum", n));
        MiscUtils.accept(this.priority, p -> obj.addProperty("priority", p));

        return obj;
    }

    public void build(Consumer<PatchouliBuilder> consumerIn) {
        consumerIn.accept(this);
    }

    private static class Icon
    {
        private final ResourceLocation location;
        private final Integer          count;
        private final CompoundNBT      nbt;

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
        String           flag;
        String           anchor;

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
}
