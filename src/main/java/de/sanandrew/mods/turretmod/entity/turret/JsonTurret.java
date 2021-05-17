package de.sanandrew.mods.turretmod.entity.turret;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.api.turret.IVariantHolder;
import de.sanandrew.mods.turretmod.entity.turret.variant.VariantContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

class JsonTurret
        implements ITurret, IVariantHolder
{
    private final ResourceLocation id;
    private final ResourceLocation model;
    private final ResourceLocation glowTexture;
    private final ResourceLocation shootSoundId;
    private final AttackType attackType;
    private final int tier;
    private final boolean isBuoy;
    private final String customRenderClass;
    private final String customModelClass;

    private VariantContainer variantContainer; // set if variant holder is present
    private ResourceLocation stdTexture;               // set if no variant holder is present

    protected SoundEvent    shootSound;   // lazy-loaded
    protected AxisAlignedBB range;        // built via array -> configurable
    protected float         health;       // configurable
    protected int           ammoCapacity; // configurable
    protected int           reloadTicks;  // configurable

    protected JsonTurret(ResourceLocation id, InputStream jsonFile) {
        this.id = id;

        try( InputStreamReader isr = new InputStreamReader(jsonFile) ) {
            JsonObject jobj = JsonUtils.GSON.fromJson(isr, JsonObject.class);

            this.tier = JsonUtils.getIntVal(jobj.get("tier"));
            this.health = JsonUtils.getFloatVal(jobj.get("health"));
            this.ammoCapacity = JsonUtils.getIntVal(jobj.get("ammoCapacity"));
            this.reloadTicks = JsonUtils.getIntVal(jobj.get("reloadTime"));
            this.model = new ResourceLocation(JsonUtils.getStringVal(jobj.get("model")));
            this.glowTexture = new ResourceLocation(JsonUtils.getStringVal(jobj.get("glowTexture")));
            this.shootSoundId = new ResourceLocation(JsonUtils.getStringVal(jobj.get("shootSound")));
            this.attackType = jobj.has("attackType")
                              ? AttackType.valueOf(JsonUtils.getStringVal(jobj.get("attackType")).toUpperCase(Locale.ROOT))
                              : AttackType.GROUND;
            this.isBuoy = jobj.has("isBuoyant") && JsonUtils.getBoolVal(jobj.get("isBuoyant"));
            this.customRenderClass = JsonUtils.getStringVal(jobj.get("customRenderClass"), null);
            this.customModelClass = JsonUtils.getStringVal(jobj.get("customModelClass"), null);

            this.buildTexture(JsonUtils.getStringVal(jobj.get("baseTexture")), jobj.getAsJsonObject("variantHolder"));
            this.buildRange(jobj.get("range"));
        } catch( IOException | NullPointerException ex ) {
            throw new IllegalArgumentException(String.format("Invalid file for ID %s", id), ex);
        } catch( JsonParseException | ClassCastException ex ) {
            throw new IllegalArgumentException(String.format("Invalid JSON for ID %s", id), ex);
        }
    }

    protected void buildRange(JsonElement e) {
        double[] rangeVert = JsonUtils.getDoubleArray(e, Range.is(3));
        this.range = new AxisAlignedBB(-rangeVert[0], -rangeVert[2], -rangeVert[0], rangeVert[0], rangeVert[1], rangeVert[0]);
    }

    private void buildTexture(String texture, JsonObject vh) {
        if( vh != null ) {
            this.variantContainer = VariantContainer.buildInstance(new ResourceLocation(JsonUtils.getStringVal(vh.get("container"))), texture);
            if( this.variantContainer == null ) {
                throw new JsonParseException("variantHolder.container must me a valid container ID");
            }

            JsonArray jarr = vh.getAsJsonArray("variants");
            for( JsonElement jsonElement : jarr ) {
                this.variantContainer.register(jsonElement.getAsJsonObject());
            }
        } else {
            this.stdTexture = new ResourceLocation(texture);
            this.variantContainer = null;
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public ResourceLocation getModelLocation() {
        return this.model;
    }

    @Override
    public String getCustomRenderClass() {
        return this.customRenderClass;
    }

    @Override
    public String getCustomModelClass() {
        return this.customModelClass;
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return this.hasVariants() ? turretInst.getVariant().getTexture() : this.stdTexture;
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return this.glowTexture;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return this.shootSound != null ? this.shootSound : (this.shootSound = ForgeRegistries.SOUND_EVENTS.getValue(this.shootSoundId));
    }

    @Override
    public AxisAlignedBB getRangeBB(@Nullable ITurretInst turretInst) {
        return this.range;
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public int getAmmoCapacity() {
        return this.ammoCapacity;
    }

    @Override
    public int getReloadTicks() {
        return this.reloadTicks;
    }

    @Override
    public AttackType getAttackType() {
        return this.attackType;
    }

    @Override
    public boolean hasVariants() {
        return this.variantContainer != null;
    }

    @Override
    public boolean isBuoy() {
        return this.isBuoy;
    }

    @Override
    public IVariant getVariant(Object id) {
        return this.hasVariants() ? this.variantContainer.get(id) : null;
    }

    @Override
    public void registerVariant(IVariant variant) {
        if( this.hasVariants() ) {
            this.variantContainer.register(variant);
        }
    }

    @Override
    public boolean isDefaultVariant(IVariant variant) {
        return this.hasVariants() && this.variantContainer.isDefault(variant);
    }

    @Override
    public IVariant getVariant(String s) {
        return this.hasVariants() ? this.variantContainer.get(s) : null;
    }
}
