/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Tyler Bucher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.reallifegames.ow;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    /**
     * Open account creation type.
     */
    public static final int ACCOUNT_CREATION_OPEN = 0;

    /**
     * Invite account creation type.
     */
    public static final int ACCOUNT_CREATION_INVITE = 0;

    /**
     * The database url connection string.
     */
    private final String MONGO_CONNECTION_URI;

    /**
     * The name of the mongo db to use.
     */
    private final String MONGO_DATABASE_NAME;

    /**
     * The JWT secretKey auto initialize.
     */
    private final boolean SECRET_KEY_AUTO;

    /**
     * The JWT secretKey.
     */
    private final SecretKey SECRET_KEY;

    /**
     * The amount of time in the future the token will expire.
     */
    private final long JWT_EXPIRE_TIME;

    /**
     * The bare domain for this application.
     */
    private final String DOMAIN;



    public Config() {
        MONGO_CONNECTION_URI = System.getenv("MONGO_CONNECTION_URI");
        MONGO_DATABASE_NAME = System.getenv("MONGO_DATABASE_NAME");
        SECRET_KEY_AUTO = getSecretKeyAutoEnv(true);
        SECRET_KEY = getSecretKeyEnv(SECRET_KEY_AUTO);
        JWT_EXPIRE_TIME = getJwtExpireTimeEnv(604800000L);
        DOMAIN = System.getenv("DOMAIN");
    }

    private boolean getSecretKeyAutoEnv(final boolean defaultValue) {
        final String autoEnvString = System.getenv("SECRET_KEY_AUTO");
        return autoEnvString != null ? Boolean.parseBoolean(autoEnvString) : defaultValue;
    }

    private SecretKey getSecretKeyEnv(final boolean autoGen) {
        final String secretKeyString = System.getenv("SECRET_KEY");
        return autoGen || secretKeyString == null ?
                Keys.secretKeyFor(SignatureAlgorithm.HS256) :
                Keys.hmacShaKeyFor(SecurityModule.hexToBytes(secretKeyString));
    }

    private long getJwtExpireTimeEnv(final long defaultValue) {
        final String jwtEnvString = System.getenv("JWT_EXPIRE_TIME");
        return jwtEnvString != null ? Long.parseLong(jwtEnvString) : defaultValue;
    }

    private int getAccountCreationEnv() {
        final Map<String, Integer> OPTIONS = Map.of("open", ACCOUNT_CREATION_OPEN, "invite", ACCOUNT_CREATION_INVITE);
        final String env = System.getenv("ACCOUNT_CREATION");
        return OPTIONS.getOrDefault(env, ACCOUNT_CREATION_OPEN);
    }

    public String getMongoConnectionUri() {
        return MONGO_CONNECTION_URI;
    }

    public boolean isSecretKeyAuto() {
        return SECRET_KEY_AUTO;
    }

    public SecretKey getSecretKey() {
        return SECRET_KEY;
    }

    public long getJwtExpireTime() {
        return JWT_EXPIRE_TIME;
    }

    public String getMongoDatabaseName() {
        return MONGO_DATABASE_NAME;
    }

    public String getDomain() {
        return DOMAIN;
    }
}
