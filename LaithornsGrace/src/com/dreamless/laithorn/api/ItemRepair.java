package com.dreamless.laithorn.api;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.events.PlayerExperienceGainEvent;
import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;

import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class ItemRepair {

	public static boolean anvilPrepareCheck(PrepareAnvilEvent event, Material itemType, String repairString,
			List<String> flags) {

		AnvilInventory inventory = event.getInventory();
		Player player = (Player) event.getView().getPlayer();

		return baseItemCheck(inventory.getItem(0), itemType) && essenceCheck(inventory.getItem(1))
				&& RequirementsHandler.canDoAction(player, repairString, flags);
	}

	public static void anvilPickupCheck(Material itemType, InventoryClickEvent event, int repairRate,
			int expGainnumber) {
		// Check if anvil
		if (!(event.getInventory() instanceof AnvilInventory)) {
			PlayerMessager.debugLog("ANVIL FAIL");
			return;
		}

		// Check slot click
		if (event.getSlot() != 2) {
			PlayerMessager.debugLog("CLICK FAIL");
			return;
		}

		Inventory inventory = event.getInventory();

		// Left Side
		ItemStack leftSide = inventory.getItem(0);
		if (!baseItemCheck(leftSide, itemType)) {
			return;
		}

		// Right side
		ItemStack rightSide = inventory.getItem(1);
		if (!essenceCheck(rightSide)) {
			return;
		}

		ItemStack result = event.getCurrentItem();

		// Give item to player
		Player player = (Player) event.getWhoClicked();
		if (event.isShiftClick()) {
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
		int finalrepairRate = (int) (repairRate * FragmentRarity.valueOf(laithorn.getString("level")).rarityModifier());
		int fragmentsUsed = (repairValue + finalrepairRate - 1) / finalrepairRate;

		int expGain = repairValue * expGainnumber;

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

	public static ItemStack generateRepairedItem(AnvilInventory inventory, int repairRate) {

		ItemStack baseItem = inventory.getItem(0);
		ItemStack essence = inventory.getItem(1);

		ItemStack repairedItem = baseItem.clone();
		Damageable damageable = (Damageable) baseItem.getItemMeta();

		int repairValue = 0;

		// NBT Setup
		NBTItem nbti = new NBTItem(essence);
		NBTCompound laithorn = nbti.getCompound("Laithorn");
		if (laithorn == null) {
			PlayerMessager.debugLog("Cannot Repair: Item is not a fragment");
		} else {
			repairValue = (int) (repairRate * FragmentRarity.valueOf(laithorn.getString("level")).rarityModifier()
					* essence.getAmount());
		}

		PlayerMessager.debugLog("Repairing by " + repairValue);
		damageable.setDamage(Math.max(damageable.getDamage() - repairValue, 0));

		repairedItem.setItemMeta((ItemMeta) damageable);

		return repairedItem;
	}

	private static final boolean baseItemCheck(ItemStack item, Material itemType) {
		// Left Side
		if (item == null || item.getType() != itemType || !ItemCrafting.isLaithornEnchanted(item)) {
			PlayerMessager.debugLog("LEFT FAIL");
			return false;
		}
		return true;
	}

	private static final boolean essenceCheck(ItemStack essence) {
		if (essence == null || !ItemCrafting.isEssence(essence)) {
			PlayerMessager.debugLog("RIGHT FAIL");
			return false;
		}
		return true;
	}
}
