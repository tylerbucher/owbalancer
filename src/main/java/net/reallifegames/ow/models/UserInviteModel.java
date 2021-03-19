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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Invite data model.
 *
 * @author Tyler Bucher
 */
@Entity ("invites")
public class UserInviteModel {

    /**
     * Invite id.
     */
    @Id
    public final String email;

    /**
     * The list of permissions which will be given to the user when they signup.
     */
    public final List<Integer> permissions;

    private UserInviteModel() {
        email = null;
        permissions = new ArrayList<>();
    }

    public UserInviteModel(@Nonnull final String email, @Nonnull final List<Integer> permissions) {
        this.email = email;
        this.permissions = permissions;
    }
}
