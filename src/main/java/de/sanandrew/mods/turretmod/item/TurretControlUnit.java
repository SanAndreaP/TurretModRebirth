/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.ITcuRegistry;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.entity.EntityRegistry;
import de.sanandrew.mods.turretmod.entity.turret.TurretEntity;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.inventory.container.TcuContainerFactory;
import de.sanandrew.mods.turretmod.inventory.container.TcuRemoteAccessContainer;
import de.sanandrew.mods.turretmod.inventory.container.TcuUpgradesContainer;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TurretControlUnit
        extends Item
        implements ITcuRegistry
{
    private static final AxisAlignedBB          GLOW_SRC_BB         = new AxisAlignedBB(-32, -32, -32, 32, 32, 32);
    private static final String                 NBT_SAPTURRETMOD    = TmrConstants.ID;
    private static final String                 NBT_BOUND_TURRET_ID = "Id";
    private static final int                    EE_NAME_COUNT       = 5;
    private static final List<ResourceLocation> PAGES               = new ArrayList<>();

    private long prevDisplayNameTime = 0;
    private int  nameId              = 0;

    TurretControlUnit() {
        super(new Properties().tab(TmrItemGroups.MISC));
    }

    @Nonnull
    @Override
    public ITextComponent getName(@Nonnull ItemStack stack) {
        long currDisplayNameTime = System.currentTimeMillis();
        if( this.prevDisplayNameTime + 1000 < currDisplayNameTime ) {
            if( MiscUtils.RNG.randomInt(1000) != 0 ) {
                this.nameId = 0;
            } else {
                this.nameId = MiscUtils.RNG.randomInt(EE_NAME_COUNT - 1) + 1;
            }
        }

        this.prevDisplayNameTime = currDisplayNameTime;

        if( this.nameId < 1 ) {
            return super.getName(stack);
        } else {
            return new TranslationTextComponent(String.format("%s.%d", this.getDescriptionId(), this.nameId));
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        if( getBoundID(stack) != null ) {
            tooltip.add(new TranslationTextComponent(String.format("%s.bound", this.getDescriptionId())).withStyle(TextFormatting.GRAY));
        }
    }

    private boolean accessDenied(ITurretEntity turret, PlayerEntity player) {
        if( !turret.hasPlayerPermission(player) ) {
            ITextComponent errorMsg = new TranslationTextComponent(this.getDescriptionId() + ".access_denied").withStyle(TextFormatting.RED);
            player.displayClientMessage(errorMsg, true);
            return true;
        }

        return false;
    }

    @Nonnull
    @Override
    @SuppressWarnings("java:S3776")
    public ActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if( ItemStackUtils.isItem(heldStack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            ITurretEntity turret = getBoundTurret(heldStack, world, player);
            if( turret != null ) {
                if( this.accessDenied(turret, player) ) {
                    if( !world.isClientSide ) {
                        bindTurret(heldStack, null);
                    }

                    return ActionResult.fail(heldStack);
                }

                if( !world.isClientSide ) {
                    if( player.isCrouching() ) {
                        bindTurret(heldStack, null);
                    } else if( player instanceof ServerPlayerEntity ) {
                        openTcu((ServerPlayerEntity) player, heldStack, turret, PAGES.get(0), true);
                    }
                }

                return ActionResult.success(heldStack);
            }
        }

        return super.use(world, player, hand);
    }

    @Nonnull
    @Override
    public ActionResultType interactLivingEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nonnull LivingEntity entity, @Nonnull Hand hand) {
        if( entity instanceof ITurretEntity ) {
            ITurretEntity turret = (ITurretEntity) entity;
            if( this.accessDenied(turret, player) ) {
                return ActionResultType.FAIL;
            }

            if( !player.level.isClientSide ) {
                if( player.isCrouching() ) {
                    bindTurret(stack, turret);
                } else if( player instanceof ServerPlayerEntity ) {
                    openTcu((ServerPlayerEntity) player, stack, turret, PAGES.get(0), true);
                }
            }

            return ActionResultType.SUCCESS;
        }

        return super.interactLivingEntity(stack, player, entity, hand);
    }

    public static int getPageOrder(ResourceLocation pageId) {
        return PAGES.indexOf(pageId);
    }

    public static void forEachPage(Consumer<ResourceLocation> forEachFunc) {
        PAGES.forEach(forEachFunc);
    }

    private static UUID getBoundID(ItemStack stack) {
        CompoundNBT boundTurret = stack.getTagElement(NBT_SAPTURRETMOD);
        if( boundTurret != null && boundTurret.contains("Id") ) {
            return boundTurret.getUUID("Id");
        }

        return null;
    }

    public static boolean isTcuHeld(PlayerEntity player, ITurretEntity turret) {
        return ItemStackUtils.isValid(getHeldTcu(player)) && turret != null && turret.hasPlayerPermission(player);
    }

    public static ItemStack getHeldTcu(PlayerEntity player) {
        ItemStack s = player.getMainHandItem();
        if( ItemStackUtils.isItem(s, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            return s;
        }

        s = player.getOffhandItem();
        if( ItemStackUtils.isItem(s, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            return s;
        }

        return ItemStack.EMPTY;
    }

    public static boolean isHeldTcuBoundToTurret(PlayerEntity player, ITurretEntity turretInst) {
        if( player == null ) {
            return false;
        }

        return TurretControlUnit.getBoundTurret(getHeldTcu(player), player.level, player) == turretInst;
    }

    public static ITurretEntity getBoundTurret(ItemStack stack, World world, @Nonnull PlayerEntity player) {
        if( !ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            return null;
        }

        UUID id = getBoundID(stack);
        if( id != null ) {
            Entity entity;
            if( world instanceof ServerWorld ) {
                entity = EntityUtils.getServerEntity(world, id);
            } else {
                entity = world.getEntitiesOfClass(TurretEntity.class, GLOW_SRC_BB.move(player.position()),e -> e.getUUID().equals(id))
                              .stream().findFirst().orElse(null);
            }

            if( entity instanceof TurretEntity ) {
                return (TurretEntity) entity;
            }
        }

        return null;
    }

    public static void bindTurret(ItemStack stack, ITurretEntity turretInst) {
        if( !ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            return;
        }

        CompoundNBT tag = stack.getTagElement(NBT_SAPTURRETMOD);
        if( turretInst != null ) {
            if( tag == null ) {
                tag = stack.getOrCreateTagElement(NBT_SAPTURRETMOD);
            }
            tag.putUUID(NBT_BOUND_TURRET_ID, turretInst.get().getUUID());
        } else {
            stack.removeTagKey(NBT_SAPTURRETMOD);

            CompoundNBT cmp = stack.getTag();
            if( cmp != null && cmp.isEmpty() ) {
                stack.setTag(null);
            }
        }
    }

    public static void openTcu(@Nullable ServerPlayerEntity player, ItemStack stack, ITurretEntity turret, ResourceLocation type, boolean initial) {
        if( player == null || player.level.isClientSide ) {
            TurretModRebirth.PROXY.openTcuGuiRemote(stack, turret, type, initial);
        } else {
            boolean isRemote = isHeldTcuBoundToTurret(player, turret);
            NetworkHooks.openGui(player, new TcuContainerFactory.Provider(stack, turret, type, isRemote, initial), buf -> {
                buf.writeVarInt(turret.get().getId());
                buf.writeResourceLocation(type);
                buf.writeBoolean(isRemote);
                buf.writeBoolean(initial);
            });
        }
    }

    @Override
    public void registerTcuPage(@Nonnull ResourceLocation id) {
        this.registerTcuPage(id, null);
    }

    @Override
    public void registerTcuPage(@Nonnull ResourceLocation id, @Nullable TcuContainer.TcuContainerProvider containerProvider) {
        if( !PAGES.contains(id) ) {
            PAGES.add(id);
            if( containerProvider != null ) {
                TcuContainerFactory.TCU_CONTAINERS.put(id, containerProvider);
            }
        } else {
            TmrConstants.LOG.log(Level.WARN, "TCU Page with id <{}> has already been registered.", id);
        }
    }

    public static final ResourceLocation INFO              = new ResourceLocation(TmrConstants.ID, "info");
    public static final ResourceLocation TARGETS_CREATURES = new ResourceLocation(TmrConstants.ID, "targets_creatures");
    public static final ResourceLocation TARGETS_PLAYERS   = new ResourceLocation(TmrConstants.ID, "targets_players");
    public static final ResourceLocation TARGETS_SMART     = new ResourceLocation(TmrConstants.ID, "targets_smart");
    public static final ResourceLocation UPGRADES          = new ResourceLocation(TmrConstants.ID, "upgrades");
    public static final ResourceLocation LEVELS            = new ResourceLocation(TmrConstants.ID, "leveling");
    public static final ResourceLocation REMOTE_ACCESS     = new ResourceLocation(TmrConstants.ID, "remote_access");

    public static void register(ITcuRegistry registry) {
        registry.registerTcuPage(INFO);
        registry.registerTcuPage(TARGETS_CREATURES);
        registry.registerTcuPage(TARGETS_PLAYERS);
        registry.registerTcuPage(TARGETS_SMART);
        registry.registerTcuPage(UPGRADES, TcuUpgradesContainer::new);
        registry.registerTcuPage(LEVELS);
        registry.registerTcuPage(REMOTE_ACCESS, TcuRemoteAccessContainer::new);
    }
}
