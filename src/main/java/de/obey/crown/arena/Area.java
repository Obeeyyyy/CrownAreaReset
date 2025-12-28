/* CrownPlugins - CrownAreaReset */
/* 04.03.2025 - 16:08 */

package de.obey.crown.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;

@Getter @Setter
public final class Area {

    private String areaName, displayName;
    private long lastReset, resetTime;
    private Location center;
    private List<String> schematicNames;

    private final Random random = new Random();

    public String getRandomSchematicName() {
        if(schematicNames.isEmpty()) {
            return areaName;
        }

        return schematicNames.get(random.nextInt(schematicNames.size()));
    }

}
