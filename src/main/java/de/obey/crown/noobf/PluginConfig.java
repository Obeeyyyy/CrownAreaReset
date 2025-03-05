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

@Getter @Setter
public final class PluginConfig extends CrownConfig {

    private boolean broadcasetRegen;
    private int broadcastRadius;

    public PluginConfig(@NonNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(getConfigFile());

        setBroadcasetRegen(FileUtil.getBoolean(configuration, "area-regen-broadcast", true));

        saveConfig();
    }

    @Override
    public void saveConfig() {

    }
}
