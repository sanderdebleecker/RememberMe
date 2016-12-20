package be.sanderdebleecker.herinneringsapp.Helpers.Security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*Salt should be a RSA in internal storage to avoid the risk of the APK getting read*/

public class Crypto {
    private static final String SALT = "$€'@{}zZ^^=-_°/19";
    public static String md5(String input) {
        String md5 = null;
        if(null == input) return null;
        try {
            //Init MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //Salt input
            input+=SALT;
            //MD5
            digest.update(input.getBytes(), 0, input.length());
            //MD5 string
            md5 = new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }
}
