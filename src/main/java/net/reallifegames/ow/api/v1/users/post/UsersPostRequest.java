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
package net.reallifegames.ow.api.v1.users.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.ow.DataValidation;
import net.reallifegames.ow.DbModule;

import javax.annotation.Nonnull;

/**
 * JSON Model for creating a new user.
 *
 * @author Tyler Bucher
 */
public class UsersPostRequest {

    /**
     * Identification filed for the account.
     */
    private final String email;

    /**
     * Visual username for the account.
     */
    private final String username;

    /**
     * The password for the user account.
     */
    private final String password;

    /**
     * Password equality field. Used to make sure passwords match.
     */
    private final String passwordConfirm;

    @JsonCreator
    public UsersPostRequest(@JsonProperty ("email") @Nonnull final String email,
                            @JsonProperty ("username") @Nonnull final String username,
                            @JsonProperty ("password") @Nonnull final String password,
                            @JsonProperty ("passwordConfirm") @Nonnull final String passwordConfirm) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * @return true if the data in this request is valid false otherwise.
     */
    public boolean validate() {
        return DataValidation.isEmailValid(this.email) && DataValidation.isUsernameValid(this.username) &&
                this.password.equals(this.passwordConfirm);
    }

    /**
     * Attempts to create a new user.
     *
     * @param dbModule the module instance to use.
     * @return true if the user was created false otherwise.
     */
    public boolean createNewUser(@Nonnull final DbModule dbModule) {
        return dbModule.createNewUser(this.email, this.username, this.password);
    }
}
