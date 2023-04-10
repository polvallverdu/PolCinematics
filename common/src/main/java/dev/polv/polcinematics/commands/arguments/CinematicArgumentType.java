package dev.polv.polcinematics.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class CinematicArgumentType implements ArgumentType<Cinematic> {

    private final CinematicStringType type;

    public CinematicArgumentType(CinematicStringType type) {
        this.type = type;
    }

    public static final SimpleCommandExceptionType CINEMATIC_NOT_FOUND = new SimpleCommandExceptionType(Text.of("Cinematic not found"));
    public static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(Text.of("Invalid UUID"));

    public static CinematicArgumentType name() {
        return new CinematicArgumentType(CinematicStringType.Name);
    }

    public static CinematicArgumentType uuid() {
        return new CinematicArgumentType(CinematicStringType.Uuid);
    }

    public static CinematicArgumentType both() {
        return new CinematicArgumentType(CinematicStringType.Both);
    }

    public static Cinematic getCinematic(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Cinematic.class);
    }

    public CinematicStringType getType() {
        return type;
    }

    @Override
    public Cinematic parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();
        Cinematic cinematic = null;

        switch (type) {
            case Name -> {
                cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(name);
            }
            case Uuid -> {
                try {
                    cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(UUID.fromString(name));
                } catch (IllegalArgumentException e) {
                    throw INVALID_UUID.create();
                }
            }
            case Both -> {
                try {
                    cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(UUID.fromString(name));
                } catch (IllegalArgumentException e) {
                    cinematic = PolCinematics.CINEMATICS_MANAGER.getCinematic(name);
                }
            }
        }

        if (cinematic == null)
            throw CINEMATIC_NOT_FOUND.create();

        return cinematic;
    }

    @Override
    public String toString() {
        return "cinematic()";
    }

    @Override
    public Collection<String> getExamples() {
        return type.getExamples();
    }

    public enum CinematicStringType {
        Name("cinematicname", "intro_01"),
        Uuid(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
        Both("cinematicname", "intro_01", UUID.randomUUID().toString(), UUID.randomUUID().toString()),
        ;

        private final Collection<String> examples;

        CinematicStringType(final String... examples) {
            this.examples = Arrays.asList(examples);
        }

        public Collection<String> getExamples() {
            return examples;
        }
    }

}
