/* CrownPlugins - ${PROJECT_NAME} */
/* ${DATE} - ${TIME} */

package de.obey.crown.noobf;

import de.obey.crown.arena.AreaResetCommand;
import de.obey.crown.core.data.plugin.Log;
import de.obey.crown.core.data.plugin.Messanger;
import de.obey.crown.arena.AreaHandler;
import de.obey.crown.core.data.plugin.sound.Sounds;
import de.obey.crown.listener.CoreStart;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrownAreaReset extends JavaPlugin {

    public static CrownAreaReset getInstance() {
        return getPlugin(CrownAreaReset.class);
    }

    public static Log log = new Log();

    private PluginConfig pluginConfig;
    private Messanger messanger;
    private Sounds sounds;

    private AreaHandler areaHandler;

    @Override
    public void onLoad() {
        log.setPlugin(this);
        pluginConfig = new PluginConfig(this);
        messanger = pluginConfig.getMessanger();
        sounds = pluginConfig.getSounds();
    }

    @Override
    public void onEnable() {
        areaHandler = new AreaHandler(pluginConfig, messanger, sounds);
        areaHandler.run();

        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(pluginConfig, areaHandler).register();
        }

        getServer().getPluginManager().registerEvents(new CoreStart(), this);

        initializeMetrics();
    }

    public void load() {
        areaHandler.loadAreas();

        loadCommands();
        loadListener();
    }

    public void loadCommands() {
        final AreaResetCommand command = new AreaResetCommand(pluginConfig, messanger, areaHandler);

        getCommand("crownareareset").setExecutor(command);
        getCommand("crownareareset").setTabCompleter(command);
    }

    private void initializeMetrics() {
        new Metrics(this, 25050);
    }

    public void loadListener() {}

    @Override
    public void onDisable() {
    }
}
