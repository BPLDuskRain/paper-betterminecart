package com.duskrainfall.betterminecart.vehicle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VehicleTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String nickname, @NotNull String[] args) {
        List<String> complecation = new ArrayList<>();
        if(args.length == 1){
            complecation.add("back");
            complecation.add("stop");
        }

        return complecation;
    }
}
