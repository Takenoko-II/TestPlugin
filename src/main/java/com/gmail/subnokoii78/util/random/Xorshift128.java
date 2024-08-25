package com.gmail.subnokoii78.util.random;

import java.time.Instant;

public final class Xorshift128 {
    private long x = 0x6C8E9CF570932BD5L;
    private long y = 0xBB67AE8584CAA73BL;
    private long z = 0xA54FF53A5F1D36F1L;
    private long w;

    public Xorshift128(long seed) {
        w = seed;

        long max = (seed ^ ((seed << 19) ^ (seed >>> 7))) % 18;

        for (long i = max; i >= 0; i--) rand();
    }

    public long rand() {
        long t = w;
        long s = x;
        w = z;
        z = y;
        y = s;

        t ^= t << 11;
        t ^= t >>> 8;

        t = t ^ (t >>> 7) ^ (t << 7);

        x = t ^ s ^ (s >>> 19);

        return x + w;
    }

    public static Xorshift128 now() {
        return new Xorshift128(Instant.now().getNano());
    }
}
