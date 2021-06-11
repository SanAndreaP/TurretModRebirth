/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.api.turret.IVariantHolder;
import de.sanandrew.mods.turretmod.entity.turret.TurretEntity;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.entity.turret.variant.VariantContainer;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
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
import java.util.List;

@SuppressWarnings("Duplicates")
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
        return this.turretCache != null ? this.turretCache : (this.turretCache = TurretRegistry.INSTANCE.get(this.turretId));
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

                BlockPos lowerPos = blockPos.below();
                if( this.getTurret().isBuoy() && isBlockLiquid(level, blockPos) && isBlockLiquid(level, lowerPos) && TurretEntity
                        .canTurretBePlaced(this.turretCache, level, lowerPos, false) ) {
                    TurretEntity susan = spawnTurret(level, this.turretCache, lowerPos, player);
                    new TurretStats(stack.getTag()).apply(susan);

                    if( !player.isCreative() ) {
                        stack.shrink(1);
                    }
                }
            }
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
//        return super.use(level, player, hand);
    }
//
//    @Override
//    @SuppressWarnings("ConstantConditions")
//    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
//        ItemStack stack = player.getHeldItem(hand);
//
//        if( !world.isRemote ) {
//            RayTraceResult traceResult = this.rayTrace(world, player, true);
//            if( traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK ) {
//                BlockPos blockPos = traceResult.getBlockPos();
//                if( !world.isBlockModifiable(player, blockPos) ) {
//                    return new ActionResult<>(EnumActionResult.FAIL, stack);
//                }
//
//                if( !player.canPlayerEdit(blockPos, traceResult.sideHit, stack) ) {
//                    return new ActionResult<>(EnumActionResult.FAIL, stack);
//                }
//
//                BlockPos lowerPos = blockPos.down();
//                if( this.turret.isBuoy() && isBlockLiquid(world, blockPos) && isBlockLiquid(world, lowerPos) && EntityTurret.canTurretBePlaced(this.turret, world, lowerPos, false) ) {
//                    EntityTurret susan = spawnTurret(world, this.turret, lowerPos, player);
//                    if( susan != null ) {
//                        new TurretStats(stack.getTagCompound()).apply(susan);
//
//                        if( !player.capabilities.isCreativeMode ) {
//                            stack.shrink(1);
//                        }
//                    }
//                }
//            }
//        }
//
//        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
//    }

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
        public final Float            health;
        public final ITextComponent   name;
        public final String variant;

        public TurretStats(ITurretEntity turretInst) {
            LivingEntity bob    = turretInst.get();
            ITurret      turret = turretInst.getDelegate();
            this.health = bob.getHealth();
            this.name = bob.hasCustomName() ? bob.getCustomName() : null;

            if( turret instanceof VariantContainer ) {
                IVariant variant = turretInst.getVariant();
                this.variant = variant != null ? variant.getId() : null;
            } else {
                this.variant = null;
            }
        }

        public TurretStats(Float health, ITextComponent name, IVariant variant) {
            this.health = health;
            this.name = name;
            this.variant = variant.getId();
        }

        public TurretStats(CompoundNBT nbt) {
            if( nbt != null && nbt.contains("TurretStats", Constants.NBT.TAG_COMPOUND) ) {
                nbt = nbt.getCompound("TurretStats");

                this.health = nbt.contains("Health", Constants.NBT.TAG_ANY_NUMERIC) ? nbt.getFloat("Health") : null;
                this.name = nbt.contains("Name", Constants.NBT.TAG_STRING) ? ITextComponent.Serializer.fromJson(nbt.getString("Name")) : null;
                this.variant = nbt.contains("Variant", Constants.NBT.TAG_STRING) ? nbt.getString("Variant") : null;
            } else {
                this.health = null;
                this.name = null;
                this.variant = null;
            }
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

        public CompoundNBT updateData(ItemStack stack) {
            return this.serialize(stack.getOrCreateTagElement("TurretStats"));
        }

        public CompoundNBT serialize(CompoundNBT nbt) {
            if( this.health != null ) {
                nbt.putFloat("Health", this.health);
            }
            if( this.name != null ) {
                nbt.putString("Name", ITextComponent.Serializer.toJson(this.name));
            }
            if( this.variant != null ) {
                nbt.putString("Variant", this.variant);
            }

            return nbt;
        }
    }
}
