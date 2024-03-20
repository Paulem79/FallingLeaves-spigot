package io.github.paulem.fallingleaves.utils;

import io.github.paulem.fallingleaves.leaves.Leaf;

import java.security.SecureRandom;

public class SafeRandom {
    public static int randBtw(int min, int max){
        return new SecureRandom().nextInt(max-min+1)+min;
    }

    public static double generateDouble() {
        // Use a SecureRandom instance to generate a cryptographically secure random number.
        SecureRandom secureRandom = new SecureRandom();

        // Generate a random double between 0.0 and 1.0.
        double randomDouble = secureRandom.nextDouble();

        return randomDouble * 0.1 + Leaf.FALL_SPEED/2;
    }
}
