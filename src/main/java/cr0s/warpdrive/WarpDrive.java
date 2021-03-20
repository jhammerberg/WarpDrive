package cr0s.warpdrive;

import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.Particle;
import cr0s.warpdrive.api.ParticleRegistry;
import cr0s.warpdrive.block.BlockChunkLoader;
import cr0s.warpdrive.block.BlockLaser;
import cr0s.warpdrive.block.BlockLaserMedium;
import cr0s.warpdrive.block.BlockSecurityStation;
import cr0s.warpdrive.block.TileEntityChunkLoader;
import cr0s.warpdrive.block.TileEntityLaser;
import cr0s.warpdrive.block.TileEntityLaserMedium;
import cr0s.warpdrive.block.TileEntitySecurityStation;
import cr0s.warpdrive.block.atomic.BlockAcceleratorControlPoint;
import cr0s.warpdrive.block.atomic.BlockAcceleratorCore;
import cr0s.warpdrive.block.atomic.BlockChiller;
import cr0s.warpdrive.block.atomic.BlockElectromagnetGlass;
import cr0s.warpdrive.block.atomic.BlockElectromagnetPlain;
import cr0s.warpdrive.block.atomic.BlockParticlesCollider;
import cr0s.warpdrive.block.atomic.BlockParticlesInjector;
import cr0s.warpdrive.block.atomic.BlockVoidShellGlass;
import cr0s.warpdrive.block.atomic.BlockVoidShellPlain;
import cr0s.warpdrive.block.atomic.TileEntityAcceleratorControlPoint;
import cr0s.warpdrive.block.atomic.TileEntityAcceleratorCore;
import cr0s.warpdrive.block.atomic.TileEntityParticlesInjector;
import cr0s.warpdrive.block.breathing.BlockAirFlow;
import cr0s.warpdrive.block.breathing.BlockAirGeneratorTiered;
import cr0s.warpdrive.block.breathing.BlockAirShield;
import cr0s.warpdrive.block.breathing.BlockAirSource;
import cr0s.warpdrive.block.breathing.TileEntityAirGeneratorTiered;
import cr0s.warpdrive.block.building.BlockShipScanner;
import cr0s.warpdrive.block.building.TileEntityShipScanner;
import cr0s.warpdrive.block.collection.BlockLaserTreeFarm;
import cr0s.warpdrive.block.collection.BlockMiningLaser;
import cr0s.warpdrive.block.collection.TileEntityLaserTreeFarm;
import cr0s.warpdrive.block.collection.TileEntityMiningLaser;
import cr0s.warpdrive.block.decoration.BlockBedrockGlass;
import cr0s.warpdrive.block.decoration.BlockDecorative;
import cr0s.warpdrive.block.decoration.BlockGas;
import cr0s.warpdrive.block.decoration.BlockLamp_bubble;
import cr0s.warpdrive.block.decoration.BlockLamp_flat;
import cr0s.warpdrive.block.decoration.BlockLamp_long;
import cr0s.warpdrive.block.detection.BlockBiometricScanner;
import cr0s.warpdrive.block.detection.BlockCamera;
import cr0s.warpdrive.block.detection.BlockCloakingCoil;
import cr0s.warpdrive.block.detection.BlockCloakingCore;
import cr0s.warpdrive.block.detection.BlockEnvironmentalSensor;
import cr0s.warpdrive.block.detection.BlockMonitor;
import cr0s.warpdrive.block.detection.BlockRadar;
import cr0s.warpdrive.block.detection.BlockSiren;
import cr0s.warpdrive.block.detection.BlockSpeaker;
import cr0s.warpdrive.block.detection.BlockVirtualAssistant;
import cr0s.warpdrive.block.detection.BlockWarpIsolation;
import cr0s.warpdrive.block.detection.TileEntityBiometricScanner;
import cr0s.warpdrive.block.detection.TileEntityCamera;
import cr0s.warpdrive.block.detection.TileEntityCloakingCore;
import cr0s.warpdrive.block.detection.TileEntityEnvironmentalSensor;
import cr0s.warpdrive.block.detection.TileEntityMonitor;
import cr0s.warpdrive.block.detection.TileEntityRadar;
import cr0s.warpdrive.block.detection.TileEntitySiren;
import cr0s.warpdrive.block.detection.TileEntitySpeaker;
import cr0s.warpdrive.block.detection.TileEntityVirtualAssistant;
import cr0s.warpdrive.block.energy.BlockCapacitor;
import cr0s.warpdrive.block.energy.BlockEnanReactorCore;
import cr0s.warpdrive.block.energy.BlockEnanReactorLaser;
import cr0s.warpdrive.block.energy.BlockIC2reactorLaserCooler;
import cr0s.warpdrive.block.energy.TileEntityCapacitor;
import cr0s.warpdrive.block.energy.TileEntityEnanReactorCore;
import cr0s.warpdrive.block.energy.TileEntityEnanReactorLaser;
import cr0s.warpdrive.block.energy.TileEntityIC2reactorLaserMonitor;
import cr0s.warpdrive.block.forcefield.BlockForceField;
import cr0s.warpdrive.block.forcefield.BlockForceFieldProjector;
import cr0s.warpdrive.block.forcefield.BlockForceFieldRelay;
import cr0s.warpdrive.block.forcefield.TileEntityForceField;
import cr0s.warpdrive.block.forcefield.TileEntityForceFieldProjector;
import cr0s.warpdrive.block.forcefield.TileEntityForceFieldRelay;
import cr0s.warpdrive.block.hull.BlockHullGlass;
import cr0s.warpdrive.block.hull.BlockHullOmnipanel;
import cr0s.warpdrive.block.hull.BlockHullPlain;
import cr0s.warpdrive.block.hull.BlockHullSlab;
import cr0s.warpdrive.block.hull.BlockHullStairs;
import cr0s.warpdrive.block.movement.BlockLift;
import cr0s.warpdrive.block.movement.BlockShipController;
import cr0s.warpdrive.block.movement.BlockShipCore;
import cr0s.warpdrive.block.movement.BlockTransporterBeacon;
import cr0s.warpdrive.block.movement.BlockTransporterContainment;
import cr0s.warpdrive.block.movement.BlockTransporterCore;
import cr0s.warpdrive.block.movement.BlockTransporterScanner;
import cr0s.warpdrive.block.movement.TileEntityJumpGateCore;
import cr0s.warpdrive.block.movement.TileEntityLift;
import cr0s.warpdrive.block.movement.TileEntityShipController;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.block.movement.TileEntityTransporterBeacon;
import cr0s.warpdrive.block.movement.TileEntityTransporterCore;
import cr0s.warpdrive.block.passive.BlockHighlyAdvancedMachine;
import cr0s.warpdrive.block.passive.BlockIridium;
import cr0s.warpdrive.block.weapon.BlockLaserCamera;
import cr0s.warpdrive.block.weapon.BlockWeaponController;
import cr0s.warpdrive.block.weapon.TileEntityLaserCamera;
import cr0s.warpdrive.block.weapon.TileEntityWeaponController;
import cr0s.warpdrive.client.ClientProxy;
import cr0s.warpdrive.client.ItemGroupHull;
import cr0s.warpdrive.client.ItemGroupMain;
import cr0s.warpdrive.config.Recipes;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.damage.DamageAsphyxia;
import cr0s.warpdrive.damage.DamageCold;
import cr0s.warpdrive.damage.DamageIrradiation;
import cr0s.warpdrive.damage.DamageLaser;
import cr0s.warpdrive.damage.DamageShock;
import cr0s.warpdrive.damage.DamageTeleportation;
import cr0s.warpdrive.damage.DamageWarm;
import cr0s.warpdrive.data.CamerasRegistry;
import cr0s.warpdrive.data.CelestialObjectManager;
import cr0s.warpdrive.data.CloakManager;
import cr0s.warpdrive.data.EnumAirTankTier;
import cr0s.warpdrive.data.EnumComponentType;
import cr0s.warpdrive.data.EnumDecorativeType;
import cr0s.warpdrive.data.EnumForceFieldShape;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;
import cr0s.warpdrive.data.EnumGasColor;
import cr0s.warpdrive.data.EnumHullPlainType;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.WarpDriveArmorMaterial;
import cr0s.warpdrive.entity.EntityLaserExploder;
import cr0s.warpdrive.entity.EntityNPC;
import cr0s.warpdrive.entity.EntityOfflineAvatar;
import cr0s.warpdrive.entity.EntityParticleBunch;
import cr0s.warpdrive.entity.EntitySeat;
import cr0s.warpdrive.event.ChatHandler;
import cr0s.warpdrive.event.ChunkHandler;
import cr0s.warpdrive.event.CommonWorldGenerator;
import cr0s.warpdrive.event.WorldHandler;
import cr0s.warpdrive.item.ItemAirTank;
import cr0s.warpdrive.item.ItemComponent;
import cr0s.warpdrive.item.ItemElectromagneticCell;
import cr0s.warpdrive.item.ItemForceFieldShape;
import cr0s.warpdrive.item.ItemForceFieldUpgrade;
import cr0s.warpdrive.item.ItemIC2reactorLaserFocus;
import cr0s.warpdrive.item.ItemPlasmaTorch;
import cr0s.warpdrive.item.ItemShipToken;
import cr0s.warpdrive.item.ItemTuningDriver;
import cr0s.warpdrive.item.ItemTuningFork;
import cr0s.warpdrive.item.ItemWarpArmor;
import cr0s.warpdrive.item.ItemWrench;
import cr0s.warpdrive.network.PacketHandler;
import cr0s.warpdrive.render.EntityCamera;
import cr0s.warpdrive.world.EntitySphereGen;
import cr0s.warpdrive.world.EntityStarCore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ObjectHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@EventBusSubscriber(bus = Bus.MOD)
@Mod(value = WarpDrive.MODID)
public class WarpDrive {
	public static final String MODID = "warpdrive";
	public static final String MOD_VERSION = "@version@";
	public static final Integer[] MOD_VERSION_NUMBERS;
	public static final String PROTOCOL_VERSION = "@version@";
	@SuppressWarnings("ConstantConditions")
	public static final boolean isDev = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0
	                                 || MOD_VERSION.contains("-dev");
	public static GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes("[WarpDrive]".getBytes()), "[WarpDrive]");
	
	// common blocks and items
	public static Block blockLaser;
	public static Block[] blockChunkLoaders;
	public static Block[] blockLaserMediums;
	public static ItemComponent[] itemComponents;
	
	// atomic blocks and items
	public static Block blockAcceleratorCore;
	public static Block blockAcceleratorControlPoint;
	public static Block blockParticlesCollider;
	public static Block blockParticlesInjector;
	public static Block blockVoidShellPlain;
	public static Block blockVoidShellGlass;
	public static Block[] blockElectromagnets_plain;
	public static Block[] blockElectromagnets_glass;
	public static Block[] blockChillers;
	public static ItemElectromagneticCell[][] itemElectromagneticCell;
	public static ItemPlasmaTorch[] itemPlasmaTorch;
	
	// building blocks and items
	public static Block[] blockShipScanners;
	public static ItemShipToken[] itemShipTokens;
	
	// breathing
	public static Block blockAirFlow;
	public static Block blockAirSource;
	public static Block[] blockAirShields;
	public static Block[] blockAirGeneratorTiered;
	
	// collection blocks
	public static Block blockMiningLaser;
	public static Block blockLaserTreeFarm;
	
	// decoration
	public static Block blockBedrockGlass;
	public static Block[] blockDecoratives;
	public static Block[] blockGas;
	public static Block blockLamp_bubble;
	public static Block blockLamp_flat;
	public static Block blockLamp_long;
	
	// detection blocks
	public static Block blockBiometricScanner;
	public static Block blockCamera;
	public static Block blockCloakingCoil;
	public static Block blockCloakingCore;
	public static Block blockEnvironmentalSensor;
	public static Block blockMonitor;
	public static Block blockRadar;
	public static Block[] blockSirenIndustrials;
	public static Block[] blockSirenMilitaries;
	public static Block[] blockSpeakers;
	public static Block[] blockVirtualAssistants;
	public static Block blockWarpIsolation;
	
	// energy blocks and items
	public static Block[] blockCapacitors;
	public static Block[] blockEnanReactorCores;
	public static Block blockEnanReactorLaser;
	public static Block blockIC2reactorLaserCooler;
	public static Item itemIC2reactorLaserFocus;
	
	// force field blocks and items
	public static Block[][] blockForceFields;
	public static Block[] blockForceFieldHalfProjectors;
	public static Block[] blockForceFieldFullProjectors;
	public static Block[] blockForceFieldRelays;
	public static Block blockSecurityStation;
	public static ItemForceFieldShape[] itemForceFieldShapes;
	public static ItemForceFieldUpgrade[] itemForceFieldUpgrades;
	
	// hull blocks
	public static Block[][][] blockHulls_plain;
	public static Block[][] blockHulls_glass;
	public static Block[][] blockHulls_omnipanel;
	public static Block[][] blockHulls_stairs;
	public static Block[][] blockHulls_slab;
	
	// movement blocks
	public static Block blockLift;
	public static Block[] blockShipCores;
	public static Block[] blockShipControllers;
	public static Block blockTransporterBeacon;
	public static Block blockTransporterCore;
	public static Block blockTransporterContainment;
	public static Block blockTransporterScanner;
	
	// passive blocks
	public static Block blockHighlyAdvancedMachine;
	public static Block blockIridium;
	
	// weapon blocks
	public static Block blockLaserCamera;
	public static Block blockWeaponController;
	
	public static final IArmorMaterial[] armorMaterials = new ArmorMaterial[EnumTier.length];
	
	// equipment items
	public static ItemAirTank[] itemAirTanks;
	public static ItemTuningFork[] itemTuningForks;
	public static ItemTuningDriver itemTuningDriver;
	public static ItemWrench itemWrench;
	public static ArmorItem[][] itemWarpArmor;
	
	// damage sources
	public static DamageAsphyxia damageAsphyxia;
	public static DamageCold damageCold;
	public static DamageIrradiation damageIrradiation;
	public static DamageLaser damageLaser;
	public static DamageShock damageShock;
	public static DamageTeleportation damageTeleportation;
	public static DamageWarm damageWarm;
	
	// world generation
	public static Biome biomeSpace;
	@SuppressWarnings("FieldCanBeLocal")
	private CommonWorldGenerator commonWorldGenerator;
	
	public static Method methodBlock_getSilkTouch = null;
	
	// Client settings
	public static final ItemGroup itemGroupMain = new ItemGroupMain(MODID.toLowerCase() + ".main");
	public static final ItemGroup itemGroupHull = new ItemGroupHull(MODID.toLowerCase() + ".hull");
	
	@ObjectHolder(WarpDrive.MODID)
	public static WarpDrive instance;
	public static CommonProxy proxy = DistExecutor.safeRunForDist(()-> ClientProxy::new, ()->CommonProxy::new);
	
	public static CloakManager cloaks;
	public static CamerasRegistry cameras;
	
	@SuppressWarnings("FieldCanBeLocal")
	private static WarpDrivePeripheralHandler peripheralHandler = null;
	
	public static Logger logger;
	public static LoggerPrintStream printStreamError;
	public static LoggerPrintStream printStreamWarn;
	public static LoggerPrintStream printStreamInfo;
	
	static {
		// pre-calculate and sanitize the version numbers
		String[] strings = WarpDrive.MOD_VERSION.split("-");
		if (strings.length < 2) {
			strings = "0.0.0-0.0.0".split("-");
		}
		if (WarpDrive.isDev && strings[strings.length - 1].contains("dev")) {
			strings = strings[strings.length - 2].split("\\.");
		} else {
			strings = strings[strings.length - 1].split("\\.");
		}
		final ArrayList<Integer> integers = new ArrayList<>(strings.length);
		for (final String string : strings) {
			try {
				integers.add(Integer.parseInt(string));
			} catch (final NumberFormatException exception) {
				// ignore
			}
		}
		while (integers.size() < 3) {
			integers.add(0);
		}
		MOD_VERSION_NUMBERS = integers.toArray(new Integer[0]);
	}
	
	public WarpDrive() {
		logger = LogManager.getLogger(MODID);
		printStreamError = new LoggerPrintStream(Level.ERROR);
		printStreamWarn = new LoggerPrintStream(Level.WARN);
		printStreamInfo = new LoggerPrintStream(Level.INFO);
		
		final String configPath = FMLPaths.CONFIGDIR.get().resolve(FMLConfig.defaultConfigPath()).toAbsolutePath().toString();
		WarpDriveConfig.onFMLpreInitialization(configPath);
		
		// TODO MC1.15 Silktouch laser mining
		// methodBlock_getSilkTouch = ReflectionHelper.findMethod(Block.class, "getSilkTouchDrop", "func_180643_i", BlockState.class);
		
		// common blocks and items
		blockLaser = new BlockLaser("laser", EnumTier.BASIC);
		
		blockChunkLoaders = new Block[EnumTier.length];
		for (final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			blockChunkLoaders[indexTier] = new BlockChunkLoader("chunk_loader." + enumTier.getName(), enumTier);
		}
		
		blockLaserMediums = new Block[EnumTier.length];
		for (final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			blockLaserMediums[indexTier] = new BlockLaserMedium("laser_medium." + enumTier.getName(), enumTier);
		}
		
		itemComponents = new ItemComponent[EnumComponentType.length];
		for (final EnumComponentType enumComponentType : EnumComponentType.values()) {
			final int indexType = enumComponentType.ordinal();
			itemComponents[indexType] = new ItemComponent("component." + enumComponentType.getName(), EnumTier.BASIC, enumComponentType);
		}
		
		// 20% more durability, same enchantability (except basic is slightly lower), increased toughness
		armorMaterials[EnumTier.BASIC.getIndex()   ] = WarpDriveArmorMaterial.RUBBER;
		armorMaterials[EnumTier.ADVANCED.getIndex()] = WarpDriveArmorMaterial.CERAMIC;
		armorMaterials[EnumTier.SUPERIOR.getIndex()] = WarpDriveArmorMaterial.CARBON_FIBER;
		
		// atomic blocks and items
		blockAcceleratorCore = new BlockAcceleratorCore("atomic.accelerator_core", EnumTier.BASIC);
		blockAcceleratorControlPoint = new BlockAcceleratorControlPoint("atomic.accelerator_control_point", EnumTier.BASIC, false);
		blockParticlesCollider = new BlockParticlesCollider("atomic.particles_collider", EnumTier.BASIC);
		blockParticlesInjector = new BlockParticlesInjector("atomic.particles_injector", EnumTier.BASIC);
		blockVoidShellPlain = new BlockVoidShellPlain("atomic.void_shell.plain", EnumTier.BASIC, Material.ROCK);
		blockVoidShellGlass = new BlockVoidShellGlass("atomic.void_shell.glass", EnumTier.BASIC);
		
		blockElectromagnets_plain = new Block[EnumTier.length];
		blockElectromagnets_glass = new Block[EnumTier.length];
		blockChillers = new Block[EnumTier.length];
		itemElectromagneticCell = new ItemElectromagneticCell[EnumTier.length][ParticleRegistry.getRegisteredParticles().size() + 1];
		itemPlasmaTorch = new ItemPlasmaTorch[EnumTier.length];
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			blockElectromagnets_plain[indexTier] = new BlockElectromagnetPlain("atomic.electromagnet." + enumTier.getName() + ".plain", enumTier, Material.IRON);
			blockElectromagnets_glass[indexTier] = new BlockElectromagnetGlass("atomic.electromagnet." + enumTier.getName() + ".glass", enumTier);
			blockChillers[indexTier] = new BlockChiller("atomic.chiller." + enumTier.getName(), enumTier);
			
			int indexParticle = 1;
			itemElectromagneticCell[indexTier][0] = new ItemElectromagneticCell("atomic.electromagnetic_cell." + enumTier.getName(), enumTier, null);
			for(final Particle particle : ParticleRegistry.getRegisteredParticles().values()) {
				indexParticle++;
				itemElectromagneticCell[indexTier][indexParticle] = new ItemElectromagneticCell("atomic.electromagnetic_cell." + enumTier.getName() + "." + particle.getRegistryName(), enumTier, particle);
				// itemPlasmaTorch[indexTier] = new ItemPlasmaTorch("plasma_torch." + enumTier.getName(), enumTier);
			}
		}
		
		// building blocks and items
		blockShipScanners = new Block[EnumTier.length];
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			blockShipScanners[indexTier] = new BlockShipScanner("building.ship_scanner." + enumTier.getName(), enumTier);
		}
		itemShipTokens = new ItemShipToken[ItemShipToken.TOKEN_IDs.length];
		for(final int tokenId : ItemShipToken.TOKEN_IDs) {
			itemShipTokens[tokenId] = new ItemShipToken("building.ship_token" + tokenId, EnumTier.BASIC, tokenId);
		}
		
		// breathing blocks and items
		blockAirFlow = new BlockAirFlow("breathing.air_flow", EnumTier.BASIC);
		blockAirSource = new BlockAirSource("breathing.air_source", EnumTier.BASIC);
		for (final DyeColor enumDyeColor : DyeColor.values()) {
			final int indexColor = enumDyeColor.getId();
			blockAirShields[indexColor] = new BlockAirShield("breathing.air_shield", EnumTier.BASIC, enumDyeColor);
		}
		
		blockAirGeneratorTiered = new Block[EnumTier.length];
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			blockAirGeneratorTiered[indexTier] = new BlockAirGeneratorTiered("breathing.air_generator." + enumTier.getName(), enumTier);
		}
		
		itemAirTanks = new ItemAirTank[4];
		for (final EnumAirTankTier enumAirTankTier : EnumAirTankTier.values()) {
			itemAirTanks[enumAirTankTier.getIndex()] = new ItemAirTank("breathing.air_tank." + enumAirTankTier.getName(), enumAirTankTier);
		}
		
		// collection blocks
		blockMiningLaser = new BlockMiningLaser("collection.mining_laser", EnumTier.BASIC);
		blockLaserTreeFarm = new BlockLaserTreeFarm("collection.laser_tree_farm", EnumTier.BASIC);
		
		// decoration
		blockBedrockGlass = new BlockBedrockGlass("decoration.bedrock_glass", EnumTier.CREATIVE);
		
		blockDecoratives = new Block[EnumDecorativeType.values().length];
		for (final EnumDecorativeType enumDecorativeType : EnumDecorativeType.values()) {
			final int indexType = enumDecorativeType.ordinal();
			blockDecoratives[indexType] = new BlockDecorative("decorative." + enumDecorativeType.getName(), EnumTier.BASIC);
		}
		
		blockGas = new Block[EnumGasColor.length];
		for (final EnumGasColor enumGasColor : EnumGasColor.values()) {
			final int indexColor = enumGasColor.ordinal();
			blockGas[indexColor] = new BlockGas("decoration.gas." + enumGasColor.getName(), EnumTier.BASIC);
		}
		
		blockLamp_bubble = new BlockLamp_bubble("decoration.lamp_bubble", EnumTier.BASIC);
		blockLamp_flat = new BlockLamp_flat("decoration.lamp_flat", EnumTier.BASIC);
		blockLamp_long = new BlockLamp_long("decoration.lamp_long", EnumTier.BASIC);
		
		// detection blocks
		blockBiometricScanner = new BlockBiometricScanner("detection.biometric_scanner", EnumTier.BASIC);
		blockCamera = new BlockCamera("detection.camera", EnumTier.BASIC);
		blockCloakingCoil = new BlockCloakingCoil("detection.cloaking_coil", EnumTier.BASIC);
		blockCloakingCore = new BlockCloakingCore("detection.cloaking_core", EnumTier.BASIC);
		blockEnvironmentalSensor = new BlockEnvironmentalSensor("detection.environmental_sensor", EnumTier.BASIC);
		blockMonitor = new BlockMonitor("detection.monitor", EnumTier.BASIC);
		blockRadar = new BlockRadar("detection.radar", EnumTier.BASIC);
		
		blockSirenIndustrials = new Block[EnumTier.length];
		blockSirenMilitaries = new Block[EnumTier.length];
		blockSpeakers = new Block[EnumTier.length];
		blockVirtualAssistants = new Block[EnumTier.length];
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			blockSirenIndustrials[indexTier] = new BlockSiren("detection.siren_industrial." + enumTier.getName(), enumTier, true);
			blockSirenMilitaries[indexTier] = new BlockSiren("detection.siren_military." + enumTier.getName(), enumTier, false);
			blockSpeakers[indexTier] = new BlockSpeaker("detection.speaker." + enumTier.getName(), enumTier);
			blockVirtualAssistants[indexTier] = new BlockVirtualAssistant("detection.virtual_assistant." + enumTier.getName(), enumTier);
		}
		blockWarpIsolation = new BlockWarpIsolation("detection.warp_isolation", EnumTier.BASIC);
		
		// energy blocks and items
		blockCapacitors = new Block[EnumTier.length];
		for(final EnumTier enumTier : EnumTier.values()) {
			final int indexTier = enumTier.getIndex();
			blockCapacitors[indexTier] = new BlockCapacitor("energy.capacitor." + enumTier.getName(), enumTier);
		}
		blockEnanReactorCores = new Block[EnumTier.length];
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			blockEnanReactorCores[indexTier] = new BlockEnanReactorCore("energy.enan_reactor_core." + enumTier.getName(), enumTier);
		}
		blockEnanReactorLaser = new BlockEnanReactorLaser("energy.enan_reactor_laser", EnumTier.BASIC);
		
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			blockIC2reactorLaserCooler = new BlockIC2reactorLaserCooler("energy.ic2_reactor_laser_cooler", EnumTier.BASIC);
			itemIC2reactorLaserFocus = new ItemIC2reactorLaserFocus("energy.ic2_reactor_laser_focus", EnumTier.BASIC);
		}
		
		// force field blocks and items
		blockForceFields = new Block[EnumTier.length][16];
		blockForceFieldHalfProjectors = new Block[EnumTier.length];
		blockForceFieldFullProjectors = new Block[EnumTier.length];
		blockForceFieldRelays = new Block[EnumTier.length];
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			for (final DyeColor enumDyeColor : DyeColor.values()) {
				final int indexColor = enumDyeColor.getId();
				blockForceFields[indexTier][indexColor] = new BlockForceField("force_field.block." + enumTier.getName() + "." + enumDyeColor.getName(), enumTier, enumDyeColor);
			}
			blockForceFieldHalfProjectors[indexTier] = new BlockForceFieldProjector("force_field.projector." + enumTier.getName(), enumTier, false);
			blockForceFieldFullProjectors[indexTier] = new BlockForceFieldProjector("force_field.projector." + enumTier.getName(), enumTier, true);
			blockForceFieldRelays[indexTier] = new BlockForceFieldRelay("force_field.relay." + enumTier.getName(), enumTier);
		}
		blockSecurityStation = new BlockSecurityStation("machines.security_station", EnumTier.BASIC);
		
		itemForceFieldShapes = new ItemForceFieldShape[EnumForceFieldShape.length];
		for (final EnumForceFieldShape forceFieldShape : EnumForceFieldShape.values()) {
			final int indexShape = forceFieldShape.ordinal();
			itemForceFieldShapes[indexShape] = new ItemForceFieldShape("force_field.shape." + forceFieldShape.getName(), EnumTier.BASIC, forceFieldShape);
		}
		
		itemForceFieldUpgrades = new ItemForceFieldUpgrade[EnumForceFieldShape.length];
		for (final EnumForceFieldUpgrade forceFieldUpgrade : EnumForceFieldUpgrade.values()) {
			final int indexShape = forceFieldUpgrade.ordinal();
			itemForceFieldUpgrades[indexShape] = new ItemForceFieldUpgrade("force_field.upgrade." + forceFieldUpgrade.getName(), EnumTier.BASIC, forceFieldUpgrade);
		}
		
		// hull blocks
		blockHulls_plain = new Block[EnumTier.length][EnumHullPlainType.length][16];
		blockHulls_glass = new Block[EnumTier.length][16];
		blockHulls_omnipanel = new Block[EnumTier.length][16];
		blockHulls_stairs = new Block[EnumTier.length][16];
		blockHulls_slab = new Block[EnumTier.length][16];
		
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			for (final DyeColor enumDyeColor : DyeColor.values()) {
				final int indexColor = enumDyeColor.getId();
				for (final EnumHullPlainType hullPlainType : EnumHullPlainType.values()) {
					blockHulls_plain[indexTier][hullPlainType.ordinal()][indexColor] = new BlockHullPlain("hull." + enumTier.getName() + "." + hullPlainType.getName() + "." +  enumDyeColor.getName(), enumTier, enumDyeColor, hullPlainType);
				}
				blockHulls_glass[indexTier][indexColor] = new BlockHullGlass("hull." + enumTier.getName() + ".glass." + enumDyeColor.getName(), enumTier, enumDyeColor.getMapColor());
				blockHulls_omnipanel[indexTier][indexColor] = new BlockHullOmnipanel("hull." + enumTier.getName() + ".omnipanel." + enumDyeColor.getName(), enumTier, enumDyeColor);
				blockHulls_stairs[indexTier][indexColor] = new BlockHullStairs("hull." + enumTier.getName() + ".stairs." + enumDyeColor.getName(), enumTier, enumDyeColor,
				                                                           blockHulls_plain[indexTier][indexColor][0].getDefaultState());
				blockHulls_slab[indexTier][indexColor] = new BlockHullSlab("hull." + enumTier.getName() + ".slab." + enumDyeColor.getName(), enumTier, enumDyeColor,
				                                                       blockHulls_plain[indexTier][indexColor][0].getDefaultState());
			}
		}
		
		// movement blocks
		blockLift = new BlockLift("movement.lift", EnumTier.BASIC);
		
		blockShipControllers = new Block[EnumTier.length];
		blockShipCores = new Block[EnumTier.length];
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			blockShipControllers[indexTier] = new BlockShipController("movement.ship_controller." + enumTier.getName(), enumTier);
			blockShipCores[indexTier] = new BlockShipCore("movement.ship_core." + enumTier.getName(), enumTier);
		}
		
		blockTransporterBeacon = new BlockTransporterBeacon("movement.transporter_beacon", EnumTier.BASIC);
		blockTransporterContainment = new BlockTransporterContainment("movement.transporter_containment", EnumTier.BASIC);
		blockTransporterCore = new BlockTransporterCore("movement.transporter_core", EnumTier.BASIC);
		blockTransporterScanner = new BlockTransporterScanner("movement.transporter_scanner", EnumTier.BASIC);
		
		// passive blocks
		blockHighlyAdvancedMachine = new BlockHighlyAdvancedMachine("passive.highly_advanced_machine", EnumTier.BASIC);
		blockIridium = new BlockIridium("passive.iridium_block", EnumTier.BASIC);
		
		// weapon blocks
		blockLaserCamera = new BlockLaserCamera("weapon.laser_camera", EnumTier.BASIC);
		blockWeaponController = new BlockWeaponController("weapon.weapon_controller", EnumTier.BASIC);
		
		// equipment items
		itemTuningForks = new ItemTuningFork[16];
		for (final DyeColor enumDyeColor : DyeColor.values()) {
			final int indexColor = enumDyeColor.getId();
			itemTuningForks[indexColor] = new ItemTuningFork("tool.tuning_fork." + enumDyeColor.getName(), EnumTier.BASIC);
		}
		itemTuningDriver = new ItemTuningDriver("tool.tuning_driver", EnumTier.ADVANCED);
		itemWrench = new ItemWrench("tool.wrench", EnumTier.BASIC);
		
		itemWarpArmor = new ArmorItem[EnumTier.length][4];
		for(final EnumTier enumTier : EnumTier.nonCreative()) {
			final int indexTier = enumTier.getIndex();
			itemWarpArmor[indexTier][EquipmentSlotType.HEAD.getIndex() ] = new ItemWarpArmor("warp_armor." + enumTier.getName() + "." + ItemWarpArmor.suffixes[EquipmentSlotType.HEAD.getIndex() ], enumTier, armorMaterials[indexTier], EquipmentSlotType.HEAD );
			itemWarpArmor[indexTier][EquipmentSlotType.CHEST.getIndex()] = new ItemWarpArmor("warp_armor." + enumTier.getName() + "." + ItemWarpArmor.suffixes[EquipmentSlotType.CHEST.getIndex()], enumTier, armorMaterials[indexTier], EquipmentSlotType.CHEST);
			itemWarpArmor[indexTier][EquipmentSlotType.LEGS.getIndex() ] = new ItemWarpArmor("warp_armor." + enumTier.getName() + "." + ItemWarpArmor.suffixes[EquipmentSlotType.LEGS.getIndex() ], enumTier, armorMaterials[indexTier], EquipmentSlotType.LEGS );
			itemWarpArmor[indexTier][EquipmentSlotType.FEET.getIndex() ] = new ItemWarpArmor("warp_armor." + enumTier.getName() + "." + ItemWarpArmor.suffixes[EquipmentSlotType.FEET.getIndex() ], enumTier, armorMaterials[indexTier], EquipmentSlotType.FEET );
		}
		
		// damage sources
		damageAsphyxia = new DamageAsphyxia();
		damageCold = new DamageCold();
		damageIrradiation = new DamageIrradiation();
		damageLaser = new DamageLaser();
		damageShock = new DamageShock();
		damageTeleportation = new DamageTeleportation();
		damageWarm = new DamageWarm();
		
		// entities
		// (done in the event handler)
		
		// world generation
		/* TODO MC1.15 World geenration
		final Biome.BiomeProperties biomeProperties = new Biome.BiomeProperties("space").setRainDisabled().setWaterColor(0);
		biomeSpace = new BiomeSpace(biomeProperties);
		register(biomeSpace);
		
		commonWorldGenerator = new CommonWorldGenerator();
		GameRegistry.registerWorldGenerator(commonWorldGenerator, 0);
		*/
		
		// Event handlers
		MinecraftForge.EVENT_BUS.register(this);
		
		PacketHandler.init();
		
		// Registers
		cloaks = new CloakManager();
		cameras = new CamerasRegistry();
		
		CelestialObjectManager.onFMLInitialization();
		
		proxy.onForgePreInitialisation();
	}
	
	public void onCommonSetup(@Nonnull final FMLCommonSetupEvent event) {
		WarpDriveConfig.onFMLInitialization();
		
		proxy.onForgeInitialisation();
		
		WarpDriveConfig.onFMLPostInitialization();
		
		if (WarpDriveConfig.isComputerCraftLoaded) {
			peripheralHandler = new WarpDrivePeripheralHandler();
			peripheralHandler.register();
		}
		
		final WorldHandler worldHandler = new WorldHandler();
		MinecraftForge.EVENT_BUS.register(worldHandler);
		
		final ChunkHandler chunkHandler = new ChunkHandler();
		MinecraftForge.EVENT_BUS.register(chunkHandler);
		
		final ChatHandler chatHandler = new ChatHandler();
		MinecraftForge.EVENT_BUS.register(chatHandler);
	}
	
	public void onFMLServerStarting(@Nonnull final FMLServerStartingEvent event) {
		/* TODO MC1.15 Commands
		event.getCommandDispatcher().register(new CommandBed());
		event.getCommandDispatcher().register(new CommandDebug());
		event.getCommandDispatcher().register(new CommandDump());
		event.getCommandDispatcher().register(new CommandEntity());
		event.getCommandDispatcher().register(new CommandFind());
		event.getCommandDispatcher().register(new CommandGenerate());
		event.getCommandDispatcher().register(new CommandInvisible());
		event.getCommandDispatcher().register(new CommandNPC());
		event.getCommandDispatcher().register(new CommandReload());
		event.getCommandDispatcher().register(new CommandRender());
		event.getCommandDispatcher().register(new CommandSpace());
		*/
	}
	
	final public static ArrayList<Biome> biomes = new ArrayList<>(10);
	final public static ArrayList<Block> blocks = new ArrayList<>(100);
	final public static ArrayList<Enchantment> enchantments = new ArrayList<>(10);
	final public static ArrayList<Item> items = new ArrayList<>(50);
	final public static ArrayList<Effect> potions = new ArrayList<>(10);
	final public static ArrayList<Potion> potionTypes = new ArrayList<>(10);
	final public static ArrayList<SoundEvent> soundEvents = new ArrayList<>(100);
	final public static HashMap<ResourceLocation, IRecipe<?>> recipes = new HashMap<>(100);
	final public static ArrayList<VillagerProfession> villagerProfessions = new ArrayList<>(10);
	
	// Register a Biome.
	public static <BIOME extends Biome> BIOME register(@Nonnull final BIOME biome) {
		biomes.add(biome);
		return biome;
	}
	
	// Register a Block with the default ItemBlock class.
	public static <BLOCK extends Block> BLOCK register(@Nonnull final BLOCK block) {
		assert block instanceof IBlockBase;
		return register(block, ((IBlockBase) block).createItemBlock());
	}
	
	// Register a Block with a custom ItemBlock class.
	public static <BLOCK extends Block> BLOCK register(@Nonnull final BLOCK block, @Nullable final BlockItem itemBlock) {
		final ResourceLocation resourceLocation = block.getRegistryName();
		if (resourceLocation == null) {
			WarpDrive.logger.error(String.format("Missing registry name for block %s, ignoring registration...",
			                                     block));
			return block;
		}
		
		assert !blocks.contains(block);
		blocks.add(block);
		
		if (itemBlock != null) {
			itemBlock.setRegistryName(resourceLocation);
			register(itemBlock);
		}
		
		return block;
	}
	
	// Register an Enchantment.
	public static <ENCHANTMENT extends Enchantment> ENCHANTMENT register(@Nonnull final ENCHANTMENT enchantment) {
		enchantments.add(enchantment);
		return enchantment;
	}
	
	// Register an Item.
	public static <ITEM extends Item> ITEM register(@Nonnull final ITEM item) {
		items.add(item);
		return item;
	}
	
	// Register an Potion.
	public static <POTION extends Effect> POTION register(@Nonnull final POTION potion) {
		potions.add(potion);
		return potion;
	}
	
	// Register an PotionType.
	public static <POTION_TYPE extends Potion> POTION_TYPE register(@Nonnull final POTION_TYPE potionType) {
		potionTypes.add(potionType);
		return potionType;
	}
	
	// Register a recipe.
	public static <RECIPE extends IRecipe<?>> RECIPE register(@Nonnull final RECIPE recipe) {
		return register(recipe, "");
	}
	public static <RECIPE extends IRecipe<?>> RECIPE register(@Nonnull final RECIPE recipe, final String suffix) {
		ResourceLocation registryName = recipe.getId();
		/* TODO MC1.15 recipe name
		if (registryName == null) {
			final String path;
			final ItemStack itemStackOutput = recipe.getRecipeOutput();
			assert itemStackOutput.getItem().getRegistryName() != null;
			if (itemStackOutput.isEmpty()) {
				path = recipe.toString();
			} else if (itemStackOutput.getCount() == 1) {
				path = String.format("%s@%d%s",
				                     itemStackOutput.getItem().getRegistryName().getPath(),
				                     itemStackOutput.getDamage(),
				                     suffix );
			} else {
				path = String.format("%s@%dx%d%s",
				                     itemStackOutput.getItem().getRegistryName().getPath(),
				                     itemStackOutput.getDamage(),
				                     itemStackOutput.getCount(),
				                     suffix );
			}
			registryName = new ResourceLocation(MODID, path);
			if (recipes.containsKey(registryName)) {
				logger.error(String.format("Overlapping recipe detected, please report this to the mod author %s",
				                           registryName));
				registryName = new ResourceLocation(MODID, path + "!" + System.nanoTime());
				assert false;
			}
			recipe.setRegistryName(registryName);
		}
		*/
		
		recipes.put(registryName, recipe);
		return recipe;
	}
	
	// Register a SoundEvent.
	public static <SOUND_EVENT extends SoundEvent> SOUND_EVENT register(@Nonnull final SOUND_EVENT soundEvent) {
		soundEvents.add(soundEvent);
		return soundEvent;
	}
	
	// Register a VillagerProfession.
	public static <VILLAGER_PROFESSION extends VillagerProfession> VILLAGER_PROFESSION register(@Nonnull final VILLAGER_PROFESSION villagerProfession) {
		villagerProfessions.add(villagerProfession);
		return villagerProfession;
	}
	
	@SubscribeEvent
	public void onRegisterBiomes(@Nonnull final RegistryEvent.Register<Biome> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		for (final Biome biome : biomes) {
			event.getRegistry().register(biome);
		}
		
		BiomeDictionary.addTypes(biomeSpace, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.WASTELAND);
		
		LocalProfiler.stop(1000);
	}
	
	@SubscribeEvent
	public void onRegisterBlocks(@Nonnull final RegistryEvent.Register<Block> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		for (final Block block : blocks) {
			event.getRegistry().register(block);
		}
	}
	
	private <T extends TileEntity> void doRegisterTileEntity(@Nonnull final RegistryEvent.Register<TileEntityType<?>> event,
	                                                         @Nonnull final Class<T> classTileEntity,
	                                                         @Nonnull final String registryName,
	                                                         @Nonnull final Block[]... blockArrays ) {
		int size = 0;
		for (final Block[] blockArray : blockArrays) {
			size += blockArray.length;
		}
		final Block[] blocks = new Block[size];
		int index = 0;
		for (final Block[] blockArray : blockArrays) {
			for (final Block block : blockArray) {
				blocks[index++] = block;
			}
		}
		doRegisterTileEntity(event, classTileEntity, registryName, blocks);
	}
	
	private <T extends TileEntity> void doRegisterTileEntity(@Nonnull final RegistryEvent.Register<TileEntityType<?>> event,
	                                                         @Nonnull final Class<T> classTileEntity,
	                                                         @Nonnull final String registryName,
	                                                         @Nonnull final Block... blocks ) {
		// create the type
		final TileEntityType<?> tileEntityType = new TileEntityType<>(() -> {
			try {
				return classTileEntity.newInstance();
			} catch (InstantiationException | IllegalAccessException exception) {
				exception.printStackTrace(WarpDrive.printStreamError);
				WarpDrive.logger.error(String.format("Failed to instantiate a new tile entity named %s:%s",
				                                     WarpDrive.MODID, registryName ));
				throw new RuntimeException(exception);
			}
		}, Sets.newHashSet(blocks), null);
		tileEntityType.setRegistryName(WarpDrive.MODID, registryName);
		
		// save in the class itself to avoid lookups when creating new TileEntity instances
		try
		{
			final Field fieldType = classTileEntity.getField("TYPE");
			fieldType.set(null, tileEntityType);
		} catch(NoSuchFieldException | IllegalAccessException exception) {
			exception.printStackTrace(WarpDrive.printStreamError);
			WarpDrive.logger.error(String.format("Failed to save tile entity type named %s:%s into class %s",
			                                     WarpDrive.MODID, registryName, classTileEntity ));
			throw new RuntimeException(exception);
		}
		
		// actually register the type
		event.getRegistry().register(tileEntityType);
	}
	
	@SubscribeEvent
	public void onRegisterTileEntities(@Nonnull final RegistryEvent.Register<TileEntityType<?>> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		doRegisterTileEntity(event, TileEntityAcceleratorCore.class, "accelerator_core", blockAcceleratorCore);
		doRegisterTileEntity(event, TileEntityAcceleratorControlPoint.class, "accelerator_control_point", blockAcceleratorControlPoint);
		doRegisterTileEntity(event, TileEntityAirGeneratorTiered.class, "air_generator", blockAirGeneratorTiered);
		doRegisterTileEntity(event, TileEntityBiometricScanner.class, "biometric_scanner", blockBiometricScanner);
		doRegisterTileEntity(event, TileEntityCamera.class, "camera", blockCamera);
		doRegisterTileEntity(event, TileEntityCapacitor.class, "capacitor", blockCapacitors);
		doRegisterTileEntity(event, TileEntityChunkLoader.class, "chunk_loader", blockChunkLoaders);
		doRegisterTileEntity(event, TileEntityCloakingCore.class, "cloaking_core", blockCloakingCore);
		doRegisterTileEntity(event, TileEntityEnanReactorCore.class, "enan_reactor_core", blockEnanReactorCores);
		doRegisterTileEntity(event, TileEntityEnanReactorLaser.class, "enan_reactor_laser", blockEnanReactorLaser);
		doRegisterTileEntity(event, TileEntityEnvironmentalSensor.class, "environmental_sensor", blockEnvironmentalSensor);
		doRegisterTileEntity(event, TileEntityForceField.class, "force_field", blockForceFields);
		doRegisterTileEntity(event, TileEntityForceFieldProjector.class, "force_field_projector", blockForceFieldHalfProjectors, blockForceFieldFullProjectors);
		doRegisterTileEntity(event, TileEntityForceFieldRelay.class, "force_field_relay", blockForceFieldRelays);
		doRegisterTileEntity(event, TileEntityIC2reactorLaserMonitor.class, "ic2_reactor_laser_monitor", blockIC2reactorLaserCooler);
		// doRegisterTileEntity(event, TileEntityJumpGateCore.class, "jump_gate_core", blockJumpGateCores);
		doRegisterTileEntity(event, TileEntityLaser.class, "laser", blockLaser);
		doRegisterTileEntity(event, TileEntityLaserCamera.class, "laser_camera", blockLaserCamera);
		doRegisterTileEntity(event, TileEntityLaserMedium.class, "laser_medium", blockLaserMediums);
		doRegisterTileEntity(event, TileEntityLaserTreeFarm.class, "laser_tree_farm", blockLaserTreeFarm);
		doRegisterTileEntity(event, TileEntityLift.class, "lift", blockLift);
		doRegisterTileEntity(event, TileEntityMiningLaser.class, "mining_laser", blockMiningLaser);
		doRegisterTileEntity(event, TileEntityMonitor.class, "monitor", blockMonitor);
		doRegisterTileEntity(event, TileEntityParticlesInjector.class, "particles_injector", blockParticlesInjector);
		doRegisterTileEntity(event, TileEntityRadar.class, "radar", blockRadar);
		doRegisterTileEntity(event, TileEntitySecurityStation.class, "security_station", blockSecurityStation);
		doRegisterTileEntity(event, TileEntityShipController.class, "ship_controller", blockShipControllers);
		doRegisterTileEntity(event, TileEntityShipCore.class, "ship_core", blockShipCores);
		doRegisterTileEntity(event, TileEntityShipScanner.class, "ship_scanner", blockShipScanners);
		doRegisterTileEntity(event, TileEntitySiren.class, "siren", blockSirenIndustrials, blockSirenMilitaries);
		doRegisterTileEntity(event, TileEntitySpeaker.class, "speaker", blockSpeakers);
		doRegisterTileEntity(event, TileEntityTransporterBeacon.class, "transporter_beacon", blockTransporterBeacon);
		doRegisterTileEntity(event, TileEntityTransporterCore.class, "transporter_core", blockTransporterCore);
		doRegisterTileEntity(event, TileEntityVirtualAssistant.class, "virtual_assistant", blockVirtualAssistants);
		doRegisterTileEntity(event, TileEntityWeaponController.class, "weapon_controller", blockWeaponController);
		
		LocalProfiler.stop(1000);
	}
	
	@SubscribeEvent
	public void onRegisterEnchantments(@Nonnull final RegistryEvent.Register<Enchantment> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		for (final Enchantment enchantment : enchantments) {
			event.getRegistry().register(enchantment);
		}
		
		LocalProfiler.stop(1000);
	}
	
	@SubscribeEvent
	public void onRegisterEntities(@Nonnull final RegistryEvent.Register<EntityType<?>> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		event.getRegistry().register(EntitySphereGen.TYPE);
		event.getRegistry().register(EntityStarCore.TYPE);
		event.getRegistry().register(EntityCamera.TYPE);
		event.getRegistry().register(EntityParticleBunch.TYPE);
		event.getRegistry().register(EntityLaserExploder.TYPE);
		event.getRegistry().register(EntityNPC.TYPE);
		event.getRegistry().register(EntityOfflineAvatar.TYPE);
		event.getRegistry().register(EntitySeat.TYPE);
		
		LocalProfiler.stop(1000);
	}
	
	@SubscribeEvent
	public void onRegisterItems(@Nonnull final RegistryEvent.Register<Item> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		for (final Item item : items) {
			event.getRegistry().register(item);
			proxy.onModelInitialisation(item);
		}
		for (final Block block : blocks) {
			proxy.onModelInitialisation(block);
		}
		
		LocalProfiler.stop(1000);
	}
	
	@SubscribeEvent
	public void onRegisterPotions(@Nonnull final RegistryEvent.Register<Effect> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		for (final Effect potion : potions) {
			event.getRegistry().register(potion);
		}
		
		LocalProfiler.stop(1000);
	}
	
	@SubscribeEvent
	public void onRegisterPotionTypes(@Nonnull final RegistryEvent.Register<Potion> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		for (final Potion potionType : potionTypes) {
			event.getRegistry().register(potionType);
		}
		
		LocalProfiler.stop(1000);
	}
	
	
	@SubscribeEvent
	public void onRegisterRecipes(@Nonnull final RegistryEvent.Register<IRecipeSerializer<?>> event) {
		LocalProfiler.start(String.format("Registering %s step 1", event.getName()));
		
		Recipes.initOreDictionary();
		
		Recipes.initDynamic();
		
		LocalProfiler.stop(1000);
		
		LocalProfiler.start(String.format("Registering %s step 2", event.getName()));
		
		for (final IRecipe<?> recipe : recipes.values()) {
			// TODO MC1.15 Recipe registration
			// event.getRegistry().register(recipe);
		}
		
		LocalProfiler.stop(1000);
	}
	
	@SubscribeEvent
	public void onRegisterSoundEvents(@Nonnull final RegistryEvent.Register<SoundEvent> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		cr0s.warpdrive.data.SoundEvents.registerSounds();
		for (final SoundEvent soundEvent : soundEvents) {
			event.getRegistry().register(soundEvent);
		}
		
		LocalProfiler.stop(1000);
	}
	
	@SubscribeEvent
	public void onRegisterVillagerProfessions(@Nonnull final RegistryEvent.Register<VillagerProfession> event) {
		LocalProfiler.start(String.format("Registering %s", event.getName()));
		
		for (final VillagerProfession villagerProfession : villagerProfessions) {
			event.getRegistry().register(villagerProfession);
		}
		
		LocalProfiler.stop(1000);
	}
}