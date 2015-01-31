package com.enrootapp.v2.main.util;

import java.util.Random;

/**
 * Created by rmuttineni on 16/01/2015.
 */
public class RandomGen {
    Random gen;
    public RandomGen() {
        gen = new Random();
    }

    public RandomGen(long seed) {
        gen = new Random(seed);
    }

    public <T> T getArrayElem(T[] array) {
        return array[gen.nextInt(array.length)];
    }

    public int nextInt() {
        return gen.nextInt();
    }

    public int nextInt(int n) {
        return gen.nextInt(n);
    }
}
