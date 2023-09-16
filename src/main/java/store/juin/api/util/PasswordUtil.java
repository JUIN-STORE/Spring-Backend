package store.juin.api.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@UtilityClass
public class PasswordUtil {
    public static String makePasswordHash(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
