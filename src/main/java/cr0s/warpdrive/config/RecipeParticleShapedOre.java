package cr0s.warpdrive.config;

import cr0s.warpdrive.api.IParticleContainerItem;
import cr0s.warpdrive.api.ParticleStack;

import javax.annotation.Nonnull;

import java.util.List;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

// Adds support for IParticleContainerItem ingredients
// Adds support for potions ingredients
// Loosely inspired from vanilla ShapedOreRecipe
public class RecipeParticleShapedOre extends ShapedRecipe {
	
	private final NonNullList<Ingredient> ingredients;
	
	public RecipeParticleShapedOre(final String group, final String suffix, @Nonnull final ItemStack itemStackOutput, final Object... recipe) {
		this(group, suffix, null, itemStackOutput);
	}
	public RecipeParticleShapedOre(final String group, final String suffix, NonNullList<Ingredient> ingredients, @Nonnull final ItemStack itemStackOutput) {
		super(Recipes.buildRecipeId(suffix, itemStackOutput), group, 3, 3, ingredients, itemStackOutput);
		
		this.ingredients = ingredients;
	}
	public boolean matches(@Nonnull final CraftingInventory craftingInventory, @Nonnull final World world) {
		for(int x = 0; x <= craftingInventory.getWidth() - getRecipeWidth(); ++x) {
			for(int y = 0; y <= craftingInventory.getHeight() - getRecipeHeight(); ++y) {
				if (this.checkMatch(craftingInventory, x, y, true)) {
					return true;
				}
				
				if (this.checkMatch(craftingInventory, x, y, false)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean checkMatch(@Nonnull final CraftingInventory craftingInventory, final int startX, final int startY, final boolean mirror) {
		for (int x = 0; x < craftingInventory.getWidth(); x++) {
			for (int y = 0; y < craftingInventory.getHeight(); y++) {
				final int subX = x - startX;
				final int subY = y - startY;
				Ingredient ingredient = Ingredient.EMPTY;
				
				if (subX >= 0 && subY >= 0 && subX < getRecipeWidth() && subY < getRecipeHeight()) {
					if (mirror) {
						ingredient = ingredients.get(getRecipeWidth() - subX - 1 + subY * getRecipeWidth());
					} else {
						ingredient = ingredients.get(subX + subY * getRecipeWidth());
					}
				}
				
				final ItemStack itemStackSlot = craftingInventory.getStackInSlot(x + y * craftingInventory.getWidth());
				final ItemStack[] itemStackTargets = ingredient.getMatchingStacks();
				if (itemStackTargets.length == 1) {// simple ingredient
					final ItemStack itemStackTarget = itemStackTargets[0];
					if ( !itemStackSlot.isEmpty()
					  && itemStackSlot.hasTag() ) {
						if ( itemStackSlot.getItem() instanceof IParticleContainerItem
						  && itemStackTarget.getItem() instanceof IParticleContainerItem ) {
							final IParticleContainerItem particleContainerItemSlot = (IParticleContainerItem) itemStackSlot.getItem();
							final ParticleStack particleStackSlot = particleContainerItemSlot.getParticleStack(itemStackSlot);
							
							final IParticleContainerItem particleContainerItemTarget = (IParticleContainerItem) itemStackTarget.getItem();
							final ParticleStack particleStackTarget = particleContainerItemTarget.getParticleStack(itemStackTarget);
							
							// reject different particles or insufficient quantity
							if (!particleStackSlot.containsParticle(particleStackTarget)) {
								particleContainerItemSlot.setAmountToConsume(itemStackSlot, 0);
								return false;
							}
							
							// it's a match! => mark quantity to consume
							particleContainerItemSlot.setAmountToConsume(itemStackSlot, particleStackTarget.getAmount());
							continue;
							
						} else if ( itemStackSlot.getItem() instanceof PotionItem
						         && itemStackTarget.getItem() instanceof PotionItem) {
							final List<EffectInstance> potionEffectsSlot = PotionUtils.getEffectsFromStack(itemStackSlot);
							final List<EffectInstance> potionEffectsTarget = PotionUtils.getEffectsFromStack(itemStackTarget);
							
							// reject different amount of potion effects 
							if (potionEffectsSlot.size() != potionEffectsTarget.size()) {
								return false;
							}
							
							// verify matching effects
							for (final EffectInstance potionEffectTarget : potionEffectsTarget) {
								if (!potionEffectsSlot.contains(potionEffectTarget)) {
									return false;
								}
							}
							
							// it's a match!
							continue;
						}
						// (single item stack but neither particle nor potion => default to ore dictionary matching)
					}
					
					if (!ingredient.test(itemStackTarget)) {
						return false;
					}
					
				} else if (!ingredient.test(itemStackSlot)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
}
