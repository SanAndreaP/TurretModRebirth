/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.tcu.ITcuRegistry;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuInfoPage;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuScreen;
import de.sanandrew.mods.turretmod.entity.turret.TurretEntity;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.inventory.container.TcuContainerFactory;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class TurretControlUnit
        extends Item
        implements ITcuRegistry
{
    private static final String NBT_BOUND_TURRET = TmrConstants.ID + ".bound_turret";
    private static final String NBT_BOUND_TURRET_ID = "Id";
    private static final int EE_NAME_COUNT = 5;

    private long prevDisplayNameTime = 0;
    private int nameId = 0;

    public static final List<ResourceLocation>           PAGES = new ArrayList<>();

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
                this.nameId = MiscUtils.RNG.randomInt(EE_NAME_COUNT - 1) + 1;// MathHelper.ceil(MiscUtils.RNG.randomInt(EE_NAME_COUNT - 1) + 2);
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

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if( ItemStackUtils.isItem(heldStack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            ITurretEntity turret = getBoundTurret(heldStack, world);
            if( turret != null ) {
                if( !world.isClientSide ) {
                    if( player.isCrouching() ) {
                        bindTurret(heldStack, null);
                    } else if( player instanceof ServerPlayerEntity ) {
                        openTcu((ServerPlayerEntity) player, heldStack, turret, PAGES.get(0));
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
            if( !player.level.isClientSide ) {
                if( player.isCrouching() ) {
                    bindTurret(stack, (ITurretEntity) entity);
                } else if( player instanceof ServerPlayerEntity ) {
                    openTcu((ServerPlayerEntity) player, stack, (ITurretEntity) entity, PAGES.get(0));
                }
            }

            return ActionResultType.SUCCESS;
        }

        return super.interactLivingEntity(stack, player, entity, hand);
    }

    private static UUID getBoundID(ItemStack stack) {
        CompoundNBT boundTurret = stack.getTagElement(NBT_BOUND_TURRET);
        if( boundTurret != null && boundTurret.contains("Id") ) {
            return boundTurret.getUUID("Id");
        }

        return null;
    }

    public static boolean isTcuHeld(PlayerEntity player) {
        return ItemStackUtils.isValid(getHeldTcu(player));
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

        return TurretControlUnit.getBoundTurret(getHeldTcu(player), player.level) == turretInst;
    }

    public static ITurretEntity getBoundTurret(ItemStack stack, World world) {
        if( !ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            return null;
        }

        UUID id = getBoundID(stack);
        if( id != null ) {
            Entity entity = EntityUtils.getServerEntity(world, id);
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

        CompoundNBT tag = stack.getTagElement(NBT_BOUND_TURRET);
        if( turretInst != null ) {
            if( tag == null ) {
                tag = stack.getOrCreateTagElement(NBT_BOUND_TURRET);
            }
            tag.putUUID(NBT_BOUND_TURRET_ID, turretInst.get().getUUID());
        } else {
            stack.removeTagKey(NBT_BOUND_TURRET);

            CompoundNBT cmp = stack.getTag();
            if( cmp != null && cmp.isEmpty() ) {
                stack.setTag(null);
            }
        }
    }

    public static void openTcu(@Nullable ServerPlayerEntity player, ItemStack stack, ITurretEntity turret, ResourceLocation type) {
        if( player == null || player.level.isClientSide ) {
            TurretModRebirth.PROXY.openTcuGuiRemote(stack, turret, type);
        } else {
            boolean isRemote = isHeldTcuBoundToTurret(player, turret);
            NetworkHooks.openGui(player, new TcuContainerFactory.Provider(stack, turret, type, isRemote), buf -> {
                buf.writeVarInt(turret.get().getId());
                buf.writeResourceLocation(type);
                buf.writeBoolean(isRemote);
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerTcuScreen(@Nonnull ResourceLocation id, Supplier<ItemStack> iconSupplier,
                                  Function<ContainerScreen<TcuContainer>, ITcuScreen> screenProvider)
    {
        TcuScreen.registerScreen(id, iconSupplier, screenProvider);
    }

    public static final ResourceLocation INFO              = new ResourceLocation(TmrConstants.ID, "info");
    public static final ResourceLocation TARGETS_CREATURES = new ResourceLocation(TmrConstants.ID, "targets_creatures");
    public static final ResourceLocation TARGETS_PLAYERS   = new ResourceLocation(TmrConstants.ID, "targets_players");

    public static void register(ITcuRegistry registry) {
        registry.registerTcuPage(INFO);
        registry.registerTcuPage(TARGETS_CREATURES);
        registry.registerTcuPage(TARGETS_PLAYERS);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerClient(ITcuRegistry registry) {
        registry.registerTcuScreen(INFO, new SimpleItem(Items.BOOK), TcuInfoPage::new);
        registry.registerTcuScreen(TARGETS_CREATURES, new SimpleItem(Items.ZOMBIE_HEAD),
                                   s -> null);
        registry.registerTcuScreen(TARGETS_PLAYERS, de.sanandrew.mods.turretmod.client.init.PlayerHeads::getRandomSkull,
                                   s -> null);
    }

    private static final class SimpleItem
            implements Supplier<ItemStack>
    {
        private final ItemStack stack;

        private SimpleItem(Item item) {
            this.stack = new ItemStack(item);
        }

        @Override
        public ItemStack get() {
            return this.stack;
        }
    }
}
