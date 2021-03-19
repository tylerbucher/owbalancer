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
package net.reallifegames.ow.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mongo db user model.
 *
 * @author Tyler Bucher.
 */
@Entity ("users")
public class UserModel {

    /**
     * Id for users.
     */
    @Id
    public final String email;

    /**
     * The visual display name when using the application.
     */
    @Indexed (options = @IndexOptions (unique = true))
    public final String username;

    /**
     * The password hash to check when logging in.
     */
    public final String passwordHash;

    /**
     * States if this user is active or not.
     */
    public final boolean active;

    /**
     * List of permissions this user currently has.
     */
    public final List<Integer> permissions;

    /**
     * List of players this user owns.
     */
    public final List<PlayerModel> players;

    private UserModel() {
        email = null;
        username = null;
        passwordHash = null;
        active = false;
        permissions = new ArrayList<>();
        players = new ArrayList<>();
    }

    public UserModel(@Nonnull final String email,
                     @Nonnull final String username,
                     @Nonnull final String passwordHash,
                     final boolean active,
                     @Nonnull final List<Integer> permissions,
                     @Nonnull final List<PlayerModel> players) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.active = active;
        this.permissions = permissions;
        this.players = players;
    }

    /**
     * @param permissionNodes list of nodes to check.
     * @return true if this user has any of the provided nodes, false otherwise.
     */
    public boolean hasPermission(@Nonnull final List<Integer> permissionNodes) {
        return this.active && (permissionNodes.isEmpty() || !Collections.disjoint(this.permissions, permissionNodes));
    }
}
