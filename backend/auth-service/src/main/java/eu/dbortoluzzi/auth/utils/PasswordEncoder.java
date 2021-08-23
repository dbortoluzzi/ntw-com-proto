package eu.dbortoluzzi.auth.utils;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

// TODO: improve... md5 is hackerable...
public class PasswordEncoder {

    static Logger logger = LoggerFactory.getLogger(PasswordEncoder.class);

    public static String encryptPassword(String cleanPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(cleanPassword.getBytes());
            byte[] digest = md.digest();
            return Hex.encodeHexString(digest).toUpperCase();
        }catch (Exception ex) {
            logger.error("error encryptPassword: ", ex);
            throw new IllegalStateException(ex);
        }

    }

}
