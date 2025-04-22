/* CrownPlugins - CrownAreaReset */
/* 05.03.2025 - 00:19 */

package de.obey.crown.arena;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.obey.crown.core.data.plugin.Messanger;
import de.obey.crown.core.data.plugin.sound.Sounds;
import de.obey.crown.core.handler.LocationHandler;
import de.obey.crown.core.util.FileUtil;
import de.obey.crown.core.util.TextUtil;
import de.obey.crown.noobf.CrownAreaReset;
import de.obey.crown.noobf.PluginConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public final class AreaHandler {

    private final PluginConfig pluginConfig;
    private final Messanger messanger;
    private final Sounds sounds;

    @Getter
    private BukkitTask runnable;

    @Getter
    private final Map<String, Area> areas = Maps.newConcurrentMap();

    public void run() {
        runnable = Bukkit.getScheduler().runTaskTimerAsynchronously(CrownAreaReset.getInstance(), () -> {

            if(areas.isEmpty())
                return;

            for (final Area area : areas.values()) {
                if(pluginConfig.isBroadcasetRegen()) {
                    final long remainingTime = area.getResetTime() - (System.currentTimeMillis() - area.getLastReset());
                    if (remainingTime <= 5000 && remainingTime >= 1000) {
                        broadcadstMessageToRegions(area, "area-regen-timer", new String[]{"area", "time"}, area.getDisplayName(), TextUtil.formatTimeString(remainingTime));
                    }
                }

                if(System.currentTimeMillis() - area.getLastReset() >= area.getResetTime()) {
                    resetArea(area);
                }
            }

        }, 20, 20);
    }

    public boolean exist(final String areaName) {
        return areas.containsKey(areaName);
    }

    public void deleteArea(final String areaName) {
        areas.remove(areaName);
        saveAreas();
    }

    public void createArea(final Player player, final String areaName) {
        final Area area = new Area();

        area.setAreaName(areaName);
        area.setSchematicName(areaName);
        area.setDisplayName("&f&l" + areaName);
        area.setResetTime(1000 * 60 * 20);
        area.setCenter(player.getLocation());
        area.setLastReset(System.currentTimeMillis());

        LocationHandler.setLocation(areaName, player.getLocation());

        areas.put(areaName, area);
        saveAreas();
    }

    public void resetArea(final Area area) {
        if(area.getCenter() == null)
            return;

        area.setLastReset(System.currentTimeMillis());

        Bukkit.getScheduler().runTask(CrownAreaReset.getInstance(), () -> {
            if(pluginConfig.isEnablePlayerPushbackOnRegen()) {
                for (final Entity entity : area.getCenter().getWorld().getEntities()) {
                    if (!(entity instanceof Player player))
                        continue;

                    if (isPlayerInRegion(player, area.getAreaName())) {
                        final double boost = area.getCenter().getY() > entity.getLocation().getY() ? (area.getCenter().getY() - entity.getLocation().getY()) + 10 : 0;
                        player.setVelocity(new Vector(0, (20 + boost), 0));

                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 4, 10));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 4, 10));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 10, 1));

                        sounds.playSoundToPlayer(player, "pushback-1");
                        sounds.playSoundToPlayer(player, "pushback-2");
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(CrownAreaReset.getInstance(), () -> {
                pasteSchematic(area);
            }, 10);
        });
    }

    private void pasteSchematic(final Area area) {
        final String name = area.getAreaName();
        final Location location = area.getCenter();

        if(location == null) {
            Bukkit.getLogger().warning("[CrownAreaReset] Could not find '" + name + " location.'");
            return;
        }

        final long started = System.currentTimeMillis();
        final File schematicFile = new File("./plugins/FastAsyncWorldEdit/schematics/" + name + ".schem");

        if (!schematicFile.exists()) {
            Bukkit.getLogger().warning("[CrownAreaReset] Could not find '" + name + ".schematic'");
            return;
        }

        try (FileInputStream fis = new FileInputStream(schematicFile)) {
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                Bukkit.getLogger().warning("[CrownAreaReset] Invalid format '" + name + ".schematic'");
                return;
            }

            final Clipboard clipboard = format.getReader(fis).read();

            final BlockVector3 pasteLocation = BlockVector3.at(
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            );

            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(location.getWorld());

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld)) {
                final ClipboardHolder holder = new ClipboardHolder(clipboard);

                Operations.complete(holder
                        .createPaste(editSession)
                        .to(pasteLocation)
                        .ignoreAirBlocks(false)
                        .build()
                );
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }

            if(pluginConfig.isBroadcasetRegen()) {
                broadcadstMessageToRegions(area, "area-regenerated", new String[]{"area", "time"}, area.getDisplayName(), TextUtil.formatNumber((System.currentTimeMillis() - started)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcadstMessageToRegions(final Area area, final String messageKey, final String[] keys, final String... values) {
        Bukkit.getScheduler().runTask(CrownAreaReset.getInstance(), () -> {
            for (final Entity entity : area.getCenter().getWorld().getEntities()) {
                if (!(entity instanceof Player player))
                    continue;

                if (isPlayerInRegion(player, area.getAreaName())) {
                    messanger.sendMessage(player, messageKey, keys, values);
                    sounds.playSoundToPlayer(player, "area-reset");
                }
            }
        });
    }

    private boolean isPlayerInRegion(final Player player, final String regionName) {
        final Location loc = player.getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(loc));

        for (ProtectedRegion region : regions) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }

    public void loadAreas() {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(pluginConfig.getConfigFile());

        areas.clear();

        if(!configuration.contains("area"))
            return;

        final Set<String> areaNames = configuration.getConfigurationSection("area").getKeys(false);
        if(!areaNames.isEmpty()) {

            for (final String areaName : areaNames) {
                Area area = new Area();

                area.setAreaName(areaName);
                area.setDisplayName(FileUtil.getString(configuration, "area." + areaName + ".displayName", "&f&l" + areaName));
                area.setSchematicName(FileUtil.getString(configuration, "area." + areaName + ".schematicName", areaName));
                area.setLastReset(FileUtil.getLong(configuration, "area." + areaName + ".lastReset", System.currentTimeMillis()));
                area.setResetTime(FileUtil.getLong(configuration, "area." + areaName + ".resetTime", 50000));

                final Location center = LocationHandler.getLocation(areaName);
                if (center == null) {
                    Bukkit.getLogger().info("No Location for " + areaName + " found. Use /locations set " + areaName);
                } else {
                    area.setCenter(LocationHandler.getLocation(areaName));
                }

                areas.put(areaName, area);
            }
        }
    }

    public void saveAreas() {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(pluginConfig.getConfigFile());

        configuration.set("area",null);

        if(areas.isEmpty())
            return;

        for (final Area area : areas.values()) {
            configuration.set("area." + area.getAreaName() + ".displayName", area.getDisplayName());
            configuration.set("area." + area.getAreaName() + ".schematicName", area.getSchematicName());
            configuration.set("area." + area.getAreaName() + ".resetTime", area.getResetTime());
            configuration.set("area." + area.getAreaName() + ".lastReset", area.getLastReset());
            configuration.set("area." + area.getAreaName() + ".lastReset", area.getLastReset());
        }

        FileUtil.saveConfigurationIntoFile(configuration, pluginConfig.getConfigFile());
    }
}
