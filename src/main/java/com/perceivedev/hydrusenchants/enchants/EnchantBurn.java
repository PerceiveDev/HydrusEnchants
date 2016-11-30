package com.perceivedev.hydrusenchants.enchants;

import java.util.Arrays;
import java.util.List;

import org.bukkit.potion.PotionEffectType;

import com.perceivedev.hydrusenchants.ItemType;
import com.perceivedev.hydrusenchants.enchant.ItemSlot;
import com.perceivedev.hydrusenchants.enchant.Rarity;
import com.perceivedev.hydrusenchants.enchant.types.PotionEnchant;

/**
 * @author Rayzr
 *
 */
public class EnchantBurn extends PotionEnchant {

    public EnchantBurn() {
        super(new PotionEnchantData(ItemSlot.HELMET, PotionEffectType.FIRE_RESISTANCE, lvl -> 0));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.perceivedev.hydrusenchants.enchant.Enchant#getIdentifier()
     */
    @Override
    public String getIdentifier() {
        return "BURN";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.perceivedev.hydrusenchants.enchant.Enchant#getDisplay()
     */
    @Override
    public String getDisplay() {
        return "Burn";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.perceivedev.hydrusenchants.enchant.Enchant#maxLevel()
     */
    @Override
    public int maxLevel() {
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.perceivedev.hydrusenchants.enchant.Enchant#getRarity()
     */
    @Override
    public Rarity getRarity() {
        return Rarity.EPIC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.perceivedev.hydrusenchants.enchant.Enchant#getApplicableItems()
     */
    @Override
    public List<ItemType> getApplicableItems() {
        return Arrays.asList(ItemType.HELMET);
    }

}