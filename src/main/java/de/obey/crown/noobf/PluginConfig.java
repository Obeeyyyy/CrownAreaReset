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

    private boolean broadcastRegen, enablePlayerPushbackOnRegen, enableTeleportOnRegen;
    private int broadcastRadius;
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
        setEnableTeleportOnRegen(FileUtil.getBoolean(configuration, "teleport-on-regen.enabled", false));
        setPushbackEffects(FileUtil.getStringArrayList(configuration, "player-pushback.effects", new ArrayList<>()));
        setTimeFormat(FileUtil.getString(configuration, "time-format", "%mm%:%ss%"));
        setTeleportToLocation(FileUtil.getString(configuration, "teleport-on-regen.location", "spawn"));

        saveConfig();
    }

    @Override
    public void saveConfig() {}
}
