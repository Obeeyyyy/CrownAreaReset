/* CrownPlugins - CrownAreaReset */
/* 05.03.2025 - 03:06 */

package de.obey.crown.arena;

import de.obey.crown.core.data.plugin.Messanger;
import de.obey.crown.core.handler.LocationHandler;
import de.obey.crown.noobf.CrownAreaReset;
import de.obey.crown.noobf.PluginConfig;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public final class AreaResetCommand implements CommandExecutor, TabCompleter {

    private final PluginConfig pluginConfig;
    private final Messanger messanger;
    private final AreaHandler areaHandler;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {

                if(!messanger.hasPermission(sender, "command.reload"))
                    return false;

                pluginConfig.loadConfig();
                pluginConfig.loadMessages();
                areaHandler.loadAreas();

                messanger.sendMessage(sender, "plugin-reloaded", new String[]{"plugin"}, CrownAreaReset.getInstance().getName());

                return false;
            }
        }

        if(!messanger.hasPermission(sender, "car.admin"))
            return false;

        if(!(sender instanceof Player player))
            return false;

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("create")) {
                final String areaName = args[1];

                if(areaHandler.exist(areaName)) {
                    messanger.sendMessage(sender, "area-exists-already", new String[]{"area"}, areaName);
                    return false;
                }

                areaHandler.createArea(player, areaName);
                messanger.sendMessage(player, "area-created", new String[]{"area"}, areaName);

                return false;
            }

            if (args[0].equalsIgnoreCase("reset")) {
                final String areaName = args[1];

                if(!areaHandler.exist(areaName)) {
                    messanger.sendMessage(sender, "area-does-not-exist", new String[]{"area"}, areaName);
                    return false;
                }

                areaHandler.resetArea(areaHandler.getAreas().get(areaName));
                messanger.sendMessage(sender, "area-force-reset", new String[]{"area"}, areaName);

                return false;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                final String areaName = args[1];

                if(!areaHandler.exist(areaName)) {
                    messanger.sendMessage(sender, "area-does-not-exist", new String[]{"area"}, areaName);
                    return false;
                }

                areaHandler.deleteArea(areaName);
                areaHandler.saveAreas();
                messanger.sendMessage(sender, "area-deleted", new String[]{"area"}, areaName);

                return false;
            }

            if(args[0].equalsIgnoreCase("setlocation")) {
                final String areaName = args[1];

                if(!areaHandler.exist(areaName)) {
                    messanger.sendMessage(sender, "area-does-not-exist", new String[]{"area"}, areaName);
                    return false;
                }

                final Area area = areaHandler.getAreas().get(areaName);

                area.setCenter(player.getLocation());
                LocationHandler.setLocation(areaName, player.getLocation());
                areaHandler.saveAreas();

                messanger.sendMessage(sender, "area-edit", new String[]{"area", "value"}, areaName, "location");
                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("setdisplayname")) {
                final String areaName = args[1];

                if(!areaHandler.exist(areaName)) {
                    messanger.sendMessage(sender, "area-does-not-exist", new String[]{"area"}, areaName);
                    return false;
                }

                final Area area = areaHandler.getAreas().get(areaName);

                area.setDisplayName(args[2]);
                areaHandler.saveAreas();

                messanger.sendMessage(sender, "area-edit", new String[]{"area", "value"}, areaName, "displayname");
                return false;
            }

            if(args[0].equalsIgnoreCase("setresettime")) {
                final String areaName = args[1];

                if(!areaHandler.exist(areaName)) {
                    messanger.sendMessage(sender, "area-does-not-exist", new String[]{"area"}, areaName);
                    return false;
                }

                final long millis = messanger.isValidInt(sender, args[2], 100);

                if(millis < 100)
                    return false;

                final Area area = areaHandler.getAreas().get(areaName);

                area.setResetTime(millis);
                areaHandler.saveAreas();

                messanger.sendMessage(sender, "area-edit", new String[]{"area", "value"}, areaName, "resetTime");
                return false;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        final ArrayList<String> list = new ArrayList<>();

        if(!(sender instanceof Player player))
            return list;

        if(!sender.hasPermission("car.admin"))
            return list;

        if(args.length == 1) {
            if (sender.hasPermission("command.reload"))
                list.add("reload");

            list.add("create");
            list.add("delete");
            list.add("setlocation");
            list.add("setdisplayname");
            list.add("setresettime");
            list.add("reset");
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("setlocation") ||
                    args[0].equalsIgnoreCase("setdisplayname")||
                    args[0].equalsIgnoreCase("setresettime") ||
                    args[0].equalsIgnoreCase("delete") ||
                    args[0].equalsIgnoreCase("reset"))
            {
                list.addAll(areaHandler.getAreas().keySet());

            } else if(args[0].equalsIgnoreCase("create")) {
                list.add("§aname here");
            }
        }

        final String argument = args[args.length - 1];
        if (!argument.isEmpty())
            list.removeIf(value -> !value.toLowerCase().startsWith(argument.toLowerCase()));

        Collections.sort(list);

        return list;
    }
}
