package de.sanandrew.mods.turretmod.client.compat.patchouli;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class MouseEventHandler
{
    private static List<WeakReference<ComponentTurretEntries>> currHovered = new ArrayList<>();

    public static void register() {
        if( Loader.isModLoaded("patchouli") ) {
            MinecraftForge.EVENT_BUS.register(new MouseEventHandler());
        }
    }

    static void setCurrHoveredComponent(ComponentTurretEntries component, boolean active) {
        if( active ) {
            currHovered.add(new WeakReference<>(component));
        } else {
            currHovered.removeIf(r -> {
                if( r != null ) {
                    ComponentTurretEntries e = r.get();
                    return e == null || e.equals(component);
                }

                return true;
            });
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        GuiScreen gui = event.getGui();
        if( gui instanceof GuiBook ) {
            GuiBook guiBook = (GuiBook) gui;
            int pageBook = guiBook.getPage() + 1;
            for( Iterator<WeakReference<ComponentTurretEntries>> it = currHovered.iterator(); it.hasNext(); ) {
                WeakReference<ComponentTurretEntries> r = it.next();
                if( r == null ) {
                    it.remove();
                    continue;
                }

                ComponentTurretEntries component = r.get();
                if( component != null ) {
                    if( pageBook != MathHelper.ceil(component.pgNum / 1.99F) ) {
                        it.remove();
                    } else {
                        int scrollDir = Mouse.getEventDWheel();
                        if( scrollDir != 0 ) {
                            event.setCanceled(true);
                            component.moveButtons(scrollDir > 0);
                            break;
                        }
                    }
                } else {
                    it.remove();
                }
            }
        }
    }
}
