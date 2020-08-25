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
package net.reallifegames.ow.api.v1.balance;

import net.reallifegames.ow.models.UserInfo;

import javax.annotation.Nonnull;

public class BalancedPlayer {

    /**
     * The integer constant for the tank position.
     */
    public static final int TANK_POSITION = 0;

    /**
     * The integer constant for the support position.
     */
    public static final int SUPPORT_POSITION = 2;

    /**
     * The integer constant for the DPS position.
     */
    public static final int DPS_POSITION = 1;

    /**
     * The team the user is on. 0 or 1.
     */
    public final int team;

    /**
     * The players position.
     * <p>
     * Options. {@link BalancedPlayer#TANK_POSITION} {@link BalancedPlayer#SUPPORT_POSITION} {@link
     * BalancedPlayer#DPS_POSITION}
     * <p/>
     */
    public final int position;

    /**
     * The users information.
     */
    public final UserInfo user;

    /**
     * @param team     the team the user is on. 0 or 1.
     * @param position Options. {@link BalancedPlayer#TANK_POSITION} {@link BalancedPlayer#SUPPORT_POSITION} {@link *
     *                 BalancedPlayer#DPS_POSITION}
     * @param user     the users information.
     */
    public BalancedPlayer(final int team, final int position, @Nonnull final UserInfo user) {
        this.team = team;
        this.position = position;
        this.user = user;
    }
}
