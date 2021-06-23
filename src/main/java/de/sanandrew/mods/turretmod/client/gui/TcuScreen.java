package de.sanandrew.mods.turretmod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
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
import java.util.function.Supplier;

public class TcuScreen
        extends ContainerScreen<TcuContainer>
        implements IGui
{
    private static final Map<ResourceLocation, ScreenProvider> PAGES      = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<ItemStack>>             PAGE_ICONS = new HashMap<>();

    private float currPartTicks;
    private final GuiDefinition    guiDef;
    private final Screen           currScreen;
    private final ResourceLocation currPage;

    private final ITurretEntity    turret;

    public TcuScreen(TcuContainer tcuContainer, PlayerInventory playerInv, ITextComponent title) {
        super(tcuContainer, playerInv, title);

        this.turret = tcuContainer.turret;
        this.guiDef = this.initGuiDef();
        this.currPage = tcuContainer.currPage;
        this.currScreen = MiscUtils.applyNonNull(PAGES.get(tcuContainer.currPage),
                                                 p -> this.guiDef != null ? p.get(tcuContainer.turret, this.guiDef.width, this.guiDef.height) : null,
                                                 null);
    }

    private GuiDefinition initGuiDef() {
        try {
            GuiDefinition guiDef = GuiDefinition.getNewDefinition(Resources.GUI_TCU_BASE);

            this.imageWidth = guiDef.width;
            this.imageHeight = guiDef.height;

            return guiDef;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    public void init(@Nonnull Minecraft mc, int screenWidth, int screenHeight) {
        super.init(mc, screenWidth, screenHeight);
        if( this.currScreen != null ) {
            this.currScreen.init(mc, screenWidth, screenHeight);
        }
    }

    @Override
    protected void init() {
        super.init();

        ClientProxy.initGuiDef(this.guiDef, this);

        this.tick();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
//        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if( this.currScreen != null ) {
            this.currScreen.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();

        this.guiDef.update(this);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.currPartTicks = partialTicks;
        ClientProxy.drawGDBackground(this.guiDef, matrixStack, this, partialTicks, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int x, int y) {
        this.guiDef.drawForeground(this, matrixStack, x, y, this.currPartTicks);
    }

    @Override
    public Screen get() {
        return this;
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    public int getScreenPosX() {
        return this.leftPos;
    }

    @Override
    public int getScreenPosY() {
        return this.topPos;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.guiDef.mouseClicked(this, mouseX, mouseY, button)
               || MiscUtils.applyNonNull(this.currScreen, s -> s.mouseClicked(mouseX, mouseY, button), false)
               || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return this.guiDef.mouseScrolled(this, mouseX, mouseY, scroll)
               || MiscUtils.applyNonNull(this.currScreen, s -> s.mouseScrolled(mouseX, mouseY, scroll), false)
               || super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.guiDef.mouseReleased(this, mouseX, mouseY, button)
               || MiscUtils.applyNonNull(this.currScreen, s -> s.mouseReleased(mouseX, mouseY, button), false)
               || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.guiDef.mouseDragged(this, mouseX, mouseY, button, dragX, dragY)
               || MiscUtils.applyNonNull(this.currScreen, s -> s.mouseDragged(mouseX, mouseY, button, dragX, dragY), false)
               || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.guiDef.keyPressed(this, keyCode, scanCode, modifiers)
               || MiscUtils.applyNonNull(this.currScreen, s -> s.keyPressed(keyCode, scanCode, modifiers), false)
               || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.guiDef.keyReleased(this, keyCode, scanCode, modifiers)
               || MiscUtils.applyNonNull(this.currScreen, s -> s.keyReleased(keyCode, scanCode, modifiers), false)
               || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return this.guiDef.charTyped(this, typedChar, keyCode)
               || MiscUtils.applyNonNull(this.currScreen, s -> s.charTyped(typedChar, keyCode), false)
               || super.charTyped(typedChar, keyCode);
    }

    @Override
    public void onClose() {
        this.guiDef.onClose(this);
        MiscUtils.applyNonNull(this.currScreen, s -> {s.onClose(); return null;}, null);
        super.onClose();
    }

    public ResourceLocation getCurrPage() {
        return this.currPage;
    }

    public ITurretEntity getTurret() {
        return this.turret;
    }

    public static void registerScreen(ResourceLocation id, Supplier<ItemStack> icon, ScreenProvider screenProvider) {
        if( !PAGES.containsKey(id) ) {
            PAGES.put(id, screenProvider);
            PAGE_ICONS.put(id, icon);
        }
    }

    public static ItemStack getIcon(ResourceLocation id) {
        return MiscUtils.applyNonNull(PAGE_ICONS.get(id), Supplier::get, ItemStack.EMPTY);
    }

    @FunctionalInterface
    public interface ScreenProvider
    {
        Screen get(ITurretEntity turret, int guiWidth, int guiHeight);
    }
}
