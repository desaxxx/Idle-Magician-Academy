package org.nandayo;

public class Calculate {

    public static int getLevelRequirements(short level) {
        final int x = level;
        return 25*x*x + 75*x;
    }
}
