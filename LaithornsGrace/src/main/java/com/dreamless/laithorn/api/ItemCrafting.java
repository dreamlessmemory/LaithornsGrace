package com.dreamless.laithorn.api;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemCrafting {

	private static HashMap<ItemStack, ActionRequirements> playerRequirements = new HashMap<ItemStack, ActionRequirements>();

	public static void registerItemCrafting(Recipe recipe, int levelRequirement, int expRate, List<String> flags, RecipeType type) {
		PlayerMessager.debugLog("ItemCrafting: Registed recipe - " + recipe.getResult() + " Level: " + levelRequirement);
		playerRequirements.put(recipe.getResult(), new ActionRequirements(levelRequirement, flags, expRate, type));
	}
	
	public static boolean  containsRecipe(Recipe recipe) {
		return playerRequirements.containsKey(recipe.getResult());
	}
	
	public static boolean craftingBenchPrepareCheck(CraftingInventory inventory, Player player, Recipe recipe) {	
		ActionRequirements requirements = playerRequirements.get(recipe.getResult());
		
		if (requirements == null) {
			PlayerMessager.debugLog("ItemCrafting: No Requirements");
			return false;
		}
		
		PlayerData data = CacheHandler.getPlayer(player);
		
		// Flag Check
		List<String> flags = requirements.getFlags();
		if (flags != null) {
			for (String flag : flags) {
				if (!data.getFlag(flag)) {
					PlayerMessager.debugLog("ItemCrafting: No Flag - " + flag);
					return false;
				}
			}
		}
		
		if(!recipeTypeCheck(requirements.getType(), inventory)) {
			PlayerMessager.debugLog("ItemCrafting: Failed Type Check");
			return false;
		}
		
		if(data.getSmithingLevel() < requirements.getLevelRequirement()) {
			PlayerMessager.debugLog("ItemCrafting: Too low level");
			return false;
		}
		
		return true;
	}
	
	private static boolean recipeTypeCheck(RecipeType type, CraftingInventory inventory) {
		switch (type) {
		case CENTERED:
			return surroundedByEssence(inventory);
		case ONE_FRAGMENT:
		case TWO_FRAGMENTS:
		case THREE_FRAGMENTS:
		case FOUR_FRAGMENTS:
		case FIVE_FRAGMENTS:
		case SIX_FRAGMENTS:
		case SEVEN_FRAGMENTS:
		case EIGHT_FRAGMENTS:
			return checkForCorrectFragment(inventory, FragmentRarity.DULL, type.getNumberOfFragments());
		default:
			return true;
		}
	}

	private static boolean checkForCorrectFragment(CraftingInventory inventory, FragmentRarity minimumRarityLevel, int fragmentCount) {
		ItemStack[] matrix = inventory.getMatrix();
		int fragmentsCounted = 0;
		for (int i = 0; i < matrix.length; i++) {
			ItemStack itemStack = matrix[i];
			if (itemStack == null)
				continue;
			if (Fragment.isEssence(itemStack)) {
				NBTItem nbti = new NBTItem(itemStack);
				NBTCompound laithorn = nbti.getCompound(Fragment.getTopLevelTag());
				if (laithorn == null) {
					continue;
				}
				for (String key : laithorn.getKeys()) {
					try {
					if (FragmentRarity.valueOf(laithorn.getString(key)).meetsMinimum(minimumRarityLevel))
						++fragmentsCounted;
					} catch (IllegalArgumentException e) {
						// Do nothing, this is fine
					}
				}
			}
		}
		return fragmentCount == fragmentsCounted;
	}

	private static boolean surroundedByEssence(CraftingInventory inventory) {
		ItemStack[] matrix = inventory.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			if (i == 4)
				continue; // ignore the center
			if (!Fragment.isEssence(matrix[i]))
				return false;
		}
		return true;
	}
	
	public static final void init() {}

	private static class ActionRequirements {

		private final int levelRequirement;
		private final List<String> flags;
		private final int expRate;
		private final RecipeType type;

		private ActionRequirements(int levelRequirement, List<String> flags, int expRate, RecipeType type) {
			this.levelRequirement = levelRequirement;
			this.flags = flags;
			this.expRate = expRate;
			this.type = type;
		}

		private final List<String> getFlags() {
			return flags;
		}

		private final int getLevelRequirement() {
			return levelRequirement;
		}

		@SuppressWarnings("unused")
		private final int getExpRate() {
			return expRate;
		}

		public final RecipeType getType() {
			return type;
		}
	} 
}
