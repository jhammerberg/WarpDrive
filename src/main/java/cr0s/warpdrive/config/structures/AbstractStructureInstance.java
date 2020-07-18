package cr0s.warpdrive.config.structures;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

/**
 * @author LemADEC
 *
 */
public abstract class AbstractStructureInstance extends Feature<NoFeatureConfig> {
	
	protected AbstractStructure structure;
	protected HashMap<String,Double> variables = new HashMap<>();
	
	public AbstractStructureInstance(final AbstractStructure structure, final Random random) {
		super(NoFeatureConfig::deserialize);
		
		this.structure = structure;
		
		// evaluate variables
		for (final Entry<String, String> entry : structure.variables.entrySet()) {
			final double value;
			String stringValue = entry.getValue();
			try {
				if (stringValue.contains(",")) {
					final String[] values = stringValue.split(",");
					stringValue = values[random.nextInt(values.length)];
				}
				value = Double.parseDouble(entry.getValue());
			} catch (final NumberFormatException exception) {
				throw new RuntimeException(String.format("Invalid expression '%s'%s for variable %s in deployable structure %s: a numeric value is expected. Check the related XML configuration file...",
				                                         entry.getValue(),
				                                         (stringValue.equalsIgnoreCase(entry.getValue()) ? "" : " in '" + entry.getValue() + "'"),
				                                         entry.getKey(),
				                                         structure.name));
			}
			
			variables.put(entry.getKey(), value);
		}
	}
	
	protected String evaluate(final String valueOrExpression) {
		if (!valueOrExpression.contains("%")) {
			return valueOrExpression;
		}
		String result = valueOrExpression;
		for (final Entry<String, Double> variable : variables.entrySet()) {
			result = result.replaceAll(variable.getKey(), "" + variable.getValue());
		}
		return result;
	}
	
	public AbstractStructureInstance(final CompoundNBT tagCompound) {
		super(NoFeatureConfig::deserialize);
		
		// get structure
		final String groupStructure = tagCompound.getString("group");
		final String nameStructure = tagCompound.getString("name");
		structure = StructureManager.getStructure(null, groupStructure, nameStructure);
		
		// get variables values
		final CompoundNBT tagVariables = tagCompound.getCompound("variables");
		for (final String key : tagVariables.keySet()) {
			final double value = tagVariables.getDouble(key);
			variables.put(key, value);
		}
	}
	
	public CompoundNBT write(@Nonnull final CompoundNBT tagCompound) {
		tagCompound.putString("group", structure.group);
		tagCompound.putString("name", structure.name);
		
		if (!variables.isEmpty()) {
			final CompoundNBT tagVariables = new CompoundNBT();
			for (final Entry<String, Double> entry : variables.entrySet()) {
				tagVariables.putDouble(entry.getKey(), entry.getValue());
			}
			tagCompound.put("variables", tagVariables);
		}
		
		return tagCompound;
	}
	
	@Override
	public boolean place(@Nonnull final IWorld worldInterface, @Nonnull final ChunkGenerator<? extends GenerationSettings> generator, @Nonnull final Random rand,
	                     @Nonnull final BlockPos blockPos, @Nonnull final NoFeatureConfig config) {
		return place((World) worldInterface, rand, blockPos);
	}
	
	abstract public boolean place(@Nonnull final World world, @Nonnull final Random rand, @Nonnull final BlockPos blockPos);
}
