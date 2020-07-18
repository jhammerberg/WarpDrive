package cr0s.warpdrive.block;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.api.computer.ISecurityStation;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;

import net.minecraftforge.common.util.Constants;

public class TileEntitySecurityStation extends TileEntityAbstractMachine implements ISecurityStation {
	
	public static TileEntityType<TileEntitySecurityStation> TYPE;
	
	// persistent properties
	public final ArrayList<String> players = new ArrayList<>();
	
	public TileEntitySecurityStation() {
		super(TYPE);
		
		peripheralName = "warpdriveSecurityStation";
		addMethods(new String[] {
				"getAttachedPlayers"
				});
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		players.clear();
		final ListNBT tagListPlayers = tagCompound.getList("players", Constants.NBT.TAG_STRING);
		for (int index = 0; index < tagListPlayers.size(); index++) {
			final String namePlayer = tagListPlayers.getString(index);
			players.add(namePlayer);
		}
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		final ListNBT tagListPlayers = new ListNBT();
		for (final String namePlayer : players) {
			final StringNBT tagStringPlayer = StringNBT.valueOf(namePlayer);
			tagListPlayers.add(tagStringPlayer);
		}
		tagCompound.put("players", tagListPlayers);
		
		return tagCompound;
	}
	
	@Override
	public CompoundNBT writeItemDropNBT(CompoundNBT tagCompound) {
		tagCompound = super.writeItemDropNBT(tagCompound);
		
		tagCompound.remove("players");
		
		return tagCompound;
	}
	
	@Override
	public WarpDriveText getStatus() {
		return super.getStatus()
		            .append(null, "warpdrive.security_station.guide.registered_players",
		                    getAttachedPlayersList());
	}
	
	public WarpDriveText attachPlayer(final PlayerEntity entityPlayer) {
		for (int i = 0; i < players.size(); i++) {
			final String name = players.get(i);
			
			if (entityPlayer.getName().getUnformattedComponentText().equals(name)) {
				players.remove(i);
				WarpDriveText text = Commons.getChatPrefix(getBlockState());
				text.appendSibling(new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.security_station.guide.player_unregistered",
				                                     getAttachedPlayersList()));
				return text;
			}
		}
		
		entityPlayer.attackEntityFrom(DamageSource.GENERIC, 1);
		players.add(entityPlayer.getName().getUnformattedComponentText());
		WarpDriveText text = Commons.getChatPrefix(getBlockState());
		text.appendSibling(new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.security_station.guide.player_registered",
		                                     getAttachedPlayersList()));
		return text;
	}
	
	protected String getAttachedPlayersList() {
		if (players.isEmpty()) {
			return "<nobody>";
		}
		
		final StringBuilder list = new StringBuilder();
		
		for (int i = 0; i < players.size(); i++) {
			final String nick = players.get(i);
			list.append(nick).append(((i == players.size() - 1) ? "" : ", "));
		}
		
		return list.toString();
	}
	
	public String getFirstOnlinePlayer() {
		if (players == null || players.isEmpty()) {// no crew defined
			return null;
		}
		
		for (final String namePlayer : players) {
			final PlayerEntity entityPlayer = Commons.getOnlinePlayerByName(namePlayer);
			if (entityPlayer != null) {// crew member is online
				return namePlayer;
			}
		}
		
		// all cleared
		return null;
	}
	
	// Common OC/CC methods
	@Override
	public Object[] getAttachedPlayers() {
		final StringBuilder list = new StringBuilder();
		
		if (!players.isEmpty()) {
			for (int i = 0; i < players.size(); i++) {
				final String nick = players.get(i);
				list.append(nick).append((i == players.size() - 1) ? "" : ",");
			}
		}
		
		return new Object[] { list.toString(), players.toArray() };
	}
	
	// OpenComputers callback methods
	@Callback(direct = true)
	public Object[] getAttachedPlayers(final Context context, final Arguments arguments) {
		OC_convertArgumentsAndLogCall(context, arguments);
		return getAttachedPlayers();
	}
	
	// ComputerCraft IDynamicPeripheral methods
	@Override
	protected Object[] CC_callMethod(@Nonnull final String methodName, @Nonnull final Object[] arguments) {
		switch (methodName) {
		case "getAttachedPlayers":
			return getAttachedPlayers();
			
		default:
			return super.CC_callMethod(methodName, arguments);
		}
	}
}
