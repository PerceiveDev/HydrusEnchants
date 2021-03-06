/**
 * 
 */
package com.perceivedev.essentialenchants.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.perceivedev.essentialenchants.EssentialEnchants;
import com.perceivedev.essentialenchants.enchant.types.Enchant;

/**
 * @author Rayzr
 *
 */
public class Utils {

    private static String BUKKIT_VERSION;
    private static MethodHandle ITEM_CAUSES_DROPS;

    private static boolean OFF_HAND = false;
    private static boolean TIPPED_ARROWS = false;

    static {
        String[] versionSplit = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        BUKKIT_VERSION = versionSplit[versionSplit.length - 1];

        // Hacky way to check if this server supports the off-hand slot
        try {
            EquipmentSlot.OFF_HAND.name();
            OFF_HAND = true;
        } catch (NoSuchFieldError e) {
            // Ignore
        }

        // Hacky way to check if this server supports tipped arrows
        try {
            Material.TIPPED_ARROW.name();
            TIPPED_ARROWS = true;
        } catch (NoSuchFieldError e) {
            // Ignore
        }

        try {
            Class<?> CRAFTBLOCK = Class.forName("org.bukkit.craftbukkit." + BUKKIT_VERSION + ".block.CraftBlock");
            Method base = CRAFTBLOCK.getDeclaredMethod("itemCausesDrops", ItemStack.class);
            base.setAccessible(true);
            ITEM_CAUSES_DROPS = MethodHandles.lookup().unreflect(base);
        } catch (Exception e) {
            EssentialEnchants.getInstance().getLogger().log(Level.SEVERE, "Failed to find the 'itemCausesDrops' method in CraftBlock!", e);
        }
    }

    /**
     * For some reason, this method is not preset in the 1.8 API for players. I
     * had to manually create it.
     * 
     * @param self the player to check
     * @param type the {@link PotionEffectType} to check for
     * @return The {@link PotionEffect}, or <code>null</code> if it wasn't found
     *         (just like the newer API)
     */
    public static PotionEffect getPotionEffect(Player self, PotionEffectType type) {
        Objects.requireNonNull(self, "self cannot be null!");
        Objects.requireNonNull(type, "type cannot be null!");
        return self.getActivePotionEffects().stream().filter(eff -> eff.getType() == type).findFirst().orElse(null);
    }

    /**
     * A cross-compatibility method for checking the off-hand only when the
     * server is running a version with off-hand support (1.9+)
     * 
     * @param self the player to check (checks main-hand, then off-hand)
     * @param enchant the enchantment to check for
     * @return The level of the enchant, or -1 if it is not present in either
     *         main-hand or off-hand
     */
    public static int getHandLevel(Player self, Enchant enchant) {
        PlayerInventory inv = self.getInventory();
        @SuppressWarnings("deprecation")
        int lvl = enchant.getEnchantLevel(inv.getItemInHand());
        if (lvl > -1) {
            return lvl;
        }
        if (OFF_HAND) {
            lvl = enchant.getEnchantLevel(inv.getItemInOffHand());
            if (lvl > -1) {
                return lvl;
            }
        }
        return -1;
    }

    /**
     * A cross-compatibility method for checking the off-hand only when the
     * server is running a version with off-hand support (1.9+)
     * <br>
     * <br>
     * This is just a simple alias for doing the following:
     * 
     * <pre>
     * OneDotEightUtils.getHandLevel(self, enchant) != -1
     * </pre>
     * 
     * @param self the player to check (checks main-hand, then off-hand)
     * @param enchant the enchantment to check for
     * @return Whether or not the player is holding an item with the given
     *         enchant in either main-hand or off-hand
     */
    public static boolean isHandEnchanted(Player self, Enchant enchant) {
        return getHandLevel(self, enchant) != -1;
    }

    /**
     * A utility method for consuming the first
     * 
     * @param self
     * @param predicate
     * @return
     */
    public static boolean consumeFirst(Player self, Predicate<ItemStack> predicate) {
        Objects.requireNonNull(self, "self cannot be null!");
        Objects.requireNonNull(predicate, "predicate cannot be null!");
        Optional<ItemStack> i = Arrays.stream(self.getInventory().getStorageContents()).filter(predicate).findFirst();
        if (!i.isPresent()) {
            return false;
        }
        ItemStack consumable = i.get();
        int slot = self.getInventory().first(consumable);
        int amount = consumable.getAmount() - 1;
        if (amount < 1) {
            self.getInventory().setItem(slot, null);
        }
        consumable.setAmount(amount);
        self.getInventory().setItem(slot, consumable);
        return true;
    }

    /**
     * Attempts to consume an item from the player's inventory.
     * 
     * @param self the player to consume the item from
     * @param item the item to consume
     * @return Whether or not the item was consumed
     */
    public static boolean consume(Player self, ItemStack item) {
        Objects.requireNonNull(item, "item cannot be null!");
        Validate.isTrue(item.getType() != Material.AIR, "item cannot be AIR!");
        return consumeFirst(self, it -> item.isSimilar(it));
    }

    /**
     * Attempts to consume an item from the player's inventory.
     * 
     * @param self the player to consume the item from
     * @param type the type of item to consume
     * @return Whether or not the item was consumed
     */
    public static boolean consume(Player self, Material type) {
        return consume(self, new ItemStack(type));
    }

    /**
     * Consumes an arrow from the player, including a cross-compatibility check
     * for newer servers that have tipped arrows
     * 
     * @param self the player to consume the arrow from
     * @return Whether or not any kind of error was consumed
     */
    public static boolean consumeArrow(Player self) {
        return consumeFirst(self, it -> it != null && (it.getType() == Material.ARROW || (TIPPED_ARROWS && it.getType() == Material.TIPPED_ARROW)));
    }

    /**
     * Fixes odd durabilities of {@link Short#MAX_VALUE} on items in recipes
     * 
     * @param item the {@link ItemStack} to fix the durability of
     * @return The normalized item
     */
    public static ItemStack fixDurability(ItemStack item) {
        if (item.getDurability() != Short.MAX_VALUE) {
            return item.clone();
        }
        ItemStack fixed = item.clone();
        fixed.setDurability((short) 0);
        return fixed;
    }

    /**
     * Removes a certain amount of durability from an {@link ItemStack}, taking
     * into account the {@link Enchantment#DURABILITY Unbreaking} enchantment.
     * 
     * @param item the item to damage
     * @param amount the amount of durability to take from the item
     * @return The modified item or <code>null</code> if the damage was enough
     *         to break the item.
     */
    public static ItemStack damage(ItemStack item, int amount) {
        if (item == null) {
            return null;
        }
        Validate.isTrue(item.getDurability() + amount < Short.MAX_VALUE, "damage amount is too high!");

        ItemStack newItem = item.clone();
        int unbreaking = newItem.getEnchantmentLevel(Enchantment.DURABILITY);

        // Account for Unbreaking (thank you Minecraft Wiki for the lovely
        // formula!)
        if (unbreaking > 0 && Math.random() > (100.0 / (unbreaking + 1)) / 100.0) {
            return newItem;
        }

        newItem.setDurability((short) (newItem.getDurability() + amount));
        return newItem.getDurability() > newItem.getType().getMaxDurability() ? null : newItem;
    }

    /**
     * Removes a certain amount of durability from an {@link ItemStack}, taking
     * into account the {@link Enchantment#DURABILITY Unbreaking} enchantment.
     * This
     * calls {@link #damage(ItemStack, int)} with an amount of <code>1</code>.
     * 
     * @param item the item to damage
     * @return The modified item or <code>null</code> if the damage was enough
     *         to break the item.
     */
    public static ItemStack damage(ItemStack item) {
        return damage(item, 1);
    }

    /**
     * Checks if a certain block will drop any items when mined with the given
     * tool
     * 
     * @param block the block to check
     * @param tool the tool to use
     * @return Whether or not the tool causes items to drop
     */
    public static boolean itemCausesDrops(Block block, ItemStack tool) {
        try {
            return (boolean) ITEM_CAUSES_DROPS.bindTo(block).invoke(tool);
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * @param level the level of fortune enchant
     * @return The multiplier for item drops
     */
    public static int getFortuneMultiplier(int level) {
        if (level < 1)
            return 1;
        double chance = level == 1 ? 0.33 : (level == 2 ? 0.25 : 0.2);
        for (int i = 0; i < level; i++) {
            if (Math.random() < chance) {
                return i + 2;
            }
        }
        return 1;
    }

}
