/* CrownPlugins - CrownAreaReset */
/* 04.03.2025 - 16:08 */

package de.obey.crown.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
public final class Area {

    private String areaName, displayName, schematicName;
    private long lastReset, resetTime;
    private Location center;

}
