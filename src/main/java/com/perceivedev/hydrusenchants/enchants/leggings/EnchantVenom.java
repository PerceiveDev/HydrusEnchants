package com.perceivedev.hydrusenchants.enchants.leggings;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.perceivedev.hydrusenchants.ItemType;
import com.perceivedev.hydrusenchants.enchant.Rarity;
import com.perceivedev.hydrusenchants.enchant.types.Enchant;

/**
 * @author Rayzr
 *
 */
public class EnchantVenom extends Enchant {

	public EnchantVenom() {
		super(EntityDamageByEntityEvent.class);

		registerEventHandler(EntityDamageByEntityEvent.class, e -> {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
			if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
				return;
			}
			Player player = (Player) event.getEntity();
			int level = getEnchantLevel(player.getInventory().getChestplate());
			if (level < 0) {
				return;
			}
			Player damager = (Player) event.getDamager();

			switch (level) {
			case 1:
				if (Math.random() < 0.50) {
					damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 0));
				}
				break;
			case 2:
				if (Math.random() < 0.60) {
					damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 8 * 20, 1));
				}
				break;
			default:
				break;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.perceivedev.hydrusenchants.enchant.types.Enchant#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return "VENOM";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.perceivedev.hydrusenchants.enchant.types.Enchant#getDisplay()
	 */
	@Override
	public String getDisplay() {
		return "Venom";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.perceivedev.hydrusenchants.enchant.types.Enchant#maxLevel()
	 */
	@Override
	public int maxLevel() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.perceivedev.hydrusenchants.enchant.types.Enchant#getRarity()
	 */
	@Override
	public Rarity getRarity() {
		return Rarity.EPIC;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.perceivedev.hydrusenchants.enchant.types.Enchant#getApplicableItems()
	 */
	@Override
	public List<ItemType> getApplicableItems() {
		return Arrays.asList(ItemType.LEGGINGS);
	}

}