/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.util;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * WARNING: This class is named like that on purpose, because it's da best and so enterprise-y!
 * ( I may rename it, though :P )
 */
public final class ClientSideStateCheatPropertyManagerFactoryKeyResponseInterceptorInterpreter
{
    public static final ClientSideStateCheatPropertyManagerFactoryKeyResponseInterceptorInterpreter INSTANCE
            = new ClientSideStateCheatPropertyManagerFactoryKeyResponseInterceptorInterpreter();

    private final List<CheatInstance> cheats = new ArrayList<>();
    private int currKeyIndex = 0;
    private final List<CheatInstance> currCheats = new ArrayList<>();

    private ClientSideStateCheatPropertyManagerFactoryKeyResponseInterceptorInterpreter() { }

    public static void registerCheat(Runnable task, int... keys) {
        checkKeyChain(INSTANCE.cheats, keys);
        INSTANCE.cheats.add(new CheatInstance(task, keys));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if( event.phase == Phase.END ) {
            this.handleKeyInput();
        }
    }

    private void handleKeyInput() {
        if( Keyboard.isCreated() ) {
            while( Keyboard.next() ) {
                if( Keyboard.getEventKeyState() ) {
                    this.onKeyPress(Keyboard.getEventKey());
                }
            }
        }
    }

    private void onKeyPress(int keyIndex) {
        if( this.currKeyIndex == 0 ) {
            this.currCheats.clear();
            for( CheatInstance cheat : this.cheats ) {
                if( cheat.keyCombi[this.currKeyIndex] == keyIndex ) {
                    this.currCheats.add(cheat);
                }
            }
            this.currKeyIndex++;
        } else {
            Iterator<CheatInstance> cheatsIt = this.currCheats.iterator();
            while( cheatsIt.hasNext() ) {
                CheatInstance cheat = cheatsIt.next();
                if( cheat.keyCombi[this.currKeyIndex] != keyIndex ) {
                    cheatsIt.remove();
                }
            }

            this.currKeyIndex++;

            if( this.currCheats.size() == 1 && this.currCheats.get(0).keyCombi.length == this.currKeyIndex ) {
                this.currCheats.get(0).executeCheat();
            } else if( this.currCheats.size() == 0 ) {
                this.currKeyIndex = 0;
            }
        }
    }

    private static void checkKeyChain(List<CheatInstance> cheats, int[] chain) {
        for( CheatInstance cheat : cheats ) {
            int maxLength = Math.max(cheat.keyCombi.length, chain.length);
            for( int keyInd = 0; keyInd < maxLength; keyInd++ ) {
                if( keyInd == cheat.keyCombi.length || keyInd == chain.length ) {
                    throw new IllegalArgumentException("Cheat code keycain cannot overlap another one!");
                }
                if( cheat.keyCombi[keyInd] != chain[keyInd] ) {
                    break;
                }
            }
        }
    }

    private static final class CheatInstance
    {
        private final int[] keyCombi;
        private final Runnable executionTask;

        private CheatInstance(Runnable task, int... keys) {
            if( task == null ) {
                throw new IllegalArgumentException("Cheat-Task cannot be null!");
            }

            if( keys.length < 2 ) {
                throw new IllegalArgumentException("Cannot register cheat with a keychain with less than 2 keys!");
            }

            this.executionTask = task;
            this.keyCombi = keys;
        }

        private void executeCheat() {
            this.executionTask.run();
        }
    }
}
