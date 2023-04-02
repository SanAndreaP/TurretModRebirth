/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.init;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import dev.sanandrea.mods.turretmod.api.client.tcu.ILabelRegistry;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuClientRegistry;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuScreen;
import dev.sanandrea.mods.turretmod.api.tcu.TcuContainer;
import dev.sanandrea.mods.turretmod.client.gui.element.tcu.TcuInfoValue;
import dev.sanandrea.mods.turretmod.client.gui.tcu.TcuInfoPage;
import dev.sanandrea.mods.turretmod.client.gui.tcu.TcuLevelsPage;
import dev.sanandrea.mods.turretmod.client.gui.tcu.TcuRemoteAccessPage;
import dev.sanandrea.mods.turretmod.client.gui.tcu.TcuScreen;
import dev.sanandrea.mods.turretmod.client.gui.tcu.TcuSmartTargetingPage;
import dev.sanandrea.mods.turretmod.client.gui.tcu.TcuTargetPage;
import dev.sanandrea.mods.turretmod.client.gui.tcu.TcuUpgradesPage;
import dev.sanandrea.mods.turretmod.client.gui.tcu.info.AmmoProvider;
import dev.sanandrea.mods.turretmod.client.gui.tcu.info.HealthProvider;
import dev.sanandrea.mods.turretmod.client.gui.tcu.info.NameProvider;
import dev.sanandrea.mods.turretmod.client.gui.tcu.info.OwnerProvider;
import dev.sanandrea.mods.turretmod.client.gui.tcu.info.PersonalShieldProvider;
import dev.sanandrea.mods.turretmod.client.gui.tcu.info.TargetProvider;
import dev.sanandrea.mods.turretmod.client.renderer.turret.LabelRegistry;
import dev.sanandrea.mods.turretmod.item.TurretControlUnit;
import dev.sanandrea.mods.turretmod.item.upgrades.UpgradeRegistry;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public final class TcuClientRegistry
        implements ITcuClientRegistry
{
    public static final TcuClientRegistry               INSTANCE                   = new TcuClientRegistry();
    private static final Map<ITcuInfoProvider, Integer> TCU_INFO_PROVIDERS_ORDINAL = new HashMap<>();
    private static final List<ITcuInfoProvider>         TCU_INFO_PROVIDERS         = new ArrayList<>();

    private TcuClientRegistry() { }

    @Override
    public ILabelRegistry getLabelRegistry() {
        return LabelRegistry.INSTANCE;
    }

    @Override
    public void registerTcuScreen(@Nonnull ResourceLocation id, Supplier<ItemStack> iconSupplier,
                                  Function<ContainerScreen<TcuContainer>, ITcuScreen> screenProvider)
    {
        TcuScreen.registerScreen(id, iconSupplier, screenProvider);
    }

    @Override
    public void registerTcuInfoProvider(int priority, ITcuInfoProvider provider) {
        TCU_INFO_PROVIDERS.add(provider);
        TCU_INFO_PROVIDERS_ORDINAL.put(provider, priority);
    }

    public static void registerTcuClient(ITcuClientRegistry registry) {
        registry.registerTcuScreen(TurretControlUnit.INFO, new SimpleItem(Items.BOOK), TcuInfoPage::new);
        registry.registerTcuScreen(TurretControlUnit.TARGETS_CREATURES, new SimpleItem(Items.ZOMBIE_HEAD), TcuTargetPage.Creatures::new);
        registry.registerTcuScreen(TurretControlUnit.TARGETS_PLAYERS, PlayerHeads::getRandomSkull, TcuTargetPage.Players::new);
        registry.registerTcuScreen(TurretControlUnit.UPGRADES, () -> UpgradeRegistry.INSTANCE.getItem(UpgradeRegistry.INSTANCE.getEmptyUpgrade().getId()), TcuUpgradesPage::new);
        registry.registerTcuScreen(TurretControlUnit.TARGETS_SMART, () -> UpgradeRegistry.INSTANCE.getItem(Upgrades.SMART_TGT.getId()), TcuSmartTargetingPage::new);
        registry.registerTcuScreen(TurretControlUnit.LEVELS, () -> UpgradeRegistry.INSTANCE.getItem(Upgrades.LEVELING.getId()), TcuLevelsPage::new);
        registry.registerTcuScreen(TurretControlUnit.REMOTE_ACCESS, () -> UpgradeRegistry.INSTANCE.getItem(Upgrades.REMOTE_ACCESS.getId()), TcuRemoteAccessPage::new);

        registry.registerTcuInfoProvider(0, new NameProvider());
        registry.registerTcuInfoProvider(1, new HealthProvider());
        registry.registerTcuInfoProvider(2, new AmmoProvider());
        registry.registerTcuInfoProvider(3, new PersonalShieldProvider());
        registry.registerTcuInfoProvider(4, new TargetProvider());
        registry.registerTcuInfoProvider(5, new OwnerProvider());
    }

    public static List<ITcuInfoProvider> getProviders() {
        return TCU_INFO_PROVIDERS.stream().sorted(TcuClientRegistry::sortProviders).collect(Collectors.toList());
    }

    public static int sortProviders(GuiElementInst e1, GuiElementInst e2) {
        return sortProviders(e1.get(TcuInfoValue.class).provider, e2.get(TcuInfoValue.class).provider);
    }

    private static int sortProviders(ITcuInfoProvider e1, ITcuInfoProvider e2) {
        return Integer.compare(TCU_INFO_PROVIDERS_ORDINAL.get(e1), TCU_INFO_PROVIDERS_ORDINAL.get(e2));
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
