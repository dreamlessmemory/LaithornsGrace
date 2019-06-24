package com.dreamless.laithorn.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

public class LaithornRegister {

	public static void registerItemRepair(Material material, int levelRequirement, int expRate, int repairRate,
			boolean isEnchanted, List<String> flags) {
		ItemRepair.registerItemRepair(material, levelRequirement, expRate, repairRate, isEnchanted, flags);
	}

	public static void registerItemCrafting(Recipe recipe, int levelRequirement,  int expRate, RecipeType type, List<String> flags) {
		// Add to bukkit
		Bukkit.addRecipe(recipe);

		// Add to ItemCrafting
		ItemCrafting.registerItemCrafting(recipe, levelRequirement, expRate, flags, type);
	}

	public static void registerItemCrafting(ItemStack output, Material input, int levelRequirement, int expRate,
			RecipeType type, List<String> flags, String itemName, Plugin plugin) {
		ArrayList<Material> inputs = new ArrayList<Material>();
		inputs.add(input);
		registerItemCrafting(output, inputs, levelRequirement, expRate, type, flags, itemName, plugin);
	}

	public static void registerItemCrafting(ItemStack output, List<Material> input, int levelRequirement, int expRate,
			RecipeType type, List<String> flags, String itemName, Plugin plugin) {
		Recipe recipe;
		switch (type) {
		case CENTERED:
			recipe = RecipeGenerator.generateCenteredRecipe(plugin, itemName, input, output);
			break;
		case ONE_FRAGMENT:
		case TWO_FRAGMENTS:
		case THREE_FRAGMENTS:
		case FOUR_FRAGMENTS:
		case FIVE_FRAGMENTS:
		case SIX_FRAGMENTS:
		case SEVEN_FRAGMENTS:
		case EIGHT_FRAGMENTS:
			recipe = RecipeGenerator.generateShapelessRecipe(plugin, itemName, input, output,
					type.getNumberOfFragments());
			break;
		default:
			return;
		}
		
		registerItemCrafting(recipe, levelRequirement, expRate, type, flags);
	}
}
