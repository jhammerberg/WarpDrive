package cr0s.warpdrive.config.structures;

import cr0s.warpdrive.api.IXmlRepresentable;
import cr0s.warpdrive.config.InvalidXmlException;
import cr0s.warpdrive.config.XmlFileManager;

import javax.annotation.Nonnull;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

/**
 * @author Francesco, LemADEC
 *
 */
public abstract class AbstractStructure extends Feature<NoFeatureConfig> implements IXmlRepresentable {
	protected String group;
	protected String name;
	protected HashMap<String,String> variables = new HashMap<>();
	
	public AbstractStructure(final String group, final String name) {
		super(NoFeatureConfig::deserialize);
		
		this.group = group;
		this.name = name;
	}
	
	@Nonnull
	@Override
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return group + ":" + name;
	}
	
	
	abstract public AbstractStructureInstance instantiate(Random random);
	
	@Override
	public boolean loadFromXmlElement(final Element element) throws InvalidXmlException {
		
		final List<Element> listVariables = XmlFileManager.getChildrenElementByTagName(element, "variable");
		for (final Element elementVariable : listVariables) {
			final String variableName = elementVariable.getAttribute("name");
			final String variableExpression = elementVariable.getTextContent();
			variables.put(variableName, variableExpression);
		}
		
		return true;
	}
	
	@Override
	public boolean place(@Nonnull final IWorld worldInterface, @Nonnull final ChunkGenerator<? extends GenerationSettings> generator, @Nonnull final Random rand,
	                     @Nonnull final BlockPos blockPos, @Nonnull final NoFeatureConfig config) {
		return place((World) worldInterface, rand, blockPos);
	}
	
	abstract public boolean place(@Nonnull final World world, @Nonnull final Random rand, @Nonnull final BlockPos blockPos);
}
