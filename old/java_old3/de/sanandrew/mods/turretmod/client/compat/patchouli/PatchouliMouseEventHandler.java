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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PatchouliMouseEventHandler
{
    private static final List<WeakReference<ComponentEntryList<?>>> CURR_HOVERED = new ArrayList<>();

    public static void register() {
        if( Loader.isModLoaded("patchouli") ) {
            MinecraftForge.EVENT_BUS.register(new PatchouliMouseEventHandler());
        }
    }

    static void setCurrHoveredComponent(ComponentEntryList<?> component, boolean active) {
        if( active ) {
            CURR_HOVERED.add(new WeakReference<>(component));
        } else {
            CURR_HOVERED.removeIf(r -> {
                if( r != null ) {
                    ComponentEntryList<?> e = r.get();
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
            for( Iterator<WeakReference<ComponentEntryList<?>>> it = CURR_HOVERED.iterator(); it.hasNext(); ) {
                WeakReference<ComponentEntryList<?>> r = it.next();
                if( r == null ) {
                    it.remove();
                    continue;
                }

                ComponentEntryList<?> component = r.get();
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
