/* CrownPlugins - CrownAreaReset */
/* 04.03.2025 - 18:59 */

package de.obey.crown.listener;

import de.obey.crown.core.event.CoreStartEvent;
import de.obey.crown.noobf.CrownAreaReset;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class CoreStart implements Listener {

    @EventHandler
    public void on(final CoreStartEvent event) {
        Bukkit.getLogger().info("[^] Thank you for using " + CrownAreaReset.getInstance().getName() + " made by @Obeeyyyy!");
        CrownAreaReset.getInstance().load();
    }
}
