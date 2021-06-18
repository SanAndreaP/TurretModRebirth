package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.renderer.turret.LabelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = TmrConstants.ID, value = Dist.CLIENT)
public class RenderEventHandler
{
    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft        mc        = Minecraft.getInstance();
        ActiveRenderInfo camera    = mc.gameRenderer.getMainCamera();
        float            partTicks = event.getPartialTicks();

        LabelRegistry.INSTANCE.render(mc, event.getContext(), event.getMatrixStack(), partTicks, camera);
    }
}
