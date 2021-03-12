package de.sanandrew.mods.turretmod.api.assembly;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>An ingredient for a recipe used by the turret assembly table.</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AssemblyIngredient
        extends CompoundIngredient
{
    private final int count;

    /**
     * <p>Creates a new ingredient defined by a list of items.</p>
     *
     * @param count  the amount of items required by this ingredient.
     * @param ingredients one or more items that represent this ingredient.
     */
    public AssemblyIngredient(int count, List<Ingredient> ingredients) {
        super(ingredients);
        this.count = count;
    }

    /**
     * <p>Returns the amount of items this ingredient should consume upon crafting.</p>
     *
     * @return the amount of items required by this ingredient.
     */
    public int getCount() {
        return this.count;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if( input == null ) {
            return false;
        }

        return input.getCount() >= this.count && super.test(input);
    }

    @Nonnull
    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Nonnull
    @Override
    public JsonElement serialize()
    {
        JsonObject json = new JsonObject();
        json.addProperty("type", Objects.requireNonNull(CraftingHelper.getID(Serializer.INSTANCE)).toString());
        json.addProperty("count", this.count);

        JsonArray jArr = new JsonArray();
        this.getChildren().forEach(i -> jArr.add(i.serialize()));

        json.add("items", jArr);

        return json;
    }

    public static class Serializer implements IIngredientSerializer<AssemblyIngredient>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Nonnull
        @Override
        public AssemblyIngredient parse(PacketBuffer buffer) {
            return new AssemblyIngredient(buffer.readVarInt(), Stream.generate(() -> Ingredient.read(buffer)).limit(buffer.readVarInt()).collect(Collectors.toList()));
        }

        @Nonnull
        @Override
        public AssemblyIngredient parse(JsonObject json) {
            int              count       = JsonUtils.getIntVal(json.get("count"), 1);
            JsonElement      items       = json.get("items");
            List<Ingredient> ingredients = new ArrayList<>();

            if( items != null && items.isJsonArray() ) {
                for( JsonElement e : items.getAsJsonArray() ) {
                    ingredients.add(CraftingHelper.getIngredient(e));
                }
            } else {
                throw new JsonSyntaxException("AssemblyIngredient needs a list of items!");
            }

            return new AssemblyIngredient(count, ingredients);
        }

        @Override
        public void write(PacketBuffer buffer, AssemblyIngredient ingredient) {
            buffer.writeVarInt(ingredient.count);

            Collection<Ingredient> children = ingredient.getChildren();
            buffer.writeVarInt(children.size());
            children.forEach(c -> c.write(buffer));
        }
    }
}
