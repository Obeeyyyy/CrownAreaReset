/* CrownPlugins - CrownAreaReset */
/* 04.03.2025 - 16:05 */

package de.obey.crown.noobf;

import de.obey.crown.core.data.plugin.CrownConfig;
import de.obey.crown.core.util.FileUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public final class PluginConfig extends CrownConfig {

    private boolean broadcastRegen, enablePlayerPushbackOnRegen, enableLocationTeleportOnRegen, enableYTeleportOnRegen;
    private int broadcastRadius, teleportYDelay, pushbackDelay;
    private List<String> pushbackEffects;
    private String timeFormat, teleportToLocation;

    public PluginConfig(@NonNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(getConfigFile());

        setBroadcastRegen(FileUtil.getBoolean(configuration, "area-regen-broadcast", true));
        setEnablePlayerPushbackOnRegen(FileUtil.getBoolean(configuration, "player-pushback.enabled", true));
        setEnableYTeleportOnRegen(FileUtil.getBoolean(configuration, "player-y-teleport.enabled", true));
        setTeleportYDelay(FileUtil.getInt(configuration, "player-y-teleport.delay", 0));
        setPushbackDelay(FileUtil.getInt(configuration, "player-pushback.delay", 0));
        setPushbackEffects(FileUtil.getStringArrayList(configuration, "player-pushback.effects", new ArrayList<>()));
        setTimeFormat(FileUtil.getString(configuration, "time-format", "%mm%:%ss%"));

        setEnableLocationTeleportOnRegen(FileUtil.getBoolean(configuration, "player-location-teleport.enabled", false));
        setTeleportToLocation(FileUtil.getString(configuration, "player-location-teleport.location", "spawn"));

        saveConfig();
    }

    @Override
    public void saveConfig() {}
}
