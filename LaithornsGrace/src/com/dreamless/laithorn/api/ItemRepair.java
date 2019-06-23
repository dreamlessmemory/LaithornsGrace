package com.dreamless.laithorn.api;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.events.PlayerExperienceGainEvent;
import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;

public class ItemRepair {

	private static HashMap<Material, ActionRequirements> playerRequirements = new HashMap<Material, ActionRequirements>();

	protected static boolean anvilPrepareCheck(AnvilInventory inventory, Player player) {
		ItemStack leftSlot = inventory.getItem(0);
		ItemStack rightSlot = inventory.getItem(1);
		ActionRequirements requirements = getRequirements(leftSlot);

		return baseItemCheck(leftSlot, requirements) && essenceCheck(rightSlot) && canDoAction(player, requirements);
	}
	
	protected static void anvilPickupCheck(Inventory inventory, ItemStack result, Player player, boolean shiftClick) {

		// Left Side
		ItemStack leftSide = inventory.getItem(0);
		ItemStack rightSide = inventory.getItem(1);		
		ActionRequirements requirements = getRequirements(leftSide);
		
		if (!baseItemCheck(leftSide, requirements) || !essenceCheck(rightSide)) {
			return;
		}

		// Give item to player
		if (shiftClick) {
			player.getInventory().addItem(result);
			player.updateInventory();
		} else {
			player.setItemOnCursor(result);
		}

		// Exp Event
		Damageable previousDamage = (Damageable) leftSide.getItemMeta();
		Damageable finalDamage = (Damageable) result.getItemMeta();

		int repairValue = previousDamage.getDamage() - finalDamage.getDamage();

		// Get number of fragments used
		NBTItem nbti = new NBTItem(rightSide);
		NBTCompound laithorn = nbti.getCompound("Laithorn");
		int finalrepairRate = (int) (requirements.getRepairRate() * FragmentRarity.valueOf(laithorn.getString("level")).rarityModifier());
		int fragmentsUsed = (repairValue + finalrepairRate - 1) / finalrepairRate;

		int expGain = repairValue * requirements.getExpRate();

		Bukkit.getPluginManager().callEvent(new PlayerExperienceGainEvent(player, expGain, GainType.SMITHING, true));

		// Blank out Left Side
		inventory.setItem(0, null);

		// Reduce Right Side
		if (rightSide.getAmount() > fragmentsUsed) {
			rightSide.setAmount(rightSide.getAmount() - fragmentsUsed);
			inventory.setItem(1, rightSide);
		} else {
			inventory.setItem(1, null);
		}

		// Blank out right side
		inventory.setItem(2, null);
	}

	protected static ItemStack generateRepairedItem(AnvilInventory inventory) {

		ItemStack baseItem = inventory.getItem(0);
		ItemStack essence = inventory.getItem(1);

		ItemStack repairedItem = baseItem.clone();
		Damageable damageable = (Damageable) baseItem.getItemMeta();

		int repairValue = 0;
		
		ActionRequirements requirements = getRequirements(baseItem);

		// NBT Setup
		NBTItem nbti = new NBTItem(essence);
		NBTCompound laithorn = nbti.getCompound("Laithorn");
		if (laithorn == null) {
			PlayerMessager.debugLog("Cannot Repair: Item is not a fragment");
		} else {
			repairValue = (int) (requirements.getRepairRate() * FragmentRarity.valueOf(laithorn.getString("level")).rarityModifier()
					* essence.getAmount());
		}

		PlayerMessager.debugLog("Repairing by " + repairValue);
		damageable.setDamage(Math.max(damageable.getDamage() - repairValue, 0));

		repairedItem.setItemMeta((ItemMeta) damageable);

		return repairedItem;
	}
	
	protected static void registerItemRepair(Material material, int levelRequirement, int expRate, int repairRate, boolean isEnchanted, List<String> flags) {
		playerRequirements.put(material, new ActionRequirements(isEnchanted, levelRequirement, flags, expRate, repairRate));
	}
	
	protected static boolean removeItemRepair(Material material) {
		return playerRequirements.remove(material) != null;
	}

	private static final boolean baseItemCheck(ItemStack item, ActionRequirements requirements) {
		// Null Check
		if (item == null) {
			PlayerMessager.debugLog("Repair check failed - null item in left side");
			return false;
		}

		// Registry Check
		if (requirements == null) {
			PlayerMessager.debugLog("Repair check failed - not registered as repairable");
			return false;
		}

		// Enchantment Check
		if (requirements.isEnchanted() && !Fragment.isLaithornEnchanted(item)) {
			PlayerMessager.debugLog("Repair check failed - item is not Laithorn-powered");
			return false;
		}
		return true;
	}

	private static final boolean essenceCheck(ItemStack essence) {
		if (essence == null) {
			PlayerMessager.debugLog("Repair check failed - no null item in right side");
			return false;
		}

		if (!Fragment.isEssence(essence)) {
			PlayerMessager.debugLog("Repair check failed - no essence");
			return false;
		}
		return true;
	}
	
	private static final ActionRequirements getRequirements(ItemStack item) {
		if(item == null) {
			return null;
		}
		return playerRequirements.get(item.getType());
	}

	private static boolean canDoAction(Player player, ActionRequirements requirements) {

		// Registry Check
		if (requirements == null) {
			PlayerMessager.debugLog("Repair check failed - not registered as repairable");
			return false;
		}

		PlayerData data = CacheHandler.getPlayer(player);
		List<String> flags = requirements.getFlags(); 
		
		if(data.getSmithingLevel() < requirements.getLevelRequirement()) {
			PlayerMessager.debugLog("Repair check failed - player does not have the appropriate smithing level");
			return false;
		}

		if (flags != null) {
			for (String flag : flags) {
				if (!data.getFlag(flag))
					PlayerMessager.debugLog("Repair check failed - player does not have the flag: " + flag);
					return false;
			}
		}
		return true;
	}

	private static class ActionRequirements {

		private final boolean isEnchanted;
		private final int levelRequirement;
		private final List<String> flags;
		private final int expRate;
		private final int repairRate;
		
		private ActionRequirements(boolean isEnchanted, int levelRequirement, List<String> flags, int expRate,
				int repairRate) {
			this.isEnchanted = isEnchanted;
			this.levelRequirement = levelRequirement;
			this.flags = flags;
			this.expRate = expRate;
			this.repairRate = repairRate;
		}

		private final boolean isEnchanted() {
			return isEnchanted;
		}

		private final List<String> getFlags() {
			return flags;
		}

		private int getLevelRequirement() {
			return levelRequirement;
		}

		private final int getExpRate() {
			return expRate;
		}

		private final int getRepairRate() {
			return repairRate;
		}
	}
}
