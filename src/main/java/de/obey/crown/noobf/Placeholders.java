/* CrownPlugins - CrownAreaReset */
/* 21.04.2025 - 12:08 */

package de.obey.crown.noobf;

import de.obey.crown.arena.Area;
import de.obey.crown.arena.AreaHandler;
import de.obey.crown.core.util.TextUtil;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public final class Placeholders extends PlaceholderExpansion {

    private final AreaHandler areaHandler;

    @Override
    public @NotNull String getIdentifier() {
        return "car";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Obey";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String onPlaceholderRequest(final Player player, @NotNull String params) {
        final String[] args = params.split("_");

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("nextreset")) {
                final String areaName = args[1];
                if(!areaHandler.exist(areaName))
                    return "invalid area";

                final Area area = areaHandler.getAreas().get(areaName);

                if(area == null)
                    return "invalid area";

                return TextUtil.formatTimeString(area.getResetTime() - (System.currentTimeMillis() - area.getLastReset()));
            }
        }

        return "invalid placeholder";
    }
}
