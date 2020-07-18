package cr0s.warpdrive.config;

import cr0s.warpdrive.item.ItemTuningDriver;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

// Used to change tuning driver values
public class RecipeTuningDriver implements ICraftingRecipe {
	
	private final ResourceLocation id;
	
	private final ItemStack itemStackTool;
	private final ItemStack itemStackConsumable;
	private final int countDyesExpected;
	private ItemStack itemStackResult = ItemStack.EMPTY;
	private final int size;
	private final String group;
	
	public RecipeTuningDriver(@Nonnull final String group, @Nonnull final String suffix, @Nonnull final ItemStack itemStackTool,
	                          @Nonnull final ItemStack itemStackConsumable, final int countDyesExpected) {
		this.id = Recipes.buildRecipeId(suffix, itemStackTool);
		this.group = group;
		this.itemStackTool = itemStackTool.copy();
		this.itemStackConsumable = itemStackConsumable.copy();
		this.countDyesExpected = countDyesExpected;
		this.size = 1 + (itemStackConsumable.isEmpty() ? 0 : 1) + countDyesExpected;
	}
	
	@Nonnull
	@Override
	public ResourceLocation getId() {
		return this.id;
	}
	
	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return IRecipeSerializer.CRAFTING_SHAPELESS;
	}
	
	@Override
	@Nonnull
	public String getGroup() {
		return group;
	}
	
	@Override
	public boolean canFit(final int width, final int height) {
		return width * height >= size;
	}
	
	// Returns an Item that is the result of this recipe
	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull final CraftingInventory inventoryCrafting) {
		return itemStackResult.copy();
	}
	
	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return itemStackResult;
	}
	
	// check if a recipe matches current crafting inventory
	@Override
	public boolean matches(@Nonnull final CraftingInventory inventoryCrafting, @Nonnull final World world) {
		ItemStack itemStackInput = null;
		boolean isConsumableFound = false;
		int dye = 0;
		int countDyesFound = 0;
		for (int indexSlot = 0; indexSlot < inventoryCrafting.getSizeInventory(); indexSlot++) {
			final ItemStack itemStackSlot = inventoryCrafting.getStackInSlot(indexSlot);
			
			//noinspection StatementWithEmptyBody
			if (itemStackSlot.isEmpty()) {
				// continue
			} else if (itemStackTool.isItemEqualIgnoreDurability(itemStackSlot)) {
				// too many inputs?
				if (itemStackInput != null) {
					return false;
				}
				itemStackInput = itemStackSlot;
				
			} else if (itemStackConsumable.isItemEqual(itemStackSlot)) {
				// too many consumables?
				if (isConsumableFound) {
					return false;
				}
				isConsumableFound = true;
				
			} else {
				// find a matching dye from ore dictionary
				boolean matched = false;
				for (final DyeColor enumDyeColor : DyeColor.values()) {
					final List<ItemStack> itemStackDyes = OreDictionary.getOres(Recipes.oreDyes.get(enumDyeColor));
					for (final ItemStack itemStackDye : itemStackDyes) {
						if (OreDictionary.itemMatches(itemStackSlot, itemStackDye, true)) {
							// match found, update dye combination
							matched = true;
							countDyesFound++;
							dye = dye * 16 + enumDyeColor.getId();
						}
					}
				}
				if (!matched) {
					return false;
				}
			}
		}
		
		// missing input
		if (itemStackInput == null) {
			return false;
		}
		
		// missing or too many dyes
		if (countDyesFound != countDyesExpected) {
			return false;
		}
		
		// consumable missing or not required
		if ( (itemStackConsumable != null && !isConsumableFound)
		  || (itemStackConsumable == null &&  isConsumableFound) ) {
			return false;
		}
		
		// build result
		itemStackResult = ItemTuningDriver.setValue(itemStackInput.copy(), dye);
		
		return true;
	}
}
