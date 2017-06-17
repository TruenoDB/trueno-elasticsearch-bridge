package org.trueno.es.bridge;

/**
 * Possible actions that handles the Core Manager Server.
 *
 * @author ebarsallo
 */
public enum TruenoActions {
    SEARCH,
    BULK,
    PERSIST,
    CREATE,
    OPEN,
    DROP;

    public static TruenoActions fromString(String str) {
        for (TruenoActions a : TruenoActions.values()) {
            if (a.name().equalsIgnoreCase(str)) {
                return a;
            }
        }

        throw new IllegalArgumentException(String.format("No constant with value '%s' found", str));
    }
}
