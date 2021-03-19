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
package net.reallifegames.ow.api.v1.players.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.ow.DataValidation;
import net.reallifegames.ow.DbModule;

import javax.annotation.Nonnull;

/**
 * A create user request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class PlayersPostRequest {

    /**
     * User the player is tied to.
     */
    public final String userId;

    /**
     * The name of this player.
     */
    public final String playerName;

    /**
     * Names for this player.
     */
    private final String[] names;

    /**
     * States how much this player wants to play this role.
     */
    private final int tankPreference;

    /**
     * States how much this player wants to play this role.
     */
    private final int supportPreference;

    /**
     * States how much this player wants to play this role.
     */
    private final int dpsPreference;

    /**
     * The overwatch skill ranking for tank.
     */
    private final int tankSr;

    /**
     * The overwatch skill ranking for support.
     */
    private final int supportSr;

    /**
     * The overwatch skill ranking for dps.
     */
    private final int dpsSr;

    public PlayersPostRequest(@Nonnull @JsonProperty ("userId") final String userId,
                              @Nonnull @JsonProperty ("playerName") final String playerName,
                              @Nonnull @JsonProperty ("names") final String[] names,
                              @JsonProperty ("tankSr") final int tankSr,
                              @JsonProperty ("tankPreference") final int tankPreference,
                              @JsonProperty ("dpsSr") final int dpsSr,
                              @JsonProperty ("dpsPreference") final int dpsPreference,
                              @JsonProperty ("supportSr") final int supportSr,
                              @JsonProperty ("supportPreference") final int supportPreference) {
        this.userId = userId;
        this.playerName = playerName;
        this.names = names;
        this.tankSr = tankSr;
        this.tankPreference = tankPreference;
        this.dpsSr = dpsSr;
        this.dpsPreference = dpsPreference;
        this.supportSr = supportSr;
        this.supportPreference = supportPreference;
    }

    /**
     * @return false if the username or password length is zero.
     */
    boolean validate() {
        return DataValidation.isSrValid(this.tankSr) &&
                DataValidation.isSrValid(this.dpsSr) &&
                DataValidation.isSrValid(this.supportSr) &&
                DataValidation.isPreferenceValid(this.tankPreference) &&
                DataValidation.isPreferenceValid(this.dpsPreference) &&
                DataValidation.isPreferenceValid(this.supportPreference) &&
                DataValidation.validateNames(this.names);
    }

    /**
     * Attempts to create a new user.
     *
     * @param dbModule    the module instance to use.
     * @param useUsername if we should create a player based on the users username.
     * @return true if the user was created false otherwise.
     */
    public boolean createPlayer(@Nonnull final String userId, final boolean useUsername, @Nonnull final DbModule dbModule) {
        return dbModule.createNewPlayer(userId, playerName, tankPreference, supportPreference, dpsPreference, tankSr, supportSr, dpsSr, names, useUsername);
    }
}
