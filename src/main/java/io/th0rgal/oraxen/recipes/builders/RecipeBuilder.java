package io.th0rgal.oraxen.recipes.builders;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.settings.ResourcesManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class RecipeBuilder {

    private static final Map<UUID, RecipeBuilder> MAP = new HashMap<>();

    private Inventory inventory;
    private File configFile;
    private YamlConfiguration config;
    private final String inventoryTitle;
    private final Player player;
    private final String builderName;

    public RecipeBuilder(Player player, String builderName) {
        this.player = player;
        this.builderName = builderName;
        this.inventoryTitle = player.getName() + " " + builderName + " builder§o§r§a§x§e§n"; // watermark
        UUID playerId = player.getUniqueId();
        inventory = MAP.containsKey(playerId)
                ? MAP.get(playerId).inventory
                : createInventory(player, inventoryTitle);
        player.openInventory(inventory);
        MAP.put(playerId, this);
    }

    abstract Inventory createInventory(Player player, String inventoryTitle);

    public abstract void saveRecipe(String name);

    public abstract void saveRecipe(String name, String permission);

    protected Inventory getInventory() {
        return this.inventory;
    }

    protected void setSerializedItem(ConfigurationSection section, ItemStack itemStack) {

        String itemID = OraxenItems.getIdByItem(itemStack);

        //if our itemstack is made using oraxen and is not modified
        if (itemID != null && OraxenItems.getItemById(itemID).build().equals(itemStack)) {
            section.set("oraxen_item", itemID);
            return;
        }

        //if our itemstack is an unmodified vanilla item
        if (itemStack != null && itemStack.equals(new ItemStack(itemStack.getType()))) {
            section.set("minecraft_type", itemStack.getType().toString());
            return;
        }
        section.set("minecraft_item", itemStack);
    }

    public YamlConfiguration getConfig() {
        if (configFile == null) {
            configFile = new ResourcesManager(OraxenPlugin.get()).extractConfiguration("recipes/" + builderName + ".yml");
            config = YamlConfiguration.loadConfiguration(configFile);
        }
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        MAP.put(player.getUniqueId(), this);
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public static RecipeBuilder get(UUID playerUUID) {
        return MAP.get(playerUUID);
    }
}
