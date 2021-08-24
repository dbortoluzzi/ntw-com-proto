package eu.dbortoluzzi.commons.utils;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

import java.util.Locale;

public class StringUtils {

    public static String encodeHexString(byte [] digest) {
        return Hex.encodeHexString(digest, false);
    }

    @SneakyThrows
    public static byte[] decodeHex(String str) {
        return Hex.decodeHex(str.toLowerCase(Locale.ROOT));
    }
}
