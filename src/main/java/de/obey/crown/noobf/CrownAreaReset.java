/* CrownPlugins - ${PROJECT_NAME} */
/* ${DATE} - ${TIME} */

package de.obey.crown.noobf;

import de.obey.crown.arena.AreaResetCommand;
import de.obey.crown.core.data.plugin.Messanger;
import de.obey.crown.arena.AreaHandler;
import de.obey.crown.core.data.plugin.sound.Sounds;
import de.obey.crown.listener.CoreStart;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CrownAreaReset extends JavaPlugin {

    public static CrownAreaReset getInstance() {
        return getPlugin(CrownAreaReset.class);
    }

    private PluginConfig pluginConfig;
    private Messanger messanger;
    private Sounds sounds;

    private AreaHandler areaHandler;

    @Override
    public void onLoad() {
        pluginConfig = new PluginConfig(this);
        messanger = pluginConfig.getMessanger();
        sounds = pluginConfig.getSounds();
    }

    @Override
    public void onEnable() {
        areaHandler = new AreaHandler(pluginConfig, messanger, sounds);
        areaHandler.run();

        new Placeholders(areaHandler).register();

        getServer().getPluginManager().registerEvents(new CoreStart(), this);
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
    
    public void loadListener() {}

    @Override
    public void onDisable() {
        areaHandler.getRunnable().cancel();
    }
}
