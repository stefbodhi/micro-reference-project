package com.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashHmacPasswordEncoderTest {

    private HashHmacPasswordEncoder hashHmacPasswordEncoder = HashHmacPasswordEncoder.getInstance();

    @Test
    void password_should_be_equals_after_encoding() {
        String password = "dog";
        String encodedPassword = hashHmacPasswordEncoder.encode(password);
        System.out.println(encodedPassword);
        Assertions.assertTrue(hashHmacPasswordEncoder.matches(password, encodedPassword));
    }

    @Test
    void password_should_be_different_after_encoding() {
        String password = "Test123";
        String encodedPassword = hashHmacPasswordEncoder.encode(password);

        Assertions.assertFalse(hashHmacPasswordEncoder.matches("Test456", encodedPassword));
    }

}
