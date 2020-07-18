package cr0s.warpdrive.network;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.core.ClassTransformer;
import cr0s.warpdrive.network.PacketHandler.IMessage;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageClientValidation implements IMessage {
	
	private String mapClass;
	
	@SuppressWarnings("unused")
	public MessageClientValidation() {
		// required on receiving side
	}
	
	@Override
	public void encode(@Nonnull final PacketBuffer buffer) {
		final int size = buffer.readInt();
		mapClass = buffer.toString(buffer.readerIndex(), size, StandardCharsets.UTF_8);
		buffer.readerIndex(buffer.readerIndex() + size);
	}
	
	@Override
	public void decode(@Nonnull final PacketBuffer buffer) {
		final String mapClassFull = ClassTransformer.getClientValidation();
		final String mapClassTruncated = mapClassFull.substring(0, Math.min(32700, mapClassFull.length()));
		final byte[] bytesString = mapClassTruncated.getBytes(StandardCharsets.UTF_8);
		buffer.writeInt(bytesString.length);
		buffer.writeBytes(bytesString);
	}
	
	private void handle(final String namePlayer) {
		try {
			if (new File("ClientValidation").exists()) {
				final String fileName = String.format("ClientValidation/%s.tsv", namePlayer);
				
				final File file = new File(fileName);
				if (!file.exists()) {
					//noinspection ResultOfMethodCallIgnored
					file.createNewFile();
				}
				
				final PrintWriter printWriter = new PrintWriter(new FileWriter(file));
				printWriter.println(mapClass);
				printWriter.close();
			}
		} catch (final IOException exception) {
			exception.printStackTrace(WarpDrive.printStreamError);
			WarpDrive.logger.error("Exception while saving client validation to disk");
		}
	}
	
	@Override
	public IMessage process(@Nonnull final Context context) {
		assert context.getSender() != null;
		if (WarpDrive.isDev) {
			WarpDrive.logger.info(String.format("Received client validation packet from %s",
			                                    context.getSender().getName()));
		}
		
		handle(context.getSender().getName().getUnformattedComponentText());
        
		return null;	// no response
	}
}
