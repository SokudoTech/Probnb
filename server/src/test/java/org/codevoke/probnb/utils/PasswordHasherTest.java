package org.codevoke.probnb.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherTest {

    @Test
    void hash_ShouldReturnDifferentHashesForSamePassword() {
        // when
        String hash1 = PasswordHasher.hash("testpassword");
        String hash2 = PasswordHasher.hash("testpassword");

        // then
        assertThat(hash1).isNotEqualTo(hash2);
        assertThat(hash1).isNotNull();
        assertThat(hash2).isNotNull();
        assertThat(hash1).contains("$");
        assertThat(hash2).contains("$");
    }

    @Test
    void verify_WhenCorrectPassword_ShouldReturnTrue() {
        // given
        String password = "testpassword";
        String hash = PasswordHasher.hash(password);

        // when
        boolean result = PasswordHasher.verify(password, hash);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void verify_WhenIncorrectPassword_ShouldReturnFalse() {
        // given
        String password = "testpassword";
        String wrongPassword = "wrongpassword";
        String hash = PasswordHasher.hash(password);

        // when
        boolean result = PasswordHasher.verify(wrongPassword, hash);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void verify_WhenEmptyPassword_ShouldReturnFalse() {
        // given
        String password = "testpassword";
        String hash = PasswordHasher.hash(password);

        // when
        boolean result = PasswordHasher.verify("", hash);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void verify_WhenNullPassword_ShouldReturnFalse() {
        // given
        String password = "testpassword";
        String hash = PasswordHasher.hash(password);

        // when
        boolean result = PasswordHasher.verify(null, hash);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void hash_ShouldHandleSpecialCharacters() {
        // given
        String passwordWithSpecialChars = "p@ssw0rd!@#$%^&*()";

        // when
        String hash = PasswordHasher.hash(passwordWithSpecialChars);
        boolean result = PasswordHasher.verify(passwordWithSpecialChars, hash);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void hash_ShouldHandleUnicodeCharacters() {
        // given
        String passwordWithUnicode = "пароль123";

        // when
        String hash = PasswordHasher.hash(passwordWithUnicode);
        boolean result = PasswordHasher.verify(passwordWithUnicode, hash);

        // then
        assertThat(result).isTrue();
    }
} 