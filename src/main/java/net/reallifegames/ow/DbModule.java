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

import net.reallifegames.ow.models.UserInfo;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * An interface to define database agnostic functions.
 *
 * @author Tyler Bucher
 */
public interface DbModule {

    /**
     * Attempts to create the db tables.
     */
    void createTables();

    /**
     * Attempts to create the db tables.
     */
    void createTables(@Nonnull final String... tableStatements);

    /**
     * Retrieves a list of user info from a list of ids.
     *
     * @param userIds the list of ids to get info for.
     * @return users admin and active status from a database or null if user not found.
     */
    List<UserInfo> getUserResponses(final int[] userIds);

    /**
     * @return the list of users in the db.
     */
    List<Map.Entry<Integer, String>> getUserList();
}
