package MoonEU.kowal;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.Set;

public final class BezElek extends JavaPlugin implements Listener {

    private List<String> restrictedRegions;
    private String removalMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        restrictedRegions = config.getStringList("restricted-regions");
        removalMessage = config.getString("removal-message", "Elytra usunieta cwelu");
    }

    @EventHandler
    public void onPlayerWearElytra(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            if (isInRestrictedRegion(player.getLocation())) {
                player.sendMessage(removalMessage);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA && player.isGliding()) {
            if (isInRestrictedRegion(player.getLocation())) {
                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                player.getInventory().addItem(chestplate);
                player.sendMessage(ChatUtil.fixColor(removalMessage));
            }
        }
    }

    public boolean isInRestrictedRegion(final Location loc) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        com.sk89q.worldedit.util.Location weLoc = BukkitAdapter.adapt(loc);
        Set<ProtectedRegion> regions = query.getApplicableRegions(weLoc).getRegions();
        for (ProtectedRegion region : regions) {
            if (restrictedRegions.contains(region.getId().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}