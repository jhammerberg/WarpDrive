package cr0s.warpdrive.client;

import cr0s.warpdrive.CommonProxy;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IItemBase;
import cr0s.warpdrive.entity.EntityNPC;
import cr0s.warpdrive.entity.EntityOfflineAvatar;
import cr0s.warpdrive.entity.EntityParticleBunch;
import cr0s.warpdrive.event.ClientHandler;
import cr0s.warpdrive.event.ModelBakeEventHandler;
import cr0s.warpdrive.event.TooltipHandler;
import cr0s.warpdrive.render.ClientCameraHandler;
import cr0s.warpdrive.render.RenderEntityNPC;
import cr0s.warpdrive.render.RenderEntityOfflineAvatar;
import cr0s.warpdrive.render.RenderEntityParticleBunch;
import cr0s.warpdrive.render.RenderOverlayAir;
import cr0s.warpdrive.render.RenderOverlayCamera;
import cr0s.warpdrive.render.RenderOverlayLocation;

import javax.annotation.Nonnull;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	public static final KeyBinding keyBindingCameraCenter  = new KeyBinding("key.camera.center.name"  , InputMappings.Type.MOUSE, 3, WarpDrive.MODID);
	public static final KeyBinding keyBindingCameraShoot   = new KeyBinding("key.camera.shoot.name"   , 32, WarpDrive.MODID);
	public static final KeyBinding keyBindingCameraZoomIn  = new KeyBinding("key.camera.zoom_in.name" , InputMappings.Type.MOUSE, 0, WarpDrive.MODID);
	public static final KeyBinding keyBindingCameraZoomOut = new KeyBinding("key.camera.zoom_out.name", InputMappings.Type.MOUSE, 1, WarpDrive.MODID);
	
	@Override
	public void onForgePreInitialisation() {
		super.onForgePreInitialisation();
		
		MinecraftForge.EVENT_BUS.register(ModelBakeEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(SpriteManager.INSTANCE);
		
		// entity rendering
		RenderingRegistry.registerEntityRenderingHandler(EntityNPC.TYPE, new IRenderFactory<EntityNPC>() {
			@Nonnull
			@Override
			public EntityRenderer<EntityNPC> createRenderFor(final EntityRendererManager manager) {
				return new RenderEntityNPC(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityOfflineAvatar.TYPE, new IRenderFactory<EntityOfflineAvatar>() {
			@Nonnull
			@Override
			public EntityRenderer<EntityOfflineAvatar> createRenderFor(final EntityRendererManager manager) {
				return new RenderEntityOfflineAvatar(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityParticleBunch.TYPE, new IRenderFactory<EntityParticleBunch>() {
			@Nonnull
			@Override
			public EntityRenderer<EntityParticleBunch> createRenderFor(final EntityRendererManager manager) {
				return new RenderEntityParticleBunch(manager);
			}
		});
		
		// Key bindings
		ClientRegistry.registerKeyBinding(keyBindingCameraCenter);
		ClientRegistry.registerKeyBinding(keyBindingCameraShoot);
		ClientRegistry.registerKeyBinding(keyBindingCameraZoomIn);
		ClientRegistry.registerKeyBinding(keyBindingCameraZoomOut);
	}
	
	@Override
	public void onForgeInitialisation() {
		super.onForgeInitialisation();
		
		// event handlers
		MinecraftForge.EVENT_BUS.register(new ClientHandler());
		MinecraftForge.EVENT_BUS.register(new TooltipHandler());
		
		// color handlers
		// final Item itemAirShield = Item.getItemFromBlock(WarpDrive.blockAirShields);
		// Minecraft.getInstance().getItemColors().registerItemColorHandler((IItemColor) itemAirShield, itemAirShield);
		// TODO MC1.15 air shield colors
		// Minecraft.getInstance().getBlockColors().registerBlockColorHandler(new BlockColorAirShield(), WarpDrive.blockAirShields);
		
		// generic rendering
		// MinecraftForge.EVENT_BUS.register(new WarpDriveKeyBindings());
		MinecraftForge.EVENT_BUS.register(new RenderOverlayAir());
		MinecraftForge.EVENT_BUS.register(new RenderOverlayCamera());
		MinecraftForge.EVENT_BUS.register(new RenderOverlayLocation());
		
		MinecraftForge.EVENT_BUS.register(new ClientCameraHandler());
	}
	
	@Override
	public void onModelInitialisation(final Object object) {
		if (object instanceof IBlockBase) {
			((IBlockBase) object).modelInitialisation();
			
		} else if (object instanceof IItemBase) {
			((IItemBase) object).modelInitialisation();
			
		} else {
			throw new RuntimeException(String.format("Unsupported object, expecting an IBlockBase or IItemBase instance: %s",
			                                         object));
		}
	}
	
	@Nonnull
	public static ModelResourceLocation getModelResourceLocation(@Nonnull final ItemStack itemStack) {
		final Item item = itemStack.getItem();
		ResourceLocation resourceLocation = item.getRegistryName();
		assert resourceLocation != null;
		
		// defaults to inventory variant
		return new ModelResourceLocation(resourceLocation, "inventory");
	}
	
	public static void modelInitialisation(@Nonnull final Item item) {
		if (!(item instanceof IItemBase)) {
			throw new RuntimeException(String.format("Unable to initialize item's model, expecting an IItemBase instance: %s",
			                                         item));
		}
		
		final ModelResourceLocation modelResourceLocation = ((IItemBase) item).getModelResourceLocation(new ItemStack(item));
		// TODO MC1.15 item rendering
		// ModelLoader.setCustomModelResourceLocation(item, 0, modelResourceLocation);
	}
}