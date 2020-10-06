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
import net.reallifegames.ow.models.UserInfoTableModel;
import net.reallifegames.ow.models.UserNamesTableModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    /**
     * @return the list of users in the db.
     */
    Map<Integer, String> getUserNameList();

    /**
     * Checks to see if a user already exists.
     *
     * @param username the username to check.
     * @return true if the user exists false otherwise.
     */
    boolean userExists(@Nonnull final String username);

    /**
     * Checks to see if a user already exists.
     *
     * @param id the id to check.
     * @return true if the user exists false otherwise.
     */
    boolean deletePlayer(final int id);

    /**
     * Attempts to create a new user.
     *
     * @param username name of the new user.
     * @return true if the user was created false otherwise.
     */
    boolean createUser(@Nonnull final String username,
                       final int tankSr,
                       final int tankPreference,
                       final int dpsSr,
                       final int dpsPreference,
                       final int supportSr,
                       final int supportPreference);

    /**
     * @param username the name of a user.
     * @return the id of a user based on the username.
     */
    int getUserId(@Nonnull final String username);

    /**
     * Adds overwatch usernames to a users profile.
     *
     * @param id             the id of the users profile.
     * @param overwatchNames the list of names to add to a user.
     * @return true if the operation was successful false otherwise.
     */
    boolean addOverwatchName(final int id, @Nonnull final String[] overwatchNames);

    /**
     * @param id the users id to get info for.
     * @return a users information object.
     */
    @Nullable
    UserInfo getUserInfo(final int id);

    /**
     * @param id the id of the user.
     * @return the list of usernames in the db.
     */
    List<String> getNameListForId(final int id);

    /**
     * Deletes the list of overwatch usernames for a user.
     *
     * @param id the id of the user.
     * @return true if the operation was successful false otherwise.
     */
    boolean deleteOwNamesForId(final int id);

    /**
     * Updates a users information.
     *
     * @param id                the id of the user.
     * @param username          the new users name.
     * @param tankSr            the new sr for the user.
     * @param tankPreference    0 for not wanting to play, 1 for can play, 2 for wants to play.
     * @param dpsSr             the new sr for the user.
     * @param dpsPreference     0 for not wanting to play, 1 for can play, 2 for wants to play.
     * @param supportSr         the new sr for the user.
     * @param supportPreference 0 for not wanting to play, 1 for can play, 2 for wants to play.
     * @return true if the operation was successful false otherwise.
     */
    boolean updateUser(final int id,
                       @Nonnull final String username,
                       final int tankSr,
                       final int tankPreference,
                       final int dpsSr,
                       final int dpsPreference,
                       final int supportSr,
                       final int supportPreference);

    /**
     * @return the entire user table.
     */
    List<UserInfoTableModel> getAllUserTableData();

    /**
     * @return the entire user names table.
     */
    List<UserNamesTableModel> getAllUsersNames();
}
