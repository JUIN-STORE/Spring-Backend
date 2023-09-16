package store.juin.api.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class AuthNumberUtil {
    private static Random random = new Random();

    public static String makeAuthNumber() {
        int leftLimit = '0';
        int rightLimit = 'z';
        int targetStringLength = 10;

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
