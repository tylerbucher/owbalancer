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
package net.reallifegames.ow.api.v1.users.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.ow.DataValidation;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.Permissions;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User patch request data model.
 *
 * @author Tyler Bucher
 */
public class UsersPatchRequest {

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
     * Is this user allowed to login.
     */
    private final boolean active;

    /**
     * The list of permissions for this user.
     */
    private final List<Integer> permissions;

    /**
     * @param email       identification filed for the account.
     * @param username    visual username for the account.
     * @param password    password for the user account.
     * @param active      this user allowed to login.
     * @param permissions list of permissions for this user.
     */
    @JsonCreator
    public UsersPatchRequest(@JsonProperty ("email") @Nonnull final String email,
                             @JsonProperty ("username") @Nonnull final String username,
                             @JsonProperty ("password") @Nonnull final String password,
                             @JsonProperty ("active") final boolean active,
                             @JsonProperty ("permissions") final List<Integer> permissions) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.active = active;
        this.permissions = permissions;
    }

    /**
     * @return true if the data in this request is valid false otherwise.
     */
    public boolean validate() {
        return DataValidation.isUsernameValid(this.username);
    }

    /**
     * @param dbModule        the module instance to use.
     * @param withPermissions should permissions
     * @return true if the user was updated false otherwise.
     */
    public boolean updateUser(@Nonnull final DbModule dbModule, final boolean withPermissions) {
        return dbModule.updateUserModel(
                this.email,
                this.username,
                this.password,
                this.active,
                withPermissions ? Permissions.getValidPermissions(this.permissions) : null
        );
    }
}
