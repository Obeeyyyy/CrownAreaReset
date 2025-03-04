/* CrownPlugins - ${PROJECT_NAME} */
/* ${DATE} - ${TIME} */

package de.obey.crown.noobf;

import de.obey.crown.core.data.plugin.Messanger;
import de.obey.crown.listener.CoreStart;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrownAreaReset extends JavaPlugin {

    public static CrownAreaReset getInstance() {
        return getPlugin(CrownAreaReset.class);
    }
    
    private PluginConfig pluginConfig;
    private Messanger messanger;
    
    @Override
    public void onEnable() {
       
        pluginConfig = new PluginConfig(this);
        messanger = pluginConfig.getMessanger();

        getServer().getPluginManager().registerEvents(new CoreStart(), this);
    }

    public void load() {
        loadCommands();
        loadListener();
    }

    public void loadCommands() {}
    
    public void loadListener() {}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
