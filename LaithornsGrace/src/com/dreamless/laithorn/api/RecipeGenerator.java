package com.dreamless.laithorn.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;

public class RecipeGenerator {
	public static final ShapedRecipe generateCenteredRecipe(Plugin plugin, String recipeName, Material coreMaterial,
			ItemStack resultingItem) {
	
		ArrayList<Material> choices = new ArrayList<Material>();
		choices.add(coreMaterial);
	
		return generateCenteredRecipe(plugin, recipeName, choices, resultingItem);
	}

	public static final ShapedRecipe generateCenteredRecipe(Plugin plugin, String recipeName,
			List<Material> coreMaterials, ItemStack resultingItem) {
		NamespacedKey key = new NamespacedKey(plugin, recipeName);
	
		ShapedRecipe recipe = new ShapedRecipe(key, resultingItem);
	
		recipe.shape("EEE", "ESE", "EEE");
		recipe.setIngredient('E', Fragment.getFragmentMaterial());
	
		MaterialChoice choice = new MaterialChoice(coreMaterials);
		recipe.setIngredient('S', choice);
	
		return recipe;
	}

	public static final ShapelessRecipe generateShapelessRecipe(Plugin plugin, String providedKey, Material material,
			ItemStack item, int fragmentCount) {
		ArrayList<Material> choices = new ArrayList<Material>();
		choices.add(material);
	
		return generateShapelessRecipe(plugin, providedKey, choices, item, fragmentCount);
	}

	public static final ShapelessRecipe generateShapelessRecipe(Plugin plugin, String providedKey,
			List<Material> material, ItemStack item, int fragmentCount) {
		NamespacedKey key = new NamespacedKey(plugin, providedKey);
	
		ShapelessRecipe recipe = new ShapelessRecipe(key, item);
	
		MaterialChoice choice = new MaterialChoice(material);
		recipe.addIngredient(choice);
		if (fragmentCount < 0) {
			fragmentCount = 1;
		} else if (fragmentCount > 8) {
			fragmentCount = 8;
		}
	
		for (int i = 0; i < fragmentCount; ++i) {
			recipe.addIngredient(Fragment.getFragmentMaterial());
		}
	
		return recipe;
	}
}
