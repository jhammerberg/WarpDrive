package cr0s.warpdrive.command;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class BooleanArgumentType implements ArgumentType<Boolean> {
	private static final Collection<String> EXAMPLES = Arrays.asList("true", "1", "yes", "false", "0", "no");
	
	private BooleanArgumentType() {
	}
	
	@Nonnull
	public static BooleanArgumentType create() {
		return new BooleanArgumentType();
	}
	
	public static boolean getBoolean(@Nonnull final CommandContext<?> context, @Nonnull final String name) {
		return context.getArgument(name, Boolean.class);
	}
	
	@Nonnull
	@Override
	public Boolean parse(@Nonnull final StringReader reader) throws CommandSyntaxException {
		final int start = reader.getCursor();
		final String value = reader.readString();
		if (value.isEmpty()) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext(reader);
		}
		
		switch (value) {
		case "true":
		case "1":
		case "yes":
			return true;
		case "false":
		case "0":
		case "no":
			return false;
		default:
			reader.setCursor(start);
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool().createWithContext(reader, value);
		}
	}
	
	@Nonnull
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(@Nonnull final CommandContext<S> context, @Nonnull final SuggestionsBuilder builder) {
		if ("true".startsWith(builder.getRemaining().toLowerCase())) {
			builder.suggest("true");
		}
		if ("yes".startsWith(builder.getRemaining().toLowerCase())) {
			builder.suggest("yes");
		}
		if ("false".startsWith(builder.getRemaining().toLowerCase())) {
			builder.suggest("false");
		}
		if ("no".startsWith(builder.getRemaining().toLowerCase())) {
			builder.suggest("no");
		}
		return builder.buildFuture();
	}
	
	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}