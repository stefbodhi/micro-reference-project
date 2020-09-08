package com.crypto;

import com.authentication.exceptions.codes.ErrorCode;
import com.util.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPasswordEncoder extends AbstractPasswordEncoder {

    final static Logger logger = LoggerFactory.getLogger(HashPasswordEncoder.class);

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 32; // bytes

    private static final class HashPasswordEncoderHolder {
        static final HashPasswordEncoder INSTANCE = new HashPasswordEncoder();
    }

    public static HashPasswordEncoder getInstance() {
        return HashPasswordEncoderHolder.INSTANCE;
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
        return String.valueOf(Hex.encode(hash)) + ":" +  salt + ":" + "1";
    }



    private static byte[] encodeWithSalt(byte[] salt, char[] password) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] passwordBytes = new String(password).getBytes();
        //byte[] saltPassword = new byte[salt.length + passwordBytes.length];
        //System.arraycopy(salt, 0, saltPassword, 0, salt.length);
        //System.arraycopy(passwordBytes, 0, saltPassword, salt.length, passwordBytes.length);

        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        md.update(salt);
        return md.digest(passwordBytes);

    }


    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param hash Previously hashed password
     * @param salt The salt used for the derivation
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
     * @param hash Previously hashed password
     * @param salt The salt used for the derivation
     * @return true if the password is correct, false if not
     */
    private static boolean matches(char[] password, byte[] hash, byte[] salt) throws InvalidKeyException, NoSuchAlgorithmException {

        // Compute the hash of the provided password, using the same salt, iteration count and hash length
        byte[] testHash = encodeWithSalt(salt, password);

        // Compare the hashes in constant time. The password is correct if both hashes match.
        return EncodingUtils.slowEquals(hash, testHash);

    }

}
