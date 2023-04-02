/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public final class TurretModCommand
{
    public static final SuggestionProvider<CommandSource> PLAYER_LIST = SuggestionProviders.register(new ResourceLocation(TmrConstants.ID, "player_list"),
                                                                                                     (c, sb) -> ISuggestionProvider.suggest(PlayerList.getPlayerMap().entrySet().stream().<ArrayList<String>>collect(ArrayList::new, (a, e) -> {
                                                                                                                    a.add(e.getKey().toString());
                                                                                                                    a.add(e.getValue().name.getString());
                                                                                                                }, ArrayList::addAll), sb));

    private TurretModCommand() { }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("turretmod").then(
                LiteralArgumentBuilder.<CommandSource>literal("playerList").then(
                        LiteralArgumentBuilder.<CommandSource>literal("clear")
                                              .executes(c -> onPData(c, d -> { d.clear(); return true; }, "fail_player_list_clear"))
                ).then(
                        LiteralArgumentBuilder.<CommandSource>literal("cleanse")
                                              .executes(c -> onPData(c, d -> { d.cleanse(c.getSource().getLevel()); return true; }, "fail_player_list_cleanse"))
                ).then(
                        LiteralArgumentBuilder.<CommandSource>literal("remove").then(
                                RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.greedyString())
                                                       .suggests(PLAYER_LIST)
                                                       .executes(c -> onPData(c, d -> d.removePlayer(c.getArgument("player", String.class)), "fail_player_list_remove"))
                                ).executes(TurretModCommand::errorArgs)
                ).executes(TurretModCommand::errorArgs)
        ).executes(TurretModCommand::errorArgs));
    }

    private static int errorArgs(CommandContext<CommandSource> c) {
        c.getSource().sendFailure(new TranslationTextComponent("fail_error_args"));
        return 0;
    }

    private static int onPData(CommandContext<CommandSource> c, Predicate<PlayerList> dataConsumer, String failLocalKey) {
        PlayerList d = PlayerList.getData();
        if( d != null && dataConsumer.test(d) ) {
            return 1;
        }

        c.getSource().sendFailure(new TranslationTextComponent(String.format("command.%s.%s", TmrConstants.ID, failLocalKey)));
        return 0;
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
