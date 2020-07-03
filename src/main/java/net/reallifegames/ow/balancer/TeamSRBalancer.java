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
package net.reallifegames.ow.balancer;

import net.reallifegames.ow.models.UserInfo;

import javax.annotation.Nonnull;

public class TeamSRBalancer {

    public int team1Sr;

    public int team2Sr;

    public float calcTeamSrDifference(final float div,
                                             @Nonnull final int[] idArray,
                                             @Nonnull final UserInfo[] userInfoList) {
        team1Sr = getTeamSr(0, idArray, userInfoList);
        team2Sr = getTeamSr(6, idArray, userInfoList);
        if (team2Sr > team1Sr) {
            return (((float) team1Sr) * div) / ((float) team2Sr);
        } else {
            return (((float) team2Sr) * div) / ((float) team1Sr);
        }
    }

    private int getTeamSr(final int offset, @Nonnull final int[] idArray, @Nonnull final UserInfo[] userInfoList) {
        return userInfoList[idArray[offset]].tankSr +
                userInfoList[idArray[offset + 1]].tankSr +
                userInfoList[idArray[offset + 2]].dpsSr +
                userInfoList[idArray[offset + 3]].dpsSr +
                userInfoList[idArray[offset + 4]].supportSr +
                userInfoList[idArray[offset + 5]].supportSr;
    }
}
