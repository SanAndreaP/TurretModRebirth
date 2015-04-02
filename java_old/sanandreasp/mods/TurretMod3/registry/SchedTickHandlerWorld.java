package sanandreasp.mods.TurretMod3.registry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;

public class SchedTickHandlerWorld {

	private int tick = 0;
	public SchedTickHandlerWorld() { }

	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent event) {
        if(event.phase == TickEvent.Phase.START && event.side.isServer()) {
            tick++;
            if (tick > 4) {
                for (Object obj : event.world.playerEntities) {
                    TM3ModRegistry.proxy.initTM3PlayerTag((EntityPlayer) obj);
                }
                tick = 0;
            }
        }
	}
}
