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

import net.reallifegames.ow.models.PlayerModel;
import net.reallifegames.ow.models.SettingModel;
import net.reallifegames.ow.models.UserInviteModel;
import net.reallifegames.ow.models.UserModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * An interface to define database agnostic functions.
 *
 * @author Tyler Bucher
 */
public interface DbModule {

    @Nullable
    UserModel getUserModelByEmail(@Nonnull final String email);

    @Nullable
    UserModel getUserModelByUsername(@Nonnull final String username);

    @Nullable
    UserInviteModel getUserInviteModelByEmail(@Nonnull final String email);

    boolean createNewUser(@Nonnull final String email,
                          @Nonnull final String username,
                          @Nonnull final String password);

    @Nullable
    SettingModel getSettingByName(@Nonnull final String name);

    boolean createSetting(@Nonnull final SettingModel settingModel, final boolean createIfNotExists);

    boolean createNewInvite(@Nonnull final String email,
                            @Nonnull final List<Integer> permissions);

    boolean updateInvite(@Nonnull final String email,
                         @Nonnull final String newEmail,
                         @Nullable final List<Integer> permissions);

    boolean deleteInvite(@Nonnull final String email);

    List<UserInviteModel> getAllUserInviteModels();

    void close();

    @Nonnull
    List<UserModel> getAllUserModels();

    boolean updateUserModel(@Nonnull final String email,
                            @Nonnull final String username,
                            @Nonnull final String password,
                            final boolean active,
                            @Nullable final List<Integer> permissions);

    boolean deleteUser(@Nonnull final String email);

    boolean createNewPlayer(@Nonnull final String userId,
                            @Nonnull final String playerName,
                            final int tankPreference,
                            final int supportPreference,
                            final int dpsPreference,
                            final int tankSr,
                            final int supportSr,
                            final int dpsSr,
                            @Nonnull final String[] names,
                            final boolean useUsername);

    boolean updatePlayer(@Nonnull final String uuid,
                         @Nonnull final String playerName,
                         final int tankPreference,
                         final int supportPreference,
                         final int dpsPreference,
                         final int tankSr,
                         final int supportSr,
                         final int dpsSr,
                         @Nonnull final String[] names);

    @Nonnull
    List<PlayerModel> getAllPlayerModels();

    @Nonnull
    List<PlayerModel> getPlayerModelsFromUuids(@Nonnull final List<String> uuids);

    @Nullable
    PlayerModel getPlayerModel(@Nonnull final String uuid);

    boolean deletePlayer(@Nonnull final String uuid);
}
