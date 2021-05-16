package cr0s.warpdrive.network;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.network.PacketHandler.IMessage;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;

import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageClientValidation implements IMessage {
	
	private String mapClass;
	
	@SuppressWarnings("unused")
	public MessageClientValidation() {
		// required on receiving side
	}
	
	@Override
	public void decode(@Nonnull final PacketBuffer buffer) {
		final int size = buffer.readInt();
		mapClass = buffer.toString(buffer.readerIndex(), size, StandardCharsets.UTF_8);
		buffer.readerIndex(buffer.readerIndex() + size);
	}
	
	@Override
	public void encode(@Nonnull final PacketBuffer buffer) {
		buffer.writeInt(0);
		
		final StringBuilder result = new StringBuilder().append(new Date());
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			while (classLoader != null) {
				Class<?> CL_class = classLoader.getClass();
				while (CL_class != java.lang.ClassLoader.class) {
					CL_class = CL_class.getSuperclass();
				}
				final Field fieldClassLoader_classes = CL_class.getDeclaredField("classes");
				fieldClassLoader_classes.setAccessible(true);
				final Vector<Class<?>> classes1 = (Vector<Class<?>>) fieldClassLoader_classes.get(classLoader);
				final Class<?>[] classes2 = classes1.toArray(new Class<?>[0]);
				int count = 0;
				for (Class<?> aClass : classes2) {
					collectClass(aClass);
					count += 1;
				}
				result.append("- ").append(classLoader).append(" ").append(count);
				classLoader = classLoader.getParent();
			}
		} catch (final Exception exception) {
			exception.printStackTrace(WarpDrive.printStreamInfo);
		}
		for (final String key : countClass.keySet()) {
			result.append("\n").append(key)
			      .append("\t").append(countClass.get(key));
		}
		
		final String mapClassFull = result.toString();
		final String mapClassTruncated = mapClassFull.substring(0, Math.min(32700, mapClassFull.length()));
		final byte[] bytesString = mapClassTruncated.getBytes(StandardCharsets.UTF_8);
		buffer.writeInt(bytesString.length);
		buffer.writeBytes(bytesString);
	}
	
	public ConcurrentSkipListMap<String, Integer> countClass = new ConcurrentSkipListMap<>();
	
	private void collectClass(@Nonnull final Class<?> aClass) {
		final String name = aClass.getName();
		final String[] nameParts = name.split("[.$]");
		final String shortName = nameParts[0] + "." + (nameParts.length > 1 ? nameParts[1] : "");
		Integer count = countClass.get(shortName);
		if (count == null) {
			count = 0;
		}
		countClass.put(shortName, count + 1);
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
			                                    context.getSender().getName().getString() ));
		}
		
		handle(context.getSender().getName().getString());
        
		return null;	// no response
	}
}
