package com.gmail.subnokoii78.testplugin.util;

import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.gmail.takenokoii78.json.values.JSONNumber;
import com.gmail.takenokoii78.json.values.TypedJSONArray;
import org.jetbrains.annotations.NotNull;

public class Converter {
    private Converter() {}

    public static @NotNull Vector3Builder jsonArrayToVector3(@NotNull TypedJSONArray<JSONNumber> array) {
        if (array.length() < 3) {
            throw new IllegalArgumentException();
        }
        else {
            return new Vector3Builder(
                array.get(0).doubleValue(),
                array.get(1).doubleValue(),
                array.get(2).doubleValue()
            );
        }
    }
}
