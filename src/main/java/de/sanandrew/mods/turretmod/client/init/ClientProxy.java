/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.init;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.audio.SoundLaser;
import de.sanandrew.mods.turretmod.client.compat.patchouli.PageCustomCrafting;
import de.sanandrew.mods.turretmod.client.compat.patchouli.PatchouliMouseEventHandler;
import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.client.event.RenderEventHandler;
import de.sanandrew.mods.turretmod.client.event.RenderForcefieldHandler;
import de.sanandrew.mods.turretmod.client.gui.GuiCartridge;
import de.sanandrew.mods.turretmod.client.gui.GuiElectrolyteGenerator;
import de.sanandrew.mods.turretmod.client.gui.GuiTurretCrate;
import de.sanandrew.mods.turretmod.client.gui.GuiTurretInfo;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiAssemblyFilter;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.client.gui.element.ElectrolyteBar;
import de.sanandrew.mods.turretmod.client.gui.element.assembly.AssemblyGhostItems;
import de.sanandrew.mods.turretmod.client.gui.element.assembly.AssemblyGroupIcon;
import de.sanandrew.mods.turretmod.client.gui.element.assembly.AssemblyProgressBar;
import de.sanandrew.mods.turretmod.client.gui.element.assembly.AssemblyRecipeArea;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.RemoteAccessHealth;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TurretCam;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuTurretName;
import de.sanandrew.mods.turretmod.client.gui.element.AmmoItem;
import de.sanandrew.mods.turretmod.client.gui.element.AmmoItemTooltip;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.info.ErrorTooltip;
import de.sanandrew.mods.turretmod.client.gui.element.InfoElement;
import de.sanandrew.mods.turretmod.client.gui.element.PlayerIcon;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.level.LevelIndicator;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.level.LevelModifiers;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.nav.PageNavigation;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.nav.PageNavigationTooltip;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor.CheckBox;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor.ColorPicker;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor.ShieldRender;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.target.TargetList;
import de.sanandrew.mods.turretmod.client.gui.element.tinfo.InfoStrokeText;
import de.sanandrew.mods.turretmod.client.gui.element.tinfo.InfoBgTexture;
import de.sanandrew.mods.turretmod.client.gui.element.tinfo.InfoUpgradeItems;
import de.sanandrew.mods.turretmod.client.gui.element.tinfo.InfoUpgradeTooltips;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.PlayerHeads;
import de.sanandrew.mods.turretmod.client.model.item.ColorCartridge;
import de.sanandrew.mods.turretmod.client.model.item.ColorTippedBolt;
import de.sanandrew.mods.turretmod.client.effect.EffectHandler;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderProjectile;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.client.render.world.RenderTurretPointed;
import de.sanandrew.mods.turretmod.client.shader.Shaders;
import de.sanandrew.mods.turretmod.client.world.ClientWorldEventListener;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.init.CommonProxy;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemTurretControlUnit;
import de.sanandrew.mods.turretmod.registry.EnumEffect;
import de.sanandrew.mods.turretmod.registry.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretLaser;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;

public class ClientProxy
        extends CommonProxy
{
    public static ILexiconInst lexiconInstance;

    static {
        GuiDefinition.TYPES.put(ElectrolyteBar.ID, ElectrolyteBar::new);
        GuiDefinition.TYPES.put(AssemblyRecipeArea.ID, AssemblyRecipeArea::new);
        GuiDefinition.TYPES.put(AssemblyGroupIcon.ID, AssemblyGroupIcon::new);
        GuiDefinition.TYPES.put(AssemblyProgressBar.ID, AssemblyProgressBar::new);
        GuiDefinition.TYPES.put(AssemblyGhostItems.ID, AssemblyGhostItems::new);

        GuiDefinition.TYPES.put(PageNavigation.ID, PageNavigation::new);
        GuiDefinition.TYPES.put(PageNavigationTooltip.ID, PageNavigationTooltip::new);
        GuiDefinition.TYPES.put(TcuTurretName.ID, TcuTurretName::new);
        GuiDefinition.TYPES.put(TurretCam.ID, TurretCam::new);
        GuiDefinition.TYPES.put(PlayerIcon.ID, PlayerIcon::new);
        GuiDefinition.TYPES.put(AmmoItem.ID, AmmoItem::new);
        GuiDefinition.TYPES.put(InfoElement.ID, InfoElement::new);
        GuiDefinition.TYPES.put(AmmoItemTooltip.ID, AmmoItemTooltip::new);
        GuiDefinition.TYPES.put(TargetList.ID, TargetList::new);
        GuiDefinition.TYPES.put(ErrorTooltip.ID, ErrorTooltip::new);
        GuiDefinition.TYPES.put(ShieldRender.ID, ShieldRender::new);
        GuiDefinition.TYPES.put(LevelIndicator.ID, LevelIndicator::new);
        GuiDefinition.TYPES.put(LevelModifiers.ID, LevelModifiers::new);
        GuiDefinition.TYPES.put(RemoteAccessHealth.ID, RemoteAccessHealth::new);

        GuiDefinition.TYPES.put(ColorPicker.ID, ColorPicker::new);
        GuiDefinition.TYPES.put(CheckBox.ID, CheckBox::new);

        GuiDefinition.TYPES.put(InfoBgTexture.ID, InfoBgTexture::new);
        GuiDefinition.TYPES.put(InfoStrokeText.ID, InfoStrokeText::new);
        GuiDefinition.TYPES.put(InfoStrokeText.InfoTurretName.ID, InfoStrokeText.InfoTurretName::new);
        GuiDefinition.TYPES.put(InfoUpgradeItems.ID, InfoUpgradeItems::new);
        GuiDefinition.TYPES.put(InfoUpgradeTooltips.ID, InfoUpgradeTooltips::new);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientWorldEventListener());
        PatchouliMouseEventHandler.register();
        PageCustomCrafting.registerPage();

        RenderingRegistry.registerEntityRenderingHandler(EntityTurret.class, RenderTurret::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretProjectile.class, RenderProjectile::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerTcuGuis(GuiTcuRegistry.INSTANCE));
        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerTcuLabelElements(RenderTurretPointed.INSTANCE));

        InfoBgTexture.initialize();

        MinecraftForge.EVENT_BUS.register(RenderForcefieldHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

        IReloadableResourceManager mcResourceMgr = (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();
        mcResourceMgr.registerReloadListener(RenderForcefieldHandler.INSTANCE);

        Shaders.initShaders();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        PlayerHeads.preLoadPlayerHeadsAsync();

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ColorTippedBolt(), ItemRegistry.TURRET_AMMO.get(Ammunitions.TIPPED_BOLT.getId()));
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ColorCartridge(), ItemRegistry.AMMO_CARTRIDGE);
    }

    @Override
    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        if( player == null ) {
            player = Minecraft.getMinecraft().player;
        }

        super.openGui(player, id, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if( id >= 0 && id < EnumGui.VALUES.length ) {
            TileEntity te;
            switch( EnumGui.VALUES[id] ) {
                case TCU: {
                        Entity e = world.getEntityByID(x);
                        if( e instanceof ITurretInst ) {
                            return GuiTcuRegistry.INSTANCE.openGUI(y, player, (ITurretInst) e, z == 1);
                        }
                    }
                    break;
                case TASSEMBLY:
                    te = world.getTileEntity(new BlockPos(x, y, z));
                    if( te instanceof TileEntityTurretAssembly ) {
                        return new GuiTurretAssembly(player.inventory, (TileEntityTurretAssembly) te);
                    }
                    break;
                case TASSEMBLY_FLT:
                    ItemStack stack = TmrUtils.getHeldItemOfType(player, ItemRegistry.ASSEMBLY_UPG_FILTER);
                    if( ItemStackUtils.isValid(stack) && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER ) {
                        return new GuiAssemblyFilter(player.inventory, stack);
                    }
                    break;
                case ELECTROLYTE_GENERATOR:
                    te = world.getTileEntity(new BlockPos(x, y, z));
                    if( te instanceof TileEntityElectrolyteGenerator ) {
                        return new GuiElectrolyteGenerator(player.inventory, (TileEntityElectrolyteGenerator) te);
                    }
                    break;
                case LEXICON:
                    return lexiconInstance.getGui();
                case CARTRIDGE:
                    ItemStack heldStack = TmrUtils.getHeldItemOfType(player, ItemRegistry.AMMO_CARTRIDGE);
                    if( ItemStackUtils.isValid(heldStack) ) {
                        IInventory inv = ItemAmmoCartridge.getInventory(heldStack);
                        if( inv != null ) {
                            return new GuiCartridge(player.inventory, inv, player);
                        }
                    }
                    break;
                case TCRATE:
                    te = world.getTileEntity(new BlockPos(x, y, z));
                    if( te instanceof TileEntityTurretCrate ) {
                        return new GuiTurretCrate(player.inventory, (TileEntityTurretCrate) te);
                    }
                    break;
                case TINFO: {
                        Entity e = world.getEntityByID(x);
                        if( e instanceof ITurretInst ) {
                            return new GuiTurretInfo((ITurretInst) e);
                        }
                    }
                    break;

            }
        } else {
            TmrConstants.LOG.log(Level.WARN, String.format("Gui ID %d cannot be opened as it isn't a valid index in EnumGui!", id));
        }

        return null;
    }

    @Override
    public void addEffect(EnumEffect effect, double x, double y, double z, Tuple data) {
        EffectHandler.handle(effect, x, y, z, data);
    }

    @Override
    public void playTurretLaser(ITurretInst turretInst) {
        TurretLaser.MyRAM ram = turretInst.getRAM(TurretLaser.MyRAM::new);

        if( ram.laserSound == null ) {
            if( turretInst.getTargetProcessor().isShooting() && turretInst.getTargetProcessor().hasAmmo() ) {
                ram.laserSound = new SoundLaser(turretInst);
                Minecraft.getMinecraft().getSoundHandler().playSound(ram.laserSound);
            }
        } else if( ram.laserSound.isDonePlaying() || !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(ram.laserSound) ) {
            ram.laserSound = null;
        }
    }

    @Override
    public void addForcefield(Entity e, IForcefieldProvider provider) {
        RenderForcefieldHandler.INSTANCE.addForcefieldRenderer(e, provider);
    }

    @Override
    public boolean hasForcefield(Entity e, Class<? extends IForcefieldProvider> providerCls) {
        return RenderForcefieldHandler.INSTANCE.hasForcefield(e, providerCls);
    }

    public static float[] forceGlow() {
        float[] prevBright = new float[] {OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY};
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xF0, 0x0);
        return prevBright;
    }

    public static void resetGlow(float[] prevBright) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBright[0], prevBright[1]);
    }

    public static void addQuad(BufferBuilder buf, double minX, double minY, double maxX, double maxY, ColorObj clr) {
        addQuad(buf, minX, minY, maxX, maxY, clr, clr);
    }

    public static void addQuad(BufferBuilder buf, double minX, double minY, double maxX, double maxY, ColorObj clr1, ColorObj clr2) {
        buf.pos(minX, minY, 0.0D).color(clr1.fRed(), clr1.fGreen(), clr1.fBlue(), clr1.fAlpha()).endVertex();
        buf.pos(minX, maxY, 0.0D).color(clr2.fRed(), clr2.fGreen(), clr2.fBlue(), clr2.fAlpha()).endVertex();
        buf.pos(maxX, maxY, 0.0D).color(clr2.fRed(), clr2.fGreen(), clr2.fBlue(), clr2.fAlpha()).endVertex();
        buf.pos(maxX, minY, 0.0D).color(clr1.fRed(), clr1.fGreen(), clr1.fBlue(), clr1.fAlpha()).endVertex();
    }

    @Override
    public boolean isPlayerPressingShift() {
        return ClientTickHandler.isSneaking;
    }

    @Override
    public boolean checkTurretGlowing(ITurretInst turret) {
        Minecraft mc = Minecraft.getMinecraft();
        if( mc.pointedEntity != turret.get() ) {
            return ItemTurretControlUnit.isHeldTcuBoundToTurret(Minecraft.getMinecraft().player, turret);
        }

        return false;
    }
}
