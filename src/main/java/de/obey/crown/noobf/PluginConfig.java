/* CrownPlugins - CrownAreaReset */
/* 04.03.2025 - 16:05 */

package de.obey.crown.noobf;

import com.google.common.collect.Maps;
import de.obey.crown.core.data.plugin.CrownConfig;
import de.obey.crown.core.handler.LocationHandler;
import de.obey.crown.core.util.FileUtil;
import de.obey.crown.object.Area;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Set;

@Getter @Setter
public final class PluginConfig extends CrownConfig {

    private final Map<String, Area> areas = Maps.newConcurrentMap();

    private boolean broadcasetRegen;
    private int broadcastRadius;

    public PluginConfig(@NonNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(getConfigFile());

        setBroadcasetRegen(FileUtil.getBoolean(configuration, "area-regen-broadcast", true));
        setBroadcastRadius(FileUtil.getInt(configuration, "area-regen-broadcast-radius", 250));

        final Set<String> areaNames = configuration.getConfigurationSection("area").getKeys(false);
        if(!areaNames.isEmpty()) {

            for (final String areaName : areaNames) {
                Area area = new Area();

                area.setAreaName(areaName);
                area.setDisplayName(FileUtil.getString(configuration, "area." + areaName + ".displayName", "&f&l" + areaName));
                area.setSchematicName(FileUtil.getString(configuration, "area." + areaName + ".schematicName", areaName));
                area.setLastReset(FileUtil.getLong(configuration, "area." + areaName + ".resetTime", 50000));

                final Location center = LocationHandler.getLocation(areaName);
                if (center == null) {
                    Bukkit.getLogger().info("No Location for " + areaName + " found. Use /locations set " + areaName);
                } else {
                    area.setCenter(LocationHandler.getLocation(areaName));
                }

                areas.put(areaName, area);
            }
        }

        saveConfig();
    }

    @Override
    public void saveConfig() {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(getConfigFile());

        if(areas.isEmpty())
            return;

        for (final Area area : areas.values()) {
            configuration.set("area." + area.getAreaName() + ".displayName", area.getDisplayName());
            configuration.set("area." + area.getAreaName() + ".schematicName", area.getDisplayName());
            configuration.set("area." + area.getAreaName() + ".resetTime", area.getResetTime());
            configuration.set("area." + area.getAreaName() + ".lastReset", area.getLastReset());
        }

        FileUtil.saveConfigurationIntoFile(configuration, getConfigFile());
    }
}
