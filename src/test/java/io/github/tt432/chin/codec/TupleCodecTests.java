package io.github.tt432.chin.codec;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author TT432
 */
public class TupleCodecTests {
    @Test
    void testTupleCodec() {
        assertEquals(
                ChinExtraCodecs.tuple(Codec.INT, Codec.DOUBLE, Codec.FLOAT)
                        .parse(JsonOps.INSTANCE, new Gson().fromJson("[0, 0.1, 0.2]", JsonArray.class)).getOrThrow(),
                List.of(0, 0.1, 0.2F)
        );

        assertEquals(
                ChinExtraCodecs.tuple(Codec.LONG, Codec.either(Codec.INT, Codec.BOOL))
                        .parse(JsonOps.INSTANCE, new Gson().fromJson("[0, 1234]", JsonArray.class)).getOrThrow(),
                List.of(0L, Either.left(1234))
        );

        assertEquals(
                ChinExtraCodecs.tuple(Codec.LONG, Codec.BYTE, Codec.BYTE, Codec.BYTE)
                        .parse(JsonOps.INSTANCE, new Gson().fromJson("[0, 0, 12, 55]", JsonArray.class)).getOrThrow(),
                List.of(0L, (byte) 0, (byte) 12, (byte) 55)
        );
    }

    @Test
    void testSingleOrList() {
        assertEquals(ChinExtraCodecs.singleOrList(Codec.INT)
                        .parse(JsonOps.INSTANCE, new Gson().fromJson("[1, 2, 3, 4]", JsonArray.class)).getOrThrow(),
                List.of(1, 2, 3, 4));
        assertEquals(ChinExtraCodecs.singleOrList(Codec.INT)
                        .parse(JsonOps.INSTANCE, new Gson().fromJson("2", JsonPrimitive.class)).getOrThrow(),
                List.of(2));
    }

    @Test
    void testCheck() {
        var check = ChinExtraCodecs.check(Codec.STRING,
                s -> s.equals("1.23") ? DataResult.success(s) : DataResult.error(() -> "Not equals 1.23"));

        assertEquals(check.parse(JsonOps.INSTANCE, new Gson().fromJson("\"1.23\"", JsonPrimitive.class)).getOrThrow(),
                "1.23");
        assertThrows(IllegalStateException.class,
                () -> check.parse(JsonOps.INSTANCE, new Gson().fromJson("\"avbsf\"", JsonPrimitive.class)).getOrThrow());
    }

    @Test
    void testWithAlternative() {
        var withAlternative = ChinExtraCodecs.withAlternative(
                Codec.STRING.fieldOf("stringKey"),
                Codec.FLOAT.fieldOf("floatKey").xmap(String::valueOf, Float::parseFloat)
        );

        assertEquals(withAlternative.codec().parse(JsonOps.INSTANCE, new Gson().fromJson("""
                {
                 "stringKey": "testValue"
                }
                """, JsonObject.class)).getOrThrow(), "testValue");

        assertEquals(withAlternative.codec().parse(JsonOps.INSTANCE, new Gson().fromJson("""
                {
                 "floatKey": 1.2
                }
                """, JsonObject.class)).getOrThrow(), "1.2");
    }

    @Test
    void testTreeMap() {
        Codec<TreeMap<Integer, Float>> treeMapCodec = ChinExtraCodecs.treeMap(
                Codec.STRING.xmap(Integer::parseInt, String::valueOf),
                Codec.FLOAT,
                Comparator.comparingInt(i -> i)
        );

        Comparator<Integer> comparator = Comparator.comparingInt(i -> i);
        TreeMap<Integer, Object> treeMap = new TreeMap<>(comparator);
        treeMap.put(1, 1.7F);
        treeMap.put(2, 1.2F);
        treeMap.put(3, 1.5F);

        assertEquals(treeMapCodec.parse(JsonOps.INSTANCE, new Gson().fromJson("""
                {
                  "2" : 1.2,
                  "3" : 1.5,
                  "1" : 1.7
                }
                """, JsonObject.class)).getOrThrow(), treeMap);
    }
}
