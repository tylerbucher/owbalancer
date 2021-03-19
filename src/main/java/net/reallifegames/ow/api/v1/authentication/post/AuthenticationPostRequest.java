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
package net.reallifegames.ow.api.v1.authentication.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Jws;
import net.reallifegames.ow.Balancer;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.SecurityModule;
import net.reallifegames.ow.models.UserModel;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Get request model for the /authentication endpoint.
 *
 * @author Tyler Bucher
 */
public class AuthenticationPostRequest {

    /**
     * The user to try and login as.
     */
    private final String email;

    /**
     * Password of this login attempt.
     */
    private final String password;

    /**
     * States if the jws token should live longer then the browsing session.
     */
    public final boolean rememberMe;

    @JsonCreator
    public AuthenticationPostRequest(@JsonProperty ("email") @Nonnull final String email,
                                     @JsonProperty ("password") @Nonnull final String password,
                                     @JsonProperty ("rememberMe") final boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    /**
     * @param dbModule the module instance to use.
     * @return true if the data submitted is valid false other wise.
     */
    public boolean validate(@Nonnull final DbModule dbModule) {
        final UserModel userModel = dbModule.getUserModelByEmail(this.email);
        return userModel != null && userModel.active && BCrypt.checkpw(this.password, userModel.passwordHash);
    }

    /**
     * @return a newly created {@link Jws} token for the user.
     */
    String generateToken(@Nonnull final SecurityModule securityModule) {
        return securityModule.getJWSToken(this.email, new Date(System.currentTimeMillis() + Balancer.getConfig().getJwtExpireTime()));
    }
}
