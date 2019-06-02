package com.dreamless.laithorn.api;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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

	public static boolean anvilPrepareCheck(AnvilInventory inventory, Material itemType, Player player,
			String repairString, List<String> flags) {

		// Left Side
		ItemStack leftSide = inventory.getItem(0);
		if (leftSide == null || leftSide.getType() != itemType && ItemCrafting.isLaithornEnchanted(leftSide)) {
			return false;
		}

		// Right side
		ItemStack rightSide = inventory.getItem(1);
		if (rightSide == null || ItemCrafting.isEssence(rightSide)) {
			return false;
		}

		return RequirementsHandler.canDoAction(player, repairString, flags);
	}

	public static void anvilPickupCheck(Material itemType, InventoryClickEvent event, int repairRate,
			int expGainnumber) {
		// Check if anvil
		if (!(event.getInventory() instanceof AnvilInventory)) {
			return;
		}

		Inventory inventory = event.getInventory();

		// Check slot click
		if (event.getSlot() != 2) {
			return;
		}

		// Left Side
		ItemStack leftSide = inventory.getItem(0);
		if (leftSide == null || leftSide.getType() != itemType && ItemCrafting.isLaithornEnchanted(leftSide)) {
			return;
		}

		// Right side
		ItemStack rightSide = inventory.getItem(1);
		if (rightSide == null || ItemCrafting.isEssence(rightSide)) {
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
		int finalrepairRate = (int) (repairRate * FragmentRarity.valueOf(laithorn.getString("level")).rarityModifier()
				* rightSide.getAmount());
		int fragmentsUsed = (repairValue + finalrepairRate - 1) / finalrepairRate;

		int expGain = repairValue * expGainnumber;

		Bukkit.getPluginManager().callEvent(new PlayerExperienceGainEvent(player, expGain, GainType.SMITHING, true));

		// blank out
		inventory.setItem(0, null);

		if (rightSide.getAmount() > fragmentsUsed) {
			rightSide.setAmount(rightSide.getAmount() - fragmentsUsed);
			inventory.setItem(1, rightSide);
		} else {
			inventory.setItem(1, null);
		}

		inventory.setItem(2, null);
	}

	public static ItemStack generateRepairedItem(AnvilInventory inventory, int repairRate, List<String> bonusTags) {

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
}
