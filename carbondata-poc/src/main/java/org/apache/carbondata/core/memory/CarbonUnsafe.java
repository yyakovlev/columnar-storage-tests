package org.apache.carbondata.core.memory;

import java.lang.reflect.Field;
import java.nio.ByteOrder;

import sun.misc.Unsafe;

public class CarbonUnsafe {
    public static final int BYTE_ARRAY_OFFSET;

    public static final int SHORT_ARRAY_OFFSET;

    public static final int INT_ARRAY_OFFSET;

    public static final int LONG_ARRAY_OFFSET;

    public static final int DOUBLE_ARRAY_OFFSET;

    public static final int FLOAT_ARRAY_OFFSET;

    public static final boolean IS_LITTLE_ENDIAN =
            ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);

    private static Unsafe unsafe;

    static {
        try {
            Field cause = Unsafe.class.getDeclaredField("theUnsafe");
            cause.setAccessible(true);
            unsafe = (Unsafe) cause.get(null);
            //unsafe = null;
        } catch (Throwable var2) {
            unsafe = null;
        }
        if (unsafe != null) {
            BYTE_ARRAY_OFFSET = unsafe.arrayBaseOffset(byte[].class);
            SHORT_ARRAY_OFFSET = unsafe.arrayBaseOffset(short[].class);
            INT_ARRAY_OFFSET = unsafe.arrayBaseOffset(int[].class);
            LONG_ARRAY_OFFSET = unsafe.arrayBaseOffset(long[].class);
            FLOAT_ARRAY_OFFSET = unsafe.arrayBaseOffset(float[].class);
            DOUBLE_ARRAY_OFFSET = unsafe.arrayBaseOffset(double[].class);
        } else {
            BYTE_ARRAY_OFFSET = 0;
            SHORT_ARRAY_OFFSET = 0;
            INT_ARRAY_OFFSET = 0;
            LONG_ARRAY_OFFSET = 0;
            FLOAT_ARRAY_OFFSET = 0;
            DOUBLE_ARRAY_OFFSET = 0;
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }
}
