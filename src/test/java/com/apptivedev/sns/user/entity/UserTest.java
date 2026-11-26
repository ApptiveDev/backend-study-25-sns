package com.apptivedev.sns.user.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

class UserTest {
    @Test
    void changeName_success() {
        User user = User.create("test@example.com", "test-hash", "기존 이름");
        user.changeName("새 이름");
        assertThat(user.getName()).isEqualTo("새 이름");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" ", "123456789012345678901"})
    void changeName_invalid_keepsState(String name) {
        User user = User.create("test@example.com", "test-hash", "기존 이름");
        assertThatThrownBy(() -> user.changeName(name)).isInstanceOf(IllegalArgumentException.class);
        assertThat(user.getName()).isEqualTo("기존 이름");
    }
}
