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

import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.ow.DbModule;

import javax.annotation.Nonnull;

/**
 * A create user request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class UsersPostRequest {

    /**
     * Requested username for new user.
     */
    private final String username;

    /**
     * Requested password for new user.
     */
    private final String[] overwatchNames;

    private final int tankSr;
    private final int tankPreference;
    private final int dpsSr;
    private final int dpsPreference;
    private final int supportSr;
    private final int supportPreference;

    public UsersPostRequest(@Nonnull @JsonProperty ("username") final String username,
                            @Nonnull @JsonProperty ("overwatchNames") final String[] overwatchNames,
                            @JsonProperty ("tankSr") final int tankSr,
                            @JsonProperty ("tankPreference") final int tankPreference,
                            @JsonProperty ("dpsSr") final int dpsSr,
                            @JsonProperty ("dpsPreference") final int dpsPreference,
                            @JsonProperty ("supportSr") final int supportSr,
                            @JsonProperty ("supportPreference") final int supportPreference) {
        this.username = username;
        this.overwatchNames = overwatchNames;
        this.tankSr = tankSr;
        this.tankPreference = tankPreference;
        this.dpsSr = dpsSr;
        this.dpsPreference = dpsPreference;
        this.supportSr = supportSr;
        this.supportPreference = supportPreference;
    }

    /**
     * @param dbModule the module instance to use.
     * @return true if a user exists in the database false otherwise.
     */
    boolean userExists(@Nonnull final DbModule dbModule) {
        return dbModule.userExists(this.username);
    }

    /**
     * @return false if the username or password length is zero.
     */
    boolean isDataValid() {
        return !(this.username.length() == 0 || this.tankSr < 1 || this.tankSr > 5000 ||
                this.dpsSr < 1 || this.dpsSr > 5000 || this.supportSr < 1 || this.supportSr > 5000 ||
                this.tankPreference < 0 || this.tankPreference > 2 || this.dpsPreference < 0 || this.dpsPreference > 2
                || this.supportPreference < 0 || this.supportPreference > 2);
    }

    /**
     * Attempts to create a new user.
     *
     * @param dbModule the module instance to use.
     * @return true if the user was created false otherwise.
     */
    public boolean createUser(@Nonnull final DbModule dbModule) {
        boolean userCreated = dbModule.createUser(
                this.username,
                this.tankSr,
                this.tankPreference,
                this.dpsSr,
                this.dpsPreference,
                this.supportSr,
                this.supportPreference
        );
        if (userCreated) {
            userCreated = dbModule.addOverwatchName(dbModule.getUserId(this.username), this.overwatchNames);
        }
        return userCreated;
    }

    public boolean updateUser(final int id, @Nonnull final DbModule dbModule) {
        boolean userUpdated = dbModule.updateUser(
                id,
                this.username,
                this.tankSr,
                this.tankPreference,
                this.dpsSr,
                this.dpsPreference,
                this.supportSr,
                this.supportPreference
        );
        if (userUpdated) {
            if(userUpdated = dbModule.deleteOwNamesForId(id)) {
                userUpdated = dbModule.addOverwatchName(id, this.overwatchNames);
            }
        }
        return userUpdated;
    }
}
