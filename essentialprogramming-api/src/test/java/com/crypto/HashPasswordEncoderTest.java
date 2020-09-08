package com.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashPasswordEncoderTest {

    private HashPasswordEncoder hashPasswordEncoder = HashPasswordEncoder.getInstance();

    @Test
    void password_should_be_equals_after_encoding() {
        String password = "dog";
        String encodedPassword = hashPasswordEncoder.encode(password);
        System.out.println(encodedPassword);
        Assertions.assertTrue(hashPasswordEncoder.matches(password, encodedPassword));
    }

    @Test
    void password_should_be_different_after_encoding() {
        String password = "Test123";
        String encodedPassword = hashPasswordEncoder.encode(password);

        Assertions.assertFalse(hashPasswordEncoder.matches("Test456", encodedPassword));
    }

    @Test
    void test() {
        String password = "Test1234!";
        Assertions.assertTrue(PasswordHash.matches(password, "c6873d5176f62980d73376776c22cf9e9d01408bfd43e47ef1fa423430491de2:s2s0EnBbcHWX7FjQdf7wbYl8E725JoMD:1"));
    }
}
