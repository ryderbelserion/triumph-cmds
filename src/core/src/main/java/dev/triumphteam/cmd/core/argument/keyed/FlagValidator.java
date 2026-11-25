package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import org.jspecify.annotations.Nullable;

/**
 * Modified from commons-cli.
 * <a href="https://github.com/apache/commons-cli">...</a>
 */
final class FlagValidator {

    private FlagValidator() {
        throw new AssertionError("Util class must not be initialized.");
    }

    /**
     * Checks whether the flag contains illegal characters.
     *
     * @param flag The {@link String} flag.
     */
    static void validate(@Nullable final String flag) {
        if (flag == null) return;

        // handle the single character flag
        if (flag.length() == 1) {
            char character = flag.charAt(0);

            if (!isValidFlag(character)) {
                throw new CommandRegistrationException("Illegal flag name \"" + character + "\" on flag \"" + flag + "\".");
            }

            return;
        }

        // handle the multi character flag
        for (char character : flag.toCharArray()) {
            if (!isValidChar(character)) {
                throw new CommandRegistrationException("The flag \"" + flag + "\" contains an illegal character \"" + character + "\".");
            }
        }
    }

    /**
     * Returns whether the specified character is a valid Option.
     *
     * @param c the option to validate
     * @return true if <code>c</code> is a letter, <code>?</code> or <code>@</code>, otherwise false.
     */
    private static boolean isValidFlag(char c) {
        return isValidChar(c) || c == '?' || c == '@';
    }

    /**
     * Returns whether the specified character is a valid character.
     *
     * @param c the character to validate
     * @return true if <code>c</code> is a letter.
     */
    private static boolean isValidChar(char c) {
        return Character.isJavaIdentifierPart(c);
    }
}