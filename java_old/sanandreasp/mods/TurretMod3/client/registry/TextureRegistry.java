package sanandreasp.mods.TurretMod3.client.registry;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;

public class TextureRegistry {
    public static IIcon[] iconCache;

    @SubscribeEvent
    public void stitchTextureMap(TextureStitchEvent.Pre event){
        if(event.map.getTextureType()==1) {
            iconCache = new IIcon[3];
            iconCache[0] = event.map.registerIcon("TurretMod3:ach_piercing");
            iconCache[1] = event.map.registerIcon("TurretMod3:redFlame");
            iconCache[2] = event.map.registerIcon("TurretMod3:blueFlame");
        }
    }
}
