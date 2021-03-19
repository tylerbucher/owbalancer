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
package net.reallifegames.ow.api.v1.users.get;

import net.reallifegames.ow.models.UserModel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An safe version of the UserModel so passwords are not exposed.
 *
 * @author Tyler Bucher
 */
public class SafeUserModel {

    /**
     * Identification filed for the account.
     */
    public final String email;

    /**
     * Visual username for the account.
     */
    public final String username;

    /**
     * Is this user allowed to login.
     */
    public final boolean active;

    /**
     * The list of permissions for this user.
     */
    public final List<Integer> permissions;

    /**
     * The amount of players this user has.
     */
    public final int playerCount;

    public SafeUserModel(@Nonnull final String email,
                         @Nonnull final String username,
                         final boolean active,
                         @Nonnull final List<Integer> permissions,
                         final int playerCount) {
        this.email = email;
        this.username = username;
        this.active = active;
        this.permissions = permissions;
        this.playerCount = playerCount;
    }

    /**
     * @param userModel the UserModel to convert.
     * @return the converted UserModel.
     */
    public static SafeUserModel fromUserModel(@Nonnull final UserModel userModel, final boolean canSeeEmail) {
        return new SafeUserModel(canSeeEmail ? userModel.email : "", userModel.username, userModel.active, userModel.permissions, userModel.players.size());
    }
}
