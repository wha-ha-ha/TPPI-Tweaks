package tppitweaks.recipetweaks.modTweaks;

import codechicken.enderstorage.EnderStorage;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tppitweaks.config.ConfigurationHandler;
import tppitweaks.recipetweaks.RecipeAddition;
import tppitweaks.recipetweaks.RecipeRemoval;
import tppitweaks.recipetweaks.TweakingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class EnderStorageTweaks extends TweakingRegistry
{
	@RecipeRemoval(requiredModids = { "EnderStorage", "ThermalExpansion" })
	public static void init()
	{
		TweakingRegistry.markItemForRecipeRemoval(((Block) codechicken.enderstorage.EnderStorage.blockEnderChest).blockID, -1, TweakingAction.CHANGED, "Recipe requires tesseract",
				"because it is better than one", "and requires midgame materials");
		if (ConfigurationHandler.enderPouchNerf)
			TweakingRegistry.markItemForRecipeRemoval(((Item) codechicken.enderstorage.EnderStorage.itemEnderPouch).itemID, -1, TweakingAction.CHANGED, "Recipe requires pyrotheum+ender bucket",
					"so it requires midgame infrastructure");
	}

	@RecipeAddition(requiredModids = { "EnderStorage", "ThermalExpansion" })
	public static void addRecipes()
	{
		Object chestEnderElement = ConfigurationHandler.enderChestTesseract ? thermalexpansion.block.TEBlocks.blockTesseract : Item.enderPearl;
		Object tankEnderElement = ConfigurationHandler.enderTankTesseract ? thermalexpansion.block.TEBlocks.blockTesseract : Item.enderPearl;

		for (int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ItemStack(codechicken.enderstorage.EnderStorage.blockEnderChest, 1, codechicken.enderstorage.api.EnderStorageManager.getFreqFromColours(i, i, i)), new Object[] {
					"bWb", "OCO", "bpb", 'b', Item.blazeRod, 'p', chestEnderElement, 'O', Block.obsidian, 'C', Block.chest, 'W', new ItemStack(Block.cloth, 1, i) });

			if (!ConfigurationHandler.disableEnderTank)
			{
				GameRegistry.addRecipe(new ItemStack(codechicken.enderstorage.EnderStorage.blockEnderChest, 1, 1 << 12 | codechicken.enderstorage.api.EnderStorageManager.getFreqFromColours(i, i, i)),
						new Object[] { "bWb", "OCO", "bpb", 'b', Item.blazeRod, 'p', tankEnderElement, 'O', Block.obsidian, 'C', Item.cauldron, 'W', new ItemStack(Block.cloth, 1, i) });
			}
			else
			{
				for (int j = 0; j < 16; j++)
				{
					for (int k = 0; k < 16; k++)
					{
						TweakingRegistry.addTweakedTooltip(((Block) EnderStorage.blockEnderChest).blockID, 1 << 12 | codechicken.enderstorage.api.EnderStorageManager.getFreqFromColours(i, j, k), TweakingAction.REMOVED, "Has a serious dupe bug.");
					}
				}
			}

			if (ConfigurationHandler.enderPouchNerf)
			{
				GameRegistry.addRecipe(new ItemStack(codechicken.enderstorage.EnderStorage.itemEnderPouch, 1, codechicken.enderstorage.api.EnderStorageManager.getFreqFromColours(i, i, i)), "pWp",
						"lel", "plp",

						'p', thermalexpansion.item.TEItems.dustPyrotheum, 'l', Item.leather, 'e', new ItemStack(thermalexpansion.fluid.TEFluids.itemBucket, 1, 2), 'W',
						new ItemStack(Block.cloth, 1, i));
			}
		}
	}
}
