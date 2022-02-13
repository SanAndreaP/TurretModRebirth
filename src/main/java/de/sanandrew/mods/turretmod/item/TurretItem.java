/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.api.turret.IVariantHolder;
import de.sanandrew.mods.turretmod.entity.turret.TurretEntity;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.entity.turret.variant.VariantContainer;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.init.TmrConfig;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.IFluidBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class TurretItem
        extends Item
{
    public final ResourceLocation turretId;
    private      ITurret          turretCache;

    public TurretItem(ResourceLocation turretId) {
        super(new Properties().tab(TmrItemGroups.TURRETS));
        this.turretId = turretId;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable World level, @Nonnull List<ITextComponent> lines, @Nonnull ITooltipFlag flag) {
        TurretStats stats = new TurretStats(itemStack.getTag());

        if( stats.variant != null && this.getTurret() instanceof IVariantHolder ) {
            IVariant variant = ((IVariantHolder) this.turretCache).getVariant(stats.variant);
            lines.add(new TranslationTextComponent(Lang.ITEM_TURRET_PLACER.get("variant"), variant.getTranslatedName()));
        }

        if( stats.name != null ) {
            lines.add(new TranslationTextComponent(Lang.ITEM_TURRET_PLACER.get("customname"), stats.name));
        }

        if( stats.health != null ) {
            lines.add(new TranslationTextComponent(Lang.ITEM_TURRET_PLACER.get("health"), stats.health));
        }
    }

    public ITurret getTurret() {
        return MiscUtils.get(this.turretCache, () -> this.turretCache = TurretRegistry.INSTANCE.get(this.turretId));
    }

    @Nonnull
    @Override
    public ActionResultType useOn(@Nonnull ItemUseContext context) {
        World level = context.getLevel();
        if( !level.isClientSide ) {
            BlockPos placingOn = context.getClickedPos().relative(context.getClickedFace());

            ItemStack itemStack = context.getItemInHand();
            if( !this.getTurret().isBuoy() && TurretEntity.canTurretBePlaced(this.turretCache, level, placingOn, false) ) {
                PlayerEntity player = context.getPlayer();
                TurretEntity bob    = spawnTurret(level, this.turretCache, placingOn, player);
                new TurretStats(itemStack.getTag()).apply(bob);

                if( player == null || player.isCreative() ) {
                    itemStack.shrink(1);
                }

                return ActionResultType.SUCCESS;
            }
        } else {
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World level, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if( !level.isClientSide ) {
            BlockRayTraceResult traceResult = getPlayerPOVHitResult(level, player, RayTraceContext.FluidMode.SOURCE_ONLY);
            if( traceResult.getType() == RayTraceResult.Type.BLOCK ) {
                BlockPos blockPos = traceResult.getBlockPos();
                if( !level.mayInteract(player, blockPos) ) {
                    return new ActionResult<>(ActionResultType.FAIL, stack);
                }

                if( !player.mayUseItemAt(blockPos, traceResult.getDirection(), stack) ) {
                    return new ActionResult<>(ActionResultType.FAIL, stack);
                }

                if( this.trySpawnTurret(level, player, blockPos, stack.getTag()) ) {
                    stack.shrink(1);
                }
            }
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    private boolean trySpawnTurret(@Nonnull World level, @Nonnull PlayerEntity player, @Nonnull BlockPos blockPos, CompoundNBT itemNbt) {
        BlockPos lowerPos = blockPos.below();
        if( this.getTurret().isBuoy() && isBlockLiquid(level, blockPos) && isBlockLiquid(level, lowerPos) && TurretEntity
                .canTurretBePlaced(this.turretCache, level, lowerPos, false) ) {
            TurretEntity susan = spawnTurret(level, this.turretCache, lowerPos, player);
            new TurretStats(itemNbt).apply(susan);

            return !player.isCreative();
        }

        return false;
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);

        if( this.allowdedIn(group) && Boolean.TRUE.equals(TmrConfig.TURRETS.showVariantsInItemGroup.get()) ) {
            ITurret t = this.getTurret();
            if( t instanceof IVariantHolder ) {
                IVariantHolder vh = (IVariantHolder) t;
                if( vh.hasVariants() ) {
                    Arrays.stream(vh.getVariants()).forEach(v -> {
                        if( !vh.isDefaultVariant(v) ) {
                            ItemStack turretStack = new ItemStack(this);
                            new TurretStats(null, null, v).updateData(turretStack);
                            items.add(turretStack);
                        }
                    });
                }
            }
        }
    }

    private static boolean isBlockLiquid(World level, BlockPos pos) {
        Block b = level.getBlockState(pos).getBlock();
        return b instanceof FlowingFluidBlock || b instanceof IFluidBlock;
    }

    @Nonnull
    private static TurretEntity spawnTurret(World level, ITurret turret, BlockPos pos, PlayerEntity owner) {
        return spawnTurret(level, turret, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, owner);
    }

    @Nonnull
    private static TurretEntity spawnTurret(World level, ITurret turret, double x, double y, double z, PlayerEntity owner) {
        TurretEntity turretE = new TurretEntity(level, owner, turret, new Vector3d(x, y, z));
        level.addFreshEntity(turretE);
        turretE.playAmbientSound();

        return turretE;
    }

    public static final class TurretStats
    {
        public static final String NBT_TURRET_STATS = "TurretStats";
        public static final String NBT_TURRET_STATS_HEALTH = "Health";
        public static final String NBT_TURRET_STATS_NAME = "Name";
        public static final String NBT_TURRET_STATS_VARIANT = "Variant";

        public final Float            health;
        public final ITextComponent   name;
        public final String variant;

        public TurretStats(ITurretEntity turretInst) {
            LivingEntity bob    = turretInst.get();
            ITurret      turret = turretInst.getDelegate();
            this.health = bob.getHealth();
            this.name = bob.hasCustomName() ? bob.getCustomName() : null;

            if( turret instanceof VariantContainer ) {
                IVariant v = turretInst.getVariant();
                this.variant = v != null ? v.getId() : null;
            } else {
                this.variant = null;
            }
        }

        public TurretStats(Float health, ITextComponent name, IVariant variant) {
            this.health = health;
            this.name = name;
            this.variant = variant.getId().toString();
        }

        public TurretStats(CompoundNBT nbt) {
            if( nbt != null && nbt.contains(NBT_TURRET_STATS, Constants.NBT.TAG_COMPOUND) ) {
                nbt = nbt.getCompound(NBT_TURRET_STATS);

                this.health = getNbt(nbt, NBT_TURRET_STATS_HEALTH, Constants.NBT.TAG_ANY_NUMERIC, CompoundNBT::getFloat);
                this.name = getNbt(nbt, NBT_TURRET_STATS_NAME, Constants.NBT.TAG_STRING, (c, s) -> ITextComponent.Serializer.fromJson(c.getString(s)));
                this.variant = getNbt(nbt, NBT_TURRET_STATS_VARIANT, Constants.NBT.TAG_STRING, CompoundNBT::getString);
            } else {
                this.health = null;
                this.name = null;
                this.variant = null;
            }
        }

        private static <T> T getNbt(CompoundNBT nbt, String nbtName, int nbtType, BiFunction<CompoundNBT, String, T> func) {
            if( nbt.contains(nbtName, nbtType) ) {
                return func.apply(nbt, nbtName);
            }

            return null;
        }

        public void apply(ITurretEntity turretInst) {
            LivingEntity bob = turretInst.get();
            if( this.health != null ) {
                bob.setHealth(this.health);
            }
            if( this.name != null ) {
                bob.setCustomName(this.name);
            }
            if( this.variant != null ) {
                turretInst.setVariant(this.variant);
            }
        }

        public void updateData(ItemStack stack) {
            this.serialize(stack.getOrCreateTagElement(NBT_TURRET_STATS));
        }

        public void serialize(CompoundNBT nbt) {
            if( this.health != null ) {
                nbt.putFloat(NBT_TURRET_STATS_HEALTH, this.health);
            }
            if( this.name != null ) {
                nbt.putString(NBT_TURRET_STATS_NAME, ITextComponent.Serializer.toJson(this.name));
            }
            if( this.variant != null ) {
                nbt.putString(NBT_TURRET_STATS_VARIANT, this.variant);
            }
        }
    }
}
