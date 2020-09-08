package com.crypto;

import com.authentication.exceptions.codes.ErrorCode;
import com.util.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class HashHmacPasswordEncoder implements PasswordEncoder {

    final static Logger logger = LoggerFactory.getLogger(HashHmacPasswordEncoder.class);

    private static final String HASH_ALGORITHM = "HMACSHA256";
    private static final int SALT_LENGTH = 32; // bytes
    private static final String SECRET = "a1e06ca8854045958c5796ba7bf00927";

    private static final class HashHmacPasswordEncoderHolder {
        static final HashHmacPasswordEncoder INSTANCE = new HashHmacPasswordEncoder();
    }

    public static HashHmacPasswordEncoder getInstance() {
        return HashHmacPasswordEncoderHolder.INSTANCE;
    }

    public String encode(CharSequence password) {
        logger.debug("Encoding password");
        String passwordLower = password.toString();
        try {
            return createHash(passwordLower.toCharArray());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(ErrorCode.PASSWORD_HASH_CREATION_NOT_SUCCESFUL.getDescription(), e);
            throw new ServiceException(ErrorCode.PASSWORD_HASH_CREATION_NOT_SUCCESFUL);
        }
    }

    /**
     * Returns a salted hash of the password.
     *
     * @param password the password to hash
     * @return a salted hash of the password in the form of HASH:SALT:ALGORITHM
     * @throws InvalidKeyException, NoSuchAlgorithmException
     */
    private static String createHash(char[] password) throws InvalidKeyException, NoSuchAlgorithmException {
        logger.debug("create hash");
        // Generate a random salt
        String salt = EncodingUtils.getRandomString(SALT_LENGTH);
        // Hash the password
        byte[] hash = encodeWithSalt(salt.getBytes(), password);

        // Format hash:salt:algorithm
        return String.valueOf(Hex.encode(hash)) + ":" + salt + ":" + "1";
    }

    private static byte[] encodeWithSalt(byte[] salt, char[] password) throws NoSuchAlgorithmException, InvalidKeyException {
        Key sk = new SecretKeySpec(SECRET.getBytes(), HASH_ALGORITHM);
        Mac mac = Mac.getInstance(sk.getAlgorithm());
        mac.init(sk);
        byte[] passwordBytes = new String(password).getBytes();
        byte[] saltPassword = new byte[salt.length + passwordBytes.length];
        System.arraycopy(salt, 0, saltPassword, 0, salt.length);
        System.arraycopy(passwordBytes, 0, saltPassword, salt.length, passwordBytes.length);
        return mac.doFinal(saltPassword);
    }

    /**
     * Validates a password using a hash.
     * Verify that the encoded password obtained from storage matches the submitted raw
     * password after it is too encoded. Returns true if the passwords match, false if
     * they do not. The stored password itself is never decoded.
     *
     * @param password               the password to check
     * @param encodedPassword the stored hashed password with salt
     * @return true if the password is correct, false if not
     */
    @Override
    public boolean matches(CharSequence password, String encodedPassword) {
        String passwordLower = password.toString();
        String[] parts = encodedPassword.split(":");
        byte[] salt = new byte[0];
        if (parts.length > 1) {
            salt = parts[1].getBytes();
        }
        byte[] hash = Hex.decode(parts[0]);

        return matches(passwordLower, hash, salt);
    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param hash hashed password
     * @param salt Salt used to hash password
     * @return true if the password is correct, false if not
     */
     boolean matches(String password, byte[] hash, byte[] salt) {
        boolean result;
        try {
            result = matches(password.toCharArray(), hash, salt);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            logger.error(ErrorCode.PASSWORD_HASH_CREATION_NOT_SUCCESFUL.getDescription(), e);
            throw new ServiceException(ErrorCode.PASSWORD_HASH_CREATION_NOT_SUCCESFUL);
        }

        return result;
    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param hash hashed password
     * @param salt Salt used to hash password
     * @return true if the password is correct, false if not
     */
    private static boolean matches(char[] password, byte[] hash, byte[] salt) throws InvalidKeyException, NoSuchAlgorithmException {

        // Compute the hash of the provided password, using the same salt, iteration count and hash length
        byte[] testHash = encodeWithSalt(salt, password);

        // Compare the hashes in constant time. The password is correct if both hashes match.
        return EncodingUtils.slowEquals(hash, testHash);

    }

}
