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

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;

import javax.annotation.Nonnull;

/**
 * Mongo db user model.
 *
 * @author Tyler Bucher.
 */
@Embedded
public class PlayerModel {

    /**
     * The uuid of this player.
     */
    @Indexed (options = @IndexOptions (unique = true))
    public final String uuid;

    /**
     * The name of this player.
     */
    public final String playerName;

    /**
     * States how much this player wants to play this role.
     */
    public final int tankPreference;

    /**
     * States how much this player wants to play this role.
     */
    public final int supportPreference;

    /**
     * States how much this player wants to play this role.
     */
    public final int dpsPreference;

    /**
     * The overwatch skill ranking for tank.
     */
    public final int tankSr;

    /**
     * The overwatch skill ranking for support.
     */
    public final int supportSr;

    /**
     * The overwatch skill ranking for dps.
     */
    public final int dpsSr;

    /**
     * Names for this players
     */
    public final String[] names;

    public PlayerModel() {
        this.uuid = "";
        this.playerName = "";
        this.names = new String[0];
        this.tankPreference = 0;
        this.supportPreference = 0;
        this.dpsPreference = 0;
        this.tankSr = 0;
        this.supportSr = 0;
        this.dpsSr = 0;
    }

    public PlayerModel(@Nonnull final String uuid,
                       @Nonnull final String playerName,
                       final int tankPreference,
                       final int supportPreference,
                       final int dpsPreference,
                       final int tankSr,
                       final int supportSr,
                       final int dpsSr,
                       @Nonnull final String[] names) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.names = names;
        this.tankPreference = tankPreference;
        this.supportPreference = supportPreference;
        this.dpsPreference = dpsPreference;
        this.tankSr = tankSr;
        this.supportSr = supportSr;
        this.dpsSr = dpsSr;
    }
}
