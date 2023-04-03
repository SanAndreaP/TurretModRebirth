/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui.tcu;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiContainer;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.turretmod.api.Resources;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuScreen;
import dev.sanandrea.mods.turretmod.api.tcu.TcuContainer;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TcuScreen
        extends JsonGuiContainer<TcuContainer>
{
    private static final ITcuScreen EMPTY_SCREEN = new ITcuScreen() {
        @Override public void renderBackground(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partTicks) { /* no-op */ }
        @Override public void renderForeground(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partTicks) { /* no-op */ }
    };

    private static final Map<ResourceLocation, Function<ContainerScreen<TcuContainer>, ITcuScreen>> PAGES      = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<ItemStack>>          PAGE_ICONS = new HashMap<>();

    @Nonnull
    private final ITcuScreen       currScreen;
    private final ResourceLocation currPage;

    public final boolean isRemote;

    public TcuScreen(TcuContainer tcuContainer, PlayerInventory playerInv, ITextComponent title) {
        super(tcuContainer, playerInv, title);

        this.currPage = tcuContainer.currPage;
        this.isRemote = tcuContainer.isRemote;
        this.currScreen = MiscUtils.apply(PAGES.get(tcuContainer.currPage),
                                                 p -> this.guiDefinition != null
                                                      ? MiscUtils.get(p.apply(this), EMPTY_SCREEN)
                                                      : EMPTY_SCREEN,
                                                 EMPTY_SCREEN);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_TCU_BASE);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        super.initGd();

        this.currScreen.init(this.minecraft, this.leftPos, this.topPos);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(mStack, partialTicks, mouseX, mouseY);

        mStack.pushPose();
        mStack.translate(this.leftPos, this.topPos, 0.0);
        this.currScreen.renderBackground(mStack, mouseX, mouseY, partialTicks);
        mStack.popPose();
    }

    @Override
    protected void renderGd(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.currScreen.renderForeground(mStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();

        this.currScreen.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.currScreen.mouseClicked(mouseX, mouseY, button)
               || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return this.currScreen.mouseScrolled(mouseX, mouseY, scroll)
               || super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.currScreen.mouseReleased(mouseX, mouseY, button)
               || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.currScreen.mouseDragged(mouseX, mouseY, button, dragX, dragY)
               || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.currScreen.keyPressed(keyCode, scanCode, modifiers)
               || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.currScreen.keyReleased(keyCode, scanCode, modifiers)
               || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return this.currScreen.charTyped(typedChar, keyCode)
               || super.charTyped(typedChar, keyCode);
    }

    @Override
    public void onClose() {
        this.currScreen.onClose();

        super.onClose();
    }

    public ResourceLocation getCurrPage() {
        return this.currPage;
    }

    public ITurretEntity getTurret() {
        return this.menu.turret;
    }

    public static void registerScreen(ResourceLocation id, Supplier<ItemStack> icon, Function<ContainerScreen<TcuContainer>, ITcuScreen> screenProvider) {
        PAGES.computeIfAbsent(id, k -> {
            PAGE_ICONS.put(k, icon);

            return screenProvider;
        });
    }

    public static ItemStack getIcon(ResourceLocation id) {
        return MiscUtils.apply(PAGE_ICONS.get(id), Supplier::get, ItemStack.EMPTY);
    }
}
