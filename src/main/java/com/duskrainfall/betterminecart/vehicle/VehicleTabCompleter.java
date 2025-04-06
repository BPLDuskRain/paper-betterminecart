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
        List<String> complication = new ArrayList<>();
        if(args.length == 1){
            complication.add("back");
            complication.add("stop");
        }

        return complication;
    }
}
