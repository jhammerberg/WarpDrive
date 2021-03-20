package cr0s.warpdrive.generators;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.detection.BlockCloakingCoil;
import cr0s.warpdrive.block.detection.BlockCloakingCoil.EnumCoilType;
import cr0s.warpdrive.block.hull.BlockHullSlab;
import cr0s.warpdrive.block.hull.BlockHullSlab.EnumType;
import cr0s.warpdrive.block.movement.BlockTransporterBeacon;
import cr0s.warpdrive.data.BlockProperties;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.registries.ForgeRegistries;

public class MyBlockStateProvider extends BlockStateProvider {
	
	private static final HashSet<String> categories = new HashSet<>(Arrays.asList(
			"machine", "atomic", "breathing", "building", "collection", "decoration", "detection", "energy", "force_field", "hull", "movement", "weapon" ));
	
	/*
		// manual state & model
		"decoration.lamp_bubble"                active, facing      OBJ                 pathManualModelFacedActivated ?
		"decoration.lamp_flat"                  active, facing      OBJ                 pathManualModelFacedActivated ?
		"decoration.lamp_long"                  active, facing      OBJ                 pathManualModelFacedActivated ?
		"detection.environmental_sensor"        active, spinning    custom              too specific
		"detection.siren_industrial.<tier>"     active, spinning    custom              too specific
		"detection.siren_military.<tier>"       active, spinning    custom              too specific
		"force_field.projector.<tier>.full"     state               multipart           too specific
		"force_field.projector.<tier>.half"     state               multipart           too specific
		"force_field.relay.<tier>"              state               multipart           too specific
	 */
	
	private static final HashSet<String> pathManualModels = new HashSet<>(Arrays.asList(
			"-reserved1-",
			"-reserved2-" ));
	
	private static final HashSet<String> pathCubeAllModels = new HashSet<>(Arrays.asList(
			"atomic.void_shell.glass",
			"atomic.void_shell.plain",
			"breathing.air_flow",
			"breathing.air_source",
			"breathing.air_shield",
			"decoration.bedrock_glass",
			"decoration.decorative.glass",
			"decoration.decorative.grated",
			"decoration.decorative.plain",
			"decoration.decorative.stripes_black_down",
			"decoration.decorative.stripes_black_up",
			"decoration.decorative.stripes_yellow_down",
			"decoration.decorative.stripes_yellow_up",
			"decoration.gas.blue",
			"decoration.gas.dark",
			"decoration.gas.darkness",
			"decoration.gas.gray",
			"decoration.gas.green",
			"decoration.gas.milk",
			"decoration.gas.orange",
			"decoration.gas.red",
			"decoration.gas.siren",
			"decoration.gas.violet",
			"decoration.gas.white",
			"decoration.gas.yellow",
			"detection.warp_isolation",
			"force_field.block.<tier>.0",
			"force_field.block.<tier>.1",
			"force_field.block.<tier>.2",
			"force_field.block.<tier>.3",
			"force_field.block.<tier>.4",
			"force_field.block.<tier>.5",
			"force_field.block.<tier>.6",
			"force_field.block.<tier>.7",
			"force_field.block.<tier>.8",
			"force_field.block.<tier>.9",
			"force_field.block.<tier>.10",
			"force_field.block.<tier>.11",
			"force_field.block.<tier>.12",
			"force_field.block.<tier>.13",
			"force_field.block.<tier>.14",
			"force_field.block.<tier>.15",
			"hull.<tier>.glass.<color>",
			"hull.<tier>.omnipanel.<color>",
			"hull.<tier>.plain.<color>",
			"hull.<tier>.tiled.<color>",
			"hull.iridium_block",
			"machine.highly_advanced_machine",
			"weapon.laser",
			"weapon.laser_camera" ));
	
	private static final HashSet<String> pathTransporterSlabModels = new HashSet<>(Arrays.asList(
			"movement.transporter_containment",
			"movement.transporter_scanner" ));
	
	// following are detected by class type:
	//      "detection.cloaking_coil"
	//      "movement.transporter_beacon"
	//      "hull.<tier>.slab.plain.<color>"
	//      "hull.<tier>.slab.tiled.<color>"
	//      "hull.<tier>.stairs.plain.<color>"
	//      "hull.<tier>.stairs.tiled.<color>"
	
	private static final HashSet<String> pathCubeAllActivated = new HashSet<>(Arrays.asList(
			"atomic.accelerator_control_point",
			"atomic.accelerator_core",
			"atomic.chiller.<tier>",
			"atomic.particles_collider",
			"atomic.particles_injector",
			"detection.cloaking_core",
			"machine.chunk_loader.<tier>" ));
	
	private static final HashSet<String> pathManualModelFacingActivated = new HashSet<>(Collections.singletonList(
			"detection.biometric_scanner" ));
	
	private static final HashSet<String> pathCubeDirectionalFacingActivated = new HashSet<>(Arrays.asList(
			"breathing.air_generator.<tier>",
			"detection.camera",
			"detection.monitor",
			"detection.speaker.<tier>",
			"energy.ic2_reactor_laser_cooler" ));
	
	private static final HashSet<String> pathCubeHFacingActivated = new HashSet<>(Arrays.asList(
			"energy.enan_reactor_laser",
			"movement.ship_core.<tier>" ));
	
	private static final HashSet<String> pathCubeAllState = new HashSet<>(Collections.singletonList(
			"energy.capacitor.<tier>" ));
	
	private static final HashSet<String> pathCubeBottomTopState = new HashSet<>(Arrays.asList(
			"atomic.electromagnet.<tier>.glass",
			"atomic.electromagnet.<tier>.plain",
			"building.ship_scanner.<tier>",
			"collection.laser_tree_farm",
			"collection.mining_laser",
			"detection.radar",
			"detection.virtual_assistant.<tier>",
			"energy.enan_reactor_core.<tier>",
			"machine.laser_medium.<tier>",
			"machine.security_station",
			"movement.lift",
			"movement.ship_controller.<tier>",
			"movement.transporter_core",
			"weapon.weapon_controller" ));
	
	private final ExistingFileHelper existingFileHelper;
	
	public MyBlockStateProvider(final DataGenerator dataGenerator, final ExistingFileHelper existingFileHelper) {
		super(dataGenerator, WarpDrive.MODID, existingFileHelper);
		
		this.existingFileHelper = existingFileHelper;
	}
	
	@Override
	protected void registerStatesAndModels() {
		ForgeRegistries.BLOCKS.getValues()
		                      .stream()
		                      .filter(block -> WarpDrive.MODID.equals(Objects.requireNonNull(block.getRegistryName()).getNamespace()))
		                      .forEach(this::registerStatesAndModels);
	}
	
	protected void registerStatesAndModels(@Nonnull final Block block) {
		assert block.getRegistryName() != null;
		final String pathBlock = block.getRegistryName().getPath();
		final String[] pathNames = pathBlock.split("\\.");
		final String pathCategory = pathNames[0];
		final String pathModel = "block/" + (categories.contains(pathCategory) ? pathBlock.replaceFirst("\\.", "/") : pathBlock);
		
		// remove the tier to reduce the search list and deduplicate models
		final String pathBlockNoTier = pathBlock.replaceFirst("\\.(creative|basic|advanced|superior)", ".<tier>");
		final String pathModelNoTier = pathModel.replaceFirst("\\.(creative|basic|advanced|superior)", "").replaceFirst("/(creative|basic|advanced|superior)\\.", "/");
		
		// remove the color tag at the end to reduce the search list
		final String pathEnd  = pathNames[pathNames.length - 1];
		@SuppressWarnings("ConstantConditions") // null is a valid output since it's the fallback value
		final String pathBlockNoColor = DyeColor.byTranslationKey(pathEnd, null) == null ? pathBlock : pathBlock.replace("." + pathEnd, ".<color>");
		final String pathBlockNoTierNoColor = pathBlockNoColor.replaceFirst("\\.(creative|basic|advanced|superior)", ".<tier>");
		// note: we always need a different model per color
		
		if (pathManualModels.contains(pathBlock)) {
			final ModelFile model = models().getExistingFile(modLoc(pathModel));
			simpleBlock(block, model);
			simpleBlockItem(block, model);
			return;
		}
		
		// simple cubeAll
		if (pathCubeAllModels.contains(pathBlock)) {
			final ModelFile model = models().cubeAll(pathModel, modLoc(pathModel));
			simpleBlock(block, model);
			simpleBlockItem(block, model);
			return;
		}
		
		if (pathCubeAllModels.contains(pathBlockNoTierNoColor)) {
			final ModelFile model = models().cubeAll(pathModelNoTier, modLoc(pathModelNoTier));
			simpleBlock(block, model);
			simpleBlockItem(block, model);
			return;
		}
		
		// transporter slab 
		if (pathTransporterSlabModels.contains(pathBlockNoTierNoColor)) {
			registerTransporterSlab(block, pathModel, pathModelNoTier);
			return;
		}
		
		// transporter beacon
		if (block instanceof BlockTransporterBeacon) {
			registerTransporterBeacon(block, pathBlock);
			return;
		}
		
		// cloaking coil
		if (block instanceof BlockCloakingCoil) {
			registerCloakingCoil(block, pathModel, pathModelNoTier);
			return;
		}
		
		// advanced slab
		if (block instanceof BlockHullSlab) {
			registerOmniSlab(block, pathModel, pathModelNoTier);
			return;
		}
		
		// stairs
		if (block instanceof StairsBlock) {
			final String pathModelNoStairs       = pathModel.replace("stairs.", "");
			final String pathModelNoTierNoStairs = pathModelNoTier.replace("stairs.", "");
			final ResourceLocation textureSide   = findTexture(pathModelNoStairs, pathModelNoTierNoStairs, "-side"  , "");
			final ResourceLocation textureBottom = findTexture(pathModelNoStairs, pathModelNoTierNoStairs, "-bottom", "-horizontal", "");
			final ResourceLocation textureTop    = findTexture(pathModelNoStairs, pathModelNoTierNoStairs, "-top"   , "-vertical"  , "");
			final ModelFile modelStairs      = models().stairs     (pathModelNoTier + "-stairs", textureSide, textureBottom, textureTop);
			final ModelFile modelStairsInner = models().stairsInner(pathModelNoTier + "-inner" , textureSide, textureBottom, textureTop);
			final ModelFile modelStairsOuter = models().stairsOuter(pathModelNoTier + "-outer" , textureSide, textureBottom, textureTop);
			stairsBlock((StairsBlock) block, modelStairs, modelStairsInner, modelStairsOuter);
			simpleBlockItem(block, models().getBuilder(pathModelNoTier + "-stairs"));
			return;
		}
		
		// activated cubeAll
		if ( pathCubeAllActivated.contains(pathBlock)
		  || pathCubeAllActivated.contains(pathBlockNoTier) ) {
			final ResourceLocation textureActive   = modLoc(pathModel + "-active");
			final ResourceLocation textureInactive = modLoc(pathModel + "-inactive");
			final BlockModelBuilder modelActive = models().cubeAll(pathModel + "-active", textureActive);
			getVariantBuilder(block)
					.partialState().with(BlockProperties.ACTIVE, true ).setModels(new ConfiguredModel(modelActive))
					.partialState().with(BlockProperties.ACTIVE, false).setModels(new ConfiguredModel(models().cubeAll(pathModel + "-inactive", textureInactive)));
			simpleBlockItem(block, modelActive);
			return;
		}
		
		// facing activated manual
		if (pathManualModelFacingActivated.contains(pathBlock)) {
			final ResourceLocation textureActive   = modLoc(pathModel + "-face_active");
			final ResourceLocation textureInactive = modLoc(pathModel + "-face_inactive");
			for (final Boolean isActive : BlockProperties.ACTIVE.getAllowedValues()) {
				final ModelFile modelFile = models().withExistingParent(pathModel + (isActive ? "-active" : "-inactive"), modLoc(pathModel))
				                                    .texture("face", isActive ? textureActive : textureInactive);
				getVariantBuilder(block)
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.DOWN ).setModels(new ConfiguredModel(modelFile,  90,   0, false))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.UP   ).setModels(new ConfiguredModel(modelFile, -90,   0, false))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.NORTH).setModels(new ConfiguredModel(modelFile                 ))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.SOUTH).setModels(new ConfiguredModel(modelFile,   0, 180, false))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.WEST ).setModels(new ConfiguredModel(modelFile,   0, 270, false))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.EAST ).setModels(new ConfiguredModel(modelFile,   0,  90, false));
				if (isActive) {
					simpleBlockItem(block, modelFile);
				}
			}
			return;
		}
		
		// facing activated cube directional
		if (pathCubeDirectionalFacingActivated.contains(pathBlockNoTier)) {
			final ResourceLocation textureSide = findTexture(pathModel, pathModelNoTier, "-side");
			final ResourceLocation textureBack = findTexture(pathModel, pathModelNoTier, "-back", "-side");
			for (final Boolean isActive : BlockProperties.ACTIVE.getAllowedValues()) {
				final String suffixActive = isActive ? "active" : "inactive";
				final ResourceLocation textureFront = findTexture(pathModel, pathModelNoTier, "-front_" + suffixActive, "-front");
				final ModelFile modelFile = models().withExistingParent(pathModel + "-" + suffixActive, "block/cube_directional")
				        .texture("particle", textureFront)
				        .texture("down"    , textureSide)
				        .texture("up"      , textureSide)
				        .texture("north"   , textureFront)
				        .texture("south"   , textureBack)
				        .texture("west"    , textureSide)
				        .texture("east"    , textureSide);
				getVariantBuilder(block)
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.DOWN ).setModels(new ConfiguredModel(modelFile,  90,   0, false))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.UP   ).setModels(new ConfiguredModel(modelFile, -90,   0, false))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.NORTH).setModels(new ConfiguredModel(modelFile                 ))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.SOUTH).setModels(new ConfiguredModel(modelFile,   0, 180, false))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.WEST ).setModels(new ConfiguredModel(modelFile,   0, 270, false))
						.partialState().with(BlockProperties.ACTIVE, isActive).with(BlockProperties.FACING, Direction.EAST ).setModels(new ConfiguredModel(modelFile,   0,  90, false));
				if (isActive) {
					simpleBlockItem(block, modelFile);
				}
			}
			return;
		}
		
		// horizontal facing activated cube
		if (pathCubeHFacingActivated.contains(pathBlockNoTier)) {
			final ResourceLocation textureTop           = findTexture(pathModel, pathModelNoTier, "-top_bottom", "-top"   );
			final ResourceLocation textureBottom        = findTexture(pathModel, pathModelNoTier, "-top_bottom", "-bottom");
			final ResourceLocation textureLeftActive    = findTexture(pathModel, pathModelNoTier, "-left_active"   , "-left" , "-side_active"  , "-side");
			final ResourceLocation textureLeftInactive  = findTexture(pathModel, pathModelNoTier, "-left_inactive" , "-left" , "-side_inactive", "-side");
			final ResourceLocation textureRightActive   = findTexture(pathModel, pathModelNoTier, "-right_active"  , "-right", "-side_active"  , "-side");
			final ResourceLocation textureRightInactive = findTexture(pathModel, pathModelNoTier, "-right_inactive", "-right", "-side_inactive", "-side");
			final ResourceLocation textureFrontActive   = findTexture(pathModel, pathModelNoTier, "-front_active"  , "-front", "-side_active"  , "-side");
			final ResourceLocation textureFrontInactive = findTexture(pathModel, pathModelNoTier, "-front_inactive", "-front", "-side_inactive", "-side");
			final ResourceLocation textureBackActive    = findTexture(pathModel, pathModelNoTier, "-back_active"   , "-back" , "-side_active"  , "-side");
			final ResourceLocation textureBackInactive  = findTexture(pathModel, pathModelNoTier, "-back_inactive" , "-back" , "-side_inactive", "-side");
			final ModelFile modelFileActive   = models().cube(pathModel + "-active"  , textureBottom, textureTop,
			                                                  textureFrontActive  , textureBackActive  , textureRightActive  , textureLeftActive  )
			                                            .texture("particle", textureFrontActive);
			final ModelFile modelFileInactive = models().cube(pathModel + "-inactive", textureBottom, textureTop,
			                                                  textureFrontInactive, textureBackInactive, textureRightInactive, textureLeftInactive)
			                                            .texture("particle", textureFrontInactive);
			getVariantBuilder(block)
					.partialState().with(BlockProperties.ACTIVE, false)                                             .setModels(new ConfiguredModel(modelFileInactive))
					.partialState().with(BlockProperties.ACTIVE, true).with(BlockProperties.FACING_HORIZONTAL, Direction.NORTH).setModels(new ConfiguredModel(modelFileActive                 ))
					.partialState().with(BlockProperties.ACTIVE, true).with(BlockProperties.FACING_HORIZONTAL, Direction.SOUTH).setModels(new ConfiguredModel(modelFileActive,   0, 180, false))
					.partialState().with(BlockProperties.ACTIVE, true).with(BlockProperties.FACING_HORIZONTAL, Direction.WEST ).setModels(new ConfiguredModel(modelFileActive,   0, 270, false))
					.partialState().with(BlockProperties.ACTIVE, true).with(BlockProperties.FACING_HORIZONTAL, Direction.EAST ).setModels(new ConfiguredModel(modelFileActive,   0,  90, false));
			simpleBlockItem(block, modelFileActive);
			return;
		}
		
		// cube_all state
		if (pathCubeAllState.contains(pathBlockNoTier)) {
			final boolean isCapacitor = pathBlock.contains("capacitor");
			final IProperty<?>[] properties = block.getStateContainer().getProperties().toArray(new IProperty<?>[0]);
			for (final BlockState blockState : block.getStateContainer().getValidStates()) {
				final PartialBlockstate partialState = getVariantBuilder(block).partialState();
				partialState.getSetStates().putAll(blockState.getValues());
				final StringBuilder suffixBuilder = new StringBuilder();
				for (final IProperty<?> property : properties) {
					if (suffixBuilder.length() != 0) {
						suffixBuilder.append("_");
					}
					if (property instanceof BooleanProperty) {
						suffixBuilder.append((Boolean) blockState.get(property) ? "active" : "inactive");
					} else {
						suffixBuilder.append(blockState.get(property).toString().toLowerCase());
					}
				}
				final String suffix = suffixBuilder.toString();
				final ModelFile modelFile = models().cubeAll(pathModel + "-" + suffix, findTexture(pathModel, pathModelNoTier, "-" + suffix, ""));
				partialState.addModels(new ConfiguredModel(modelFile));
				if (!isCapacitor) {
					simpleBlockItem(block, modelFile);
				}
			}
			if (isCapacitor) {
				final ResourceLocation textureInput  = findTexture(pathModel, pathModelNoTier, "-input");
				final ResourceLocation textureOutput = findTexture(pathModel, pathModelNoTier, "-output");
				final ModelFile modelFile = models().cube(pathModel + "-inventory",
				                                          textureInput, textureInput,
				                                          textureOutput, textureOutput,
				                                          textureOutput, textureOutput )
				                                    .texture("particle", textureInput);
				simpleBlockItem(block, modelFile);
			}
			return;
		}
		
		// cube_bottom_top state
		if (pathCubeBottomTopState.contains(pathBlockNoTier)) {
			final IProperty<?>[] properties = block.getStateContainer().getProperties().toArray(new IProperty<?>[0]);
			for (final BlockState blockState : block.getStateContainer().getValidStates()) {
				final PartialBlockstate partialState = getVariantBuilder(block).partialState();
				partialState.getSetStates().putAll(blockState.getValues());
				final StringBuilder suffixBuilder = new StringBuilder();
				for (final IProperty<?> property : properties) {
					if (suffixBuilder.length() != 0) {
						suffixBuilder.append("_");
					}
					if (property instanceof BooleanProperty) {
						suffixBuilder.append((Boolean) blockState.get(property) ? "active" : "inactive");
					} else {
						suffixBuilder.append(blockState.get(property).toString().toLowerCase());
					}
				}
				final String suffix = suffixBuilder.toString();
				final ModelFile modelFile = models().cubeBottomTop(
						pathModel + "-" + suffix,
						findTexture(pathModel, pathModelNoTier, "-side_"   + suffix, "-side"                 ),
						findTexture(pathModel, pathModelNoTier, "-bottom_" + suffix, "-bottom", "-top_bottom"),
						findTexture(pathModel, pathModelNoTier, "-top_"    + suffix, "-top"   , "-top_bottom") );
				partialState.addModels(new ConfiguredModel(modelFile));
				simpleBlockItem(block, modelFile);
			}
			return;
		}
		
		WarpDrive.logger.info(String.format("Skipping unknown block %s",
		                                    block.getRegistryName() ));
	}
	
	private boolean textureExists(@Nonnull final ResourceLocation textureSimple) {
		return existingFileHelper.exists(textureSimple, ResourcePackType.CLIENT_RESOURCES, ".png", "textures");
	}
	
	/**
	 * Helper to retrieve texture path while trying to reduce duplicated ones
	 * when path is xxx/yyy.basic.blue with suffix1 -side_inactive, suffix2 -side
	 * then the first existing texture is returned in this order:
	 * - xxx/yyy.basic.blue-side_inactive
	 * - xxx/yyy.blue-side_inactive
	 * - xxx/yyy.basic.blue-side
	 * - xxx/yyy.blue-side
	 **/
	private ResourceLocation findTexture(@Nonnull final String pathModel, @Nonnull final String pathModelNoTier,
	                                     @Nonnull final String... subParts) {
		for (final String subPart : subParts) {
			final ResourceLocation texturePathTiered = modLoc(pathModel + subPart);
			if (textureExists(texturePathTiered)) {
				return texturePathTiered;
			}
			if (!pathModel.equals(pathModelNoTier)) {
				final ResourceLocation texturePathNoTier = modLoc(pathModelNoTier + subPart);
				if (textureExists(texturePathNoTier)) {
					return texturePathNoTier;
				}
			}
		}
		WarpDrive.logger.warn(String.format("No matching texture found for %s %s",
		                                    pathModelNoTier, String.join(",", subParts) ));
		return modLoc(pathModelNoTier + (subParts.length == 0 ? "" : subParts[0]));
	}
	
	private void registerTransporterSlab(@Nonnull final Block block, @Nonnull final String pathModel, @Nonnull final String pathModelNoTier) {
		if (block.getDefaultState().has(BlockProperties.ACTIVE)) {
			final ResourceLocation textureTopActive      = findTexture(pathModel, pathModelNoTier, "-top_active"     , "-top"   , "");
			final ResourceLocation textureTopInactive    = findTexture(pathModel, pathModelNoTier, "-top_inactive"   , "-top"   , "");
			final ResourceLocation textureBottomActive   = findTexture(pathModel, pathModelNoTier, "-bottom_active"  , "-bottom", "");
			final ResourceLocation textureBottomInactive = findTexture(pathModel, pathModelNoTier, "-bottom_inactive", "-bottom", "");
			final ResourceLocation textureSideActive     = findTexture(pathModel, pathModelNoTier, "-side_active"    , "-side"  , "");
			final ResourceLocation textureSideInactive   = findTexture(pathModel, pathModelNoTier, "-side_inactive"  , "-side"  , "");
			final ModelFile modelFileActive   = models().withExistingParent(pathModel + "-active", modLoc("block/transporter_slab"))
			                                            .texture("top"   , textureTopActive   )
			                                            .texture("bottom", textureBottomActive)
			                                            .texture("side"  , textureSideActive  );
			final ModelFile modelFileInactive = models().withExistingParent(pathModel + "-inactive", modLoc("block/transporter_slab"))
			                                            .texture("top"   , textureTopInactive   )
			                                            .texture("bottom", textureBottomInactive)
			                                            .texture("side"  , textureSideInactive  );
			getVariantBuilder(block)
					.partialState().with(BlockProperties.ACTIVE, true ).setModels(new ConfiguredModel(modelFileActive  ))
					.partialState().with(BlockProperties.ACTIVE, false).setModels(new ConfiguredModel(modelFileInactive));
			simpleBlockItem(block, modelFileActive);
		} else {
			final ResourceLocation textureSide   = findTexture(pathModel, pathModelNoTier, "-side"  , "");
			final ResourceLocation textureTop    = findTexture(pathModel, pathModelNoTier, "-top"   , "");
			final ResourceLocation textureBottom = findTexture(pathModel, pathModelNoTier, "-bottom", "");
			final ModelFile modelFile = models().withExistingParent(pathModel + "-active", modLoc("block/transporter_slab"))
			                                    .texture("top"   , textureTop   )
			                                    .texture("bottom", textureBottom)
			                                    .texture("side"  , textureSide  );
			getVariantBuilder(block)
					.partialState().setModels(new ConfiguredModel(modelFile));
			simpleBlockItem(block, modelFile);
		}
	}
	
	private void registerTransporterBeacon(@Nonnull final Block block, @Nonnull final String pathBlock) {
		final ModelFile modelFilePackedActive     = models().getExistingFile(modLoc("block/movement/transporter_beacon-packed_active"));
		final ModelFile modelFilePackedInactive   = models().getExistingFile(modLoc("block/movement/transporter_beacon-packed_inactive"));
		final ModelFile modelFileDeployedActive   = models().getExistingFile(modLoc("block/movement/transporter_beacon-deployed_active"));
		final ModelFile modelFileDeployedInactive = models().getExistingFile(modLoc("block/movement/transporter_beacon-deployed_inactive"));
		getVariantBuilder(block)
				.partialState().with(BlockTransporterBeacon.DEPLOYED, false).with(BlockProperties.ACTIVE, true ).setModels(new ConfiguredModel(modelFilePackedActive))
				.partialState().with(BlockTransporterBeacon.DEPLOYED, false).with(BlockProperties.ACTIVE, false).setModels(new ConfiguredModel(modelFilePackedInactive))
				.partialState().with(BlockTransporterBeacon.DEPLOYED, true ).with(BlockProperties.ACTIVE, true ).setModels(new ConfiguredModel(modelFileDeployedActive))
				.partialState().with(BlockTransporterBeacon.DEPLOYED, true ).with(BlockProperties.ACTIVE, false).setModels(new ConfiguredModel(modelFileDeployedInactive));
		itemModels().getBuilder(pathBlock)
		            .override().predicate(modLoc("active"), 0.0F).model(modelFilePackedInactive).end()
		            .override().predicate(modLoc("active"), 1.0F).model(modelFilePackedActive  ).end();
	}
	
	private void registerCloakingCoil(@Nonnull final Block block, @Nonnull final String pathModel, @Nonnull final String pathModelNoTier) {
		final ResourceLocation textureChannelingActive   = findTexture(pathModel, pathModelNoTier, "-channeling_active"  );
		final ResourceLocation textureChannelingInactive = findTexture(pathModel, pathModelNoTier, "-channeling_inactive");
		final ResourceLocation textureProjectingActive   = findTexture(pathModel, pathModelNoTier, "-projecting_active"  );
		final ResourceLocation textureProjectingInactive = findTexture(pathModel, pathModelNoTier, "-projecting_inactive");
		final ModelFile modelFileDisconnected = models().cubeAll(pathModel + "-disconnected", textureProjectingInactive);
		final ModelFile modelFileChannelingActive   = models().cubeAll(pathModel + "-channeling_active"  , textureChannelingActive  );
		final ModelFile modelFileChannelingInactive = models().cubeAll(pathModel + "-channeling_inactive", textureChannelingInactive);
		final ModelFile modelFileProjectingActive   = models().cube(pathModel + "-projecting_active",
		                                                            textureProjectingActive, textureProjectingActive,
		                                                            textureProjectingActive, textureChannelingActive,
		                                                            textureProjectingActive, textureProjectingActive )
		                                                      .texture("particle", textureProjectingActive);
		final ModelFile modelFileProjectingInactive = models().cube(pathModel + "-projecting_inactive",
		                                                            textureProjectingInactive, textureProjectingInactive,
		                                                            textureProjectingInactive, textureChannelingInactive,
		                                                            textureProjectingInactive, textureProjectingInactive )
		                                                      .texture("particle", textureProjectingInactive);
		getVariantBuilder(block)
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.DISCONNECTED)                                                                           .setModels(new ConfiguredModel(modelFileDisconnected                       ))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.INNER).with(BlockProperties.ACTIVE, false)                                              .setModels(new ConfiguredModel(modelFileChannelingInactive                 ))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.INNER).with(BlockProperties.ACTIVE, true )                                              .setModels(new ConfiguredModel(modelFileChannelingActive                   ))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, false).with(BlockProperties.FACING, Direction.DOWN ).setModels(new ConfiguredModel(modelFileProjectingInactive,  90,   0, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, false).with(BlockProperties.FACING, Direction.UP   ).setModels(new ConfiguredModel(modelFileProjectingInactive, -90,   0, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, false).with(BlockProperties.FACING, Direction.NORTH).setModels(new ConfiguredModel(modelFileProjectingInactive                 ))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, false).with(BlockProperties.FACING, Direction.SOUTH).setModels(new ConfiguredModel(modelFileProjectingInactive,   0, 180, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, false).with(BlockProperties.FACING, Direction.WEST ).setModels(new ConfiguredModel(modelFileProjectingInactive,   0, 270, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, false).with(BlockProperties.FACING, Direction.EAST ).setModels(new ConfiguredModel(modelFileProjectingInactive,   0,  90, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, true ).with(BlockProperties.FACING, Direction.DOWN ).setModels(new ConfiguredModel(modelFileProjectingActive  ,  90,   0, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, true ).with(BlockProperties.FACING, Direction.UP   ).setModels(new ConfiguredModel(modelFileProjectingActive  , -90,   0, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, true ).with(BlockProperties.FACING, Direction.NORTH).setModels(new ConfiguredModel(modelFileProjectingActive                   ))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, true ).with(BlockProperties.FACING, Direction.SOUTH).setModels(new ConfiguredModel(modelFileProjectingActive  ,   0, 180, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, true ).with(BlockProperties.FACING, Direction.WEST ).setModels(new ConfiguredModel(modelFileProjectingActive  ,   0, 270, false))
				.partialState().with(BlockCloakingCoil.COIL_TYPE, EnumCoilType.OUTER).with(BlockProperties.ACTIVE, true ).with(BlockProperties.FACING, Direction.EAST ).setModels(new ConfiguredModel(modelFileProjectingActive  ,   0,  90, false));
		simpleBlockItem(block, modelFileProjectingActive);
	}
	
	private void registerOmniSlab(@Nonnull final Block block, @Nonnull final String pathModel, @Nonnull final String pathModelNoTier) {
		final String pathModelNoSlab       = pathModel.replace("slab.", "");
		final String pathModelNoTierNoSlab = pathModelNoTier.replace("slab.", "");
		final ResourceLocation textureFull       = findTexture(pathModelNoSlab, pathModelNoTierNoSlab, "");
		final ResourceLocation textureHorizontal = findTexture(pathModelNoSlab, pathModelNoTierNoSlab, "-horizontal", "");
		final ResourceLocation textureVertical   = findTexture(pathModelNoSlab, pathModelNoTierNoSlab, "-vertical"  , "");
		getVariantBuilder(block)
				.partialState().with(BlockHullSlab.TYPE, EnumType.DOWN)
				.addModels(new ConfiguredModel(models().withExistingParent(pathModelNoTier + "-down", modLoc("block/slab_down"))
				                                       .texture("full"      , textureFull)
				                                       .texture("horizontal", textureHorizontal) ))
				.partialState().with(BlockHullSlab.TYPE, EnumType.UP)
				.addModels(new ConfiguredModel(models().withExistingParent(pathModelNoTier + "-up", modLoc("block/slab_up"))
				                                       .texture("full"      , textureFull)
				                                       .texture("horizontal", textureHorizontal) ))
				.partialState().with(BlockHullSlab.TYPE, EnumType.NORTH)
				.addModels(new ConfiguredModel(models().withExistingParent(pathModelNoTier + "-north", modLoc("block/slab_north"))
				                                       .texture("full"      , textureFull)
				                                       .texture("horizontal", textureHorizontal)
				                                       .texture("vertical"  , textureVertical) ))
				.partialState().with(BlockHullSlab.TYPE, EnumType.SOUTH)
				.addModels(new ConfiguredModel(models().withExistingParent(pathModelNoTier + "-south", modLoc("block/slab_south"))
				                                       .texture("full"      , textureFull)
				                                       .texture("horizontal", textureHorizontal)
				                                       .texture("vertical"  , textureVertical) ))
				.partialState().with(BlockHullSlab.TYPE, EnumType.EAST)
				.addModels(new ConfiguredModel(models().withExistingParent(pathModelNoTier + "-east", modLoc("block/slab_east"))
				                                       .texture("full"      , textureFull)
				                                       .texture("vertical"  , textureVertical) ))
				.partialState().with(BlockHullSlab.TYPE, EnumType.WEST)
				.addModels(new ConfiguredModel(models().withExistingParent(pathModelNoTier + "-west", modLoc("block/slab_west"))
				                                       .texture("full"      , textureFull)
				                                       .texture("vertical"  , textureVertical) ))
				.partialState().with(BlockHullSlab.TYPE, EnumType.FULL_X)
				.addModels(new ConfiguredModel(models().cube(pathModelNoTier + "-full_x",
				                                             textureVertical, textureVertical,
				                                             textureVertical, textureVertical,
				                                             textureFull, textureFull)
				                                       .texture("particle", textureFull) ))
				.partialState().with(BlockHullSlab.TYPE, EnumType.FULL_Y)
				.addModels(new ConfiguredModel(models().cube(pathModelNoTier + "-full_y",
				                                             textureFull, textureFull,
				                                             textureHorizontal, textureHorizontal,
				                                             textureHorizontal, textureHorizontal)
				                                       .texture("particle", textureFull) ))
				.partialState().with(BlockHullSlab.TYPE, EnumType.FULL_Z)
				.addModels(new ConfiguredModel(models().cube(pathModelNoTier + "-full_z",
				                                             textureHorizontal, textureHorizontal,
				                                             textureFull, textureFull,
				                                             textureVertical, textureVertical)
				                                       .texture("particle", textureFull) ));
		
		simpleBlockItem(block, models().withExistingParent(pathModelNoTier + "-down", modLoc("block/slab_down"))
		                                                   .texture("full"      , textureFull)
		                                                   .texture("horizontal", textureHorizontal) );
	}
}