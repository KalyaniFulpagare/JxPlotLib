package com.jplotx.data;

public record DatabaseConfig(
        String jdbcUrl,
        String username,
        String password
) {
}
