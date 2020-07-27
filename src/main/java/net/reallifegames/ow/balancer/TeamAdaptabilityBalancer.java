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

public class TeamAdaptabilityBalancer {

    public int team1Adaptability;
    public int team2Adaptability;

    public float calcTeamAdaptabilityScore(final float div,
                                                   @Nonnull final int[] idArray,
                                                   @Nonnull final UserInfo[] userInfoList) {
        team1Adaptability = calcTeamAdaptability(0, idArray, userInfoList);
        team2Adaptability = calcTeamAdaptability(6, idArray, userInfoList);
        return (((float) Math.min(team2Adaptability, team1Adaptability)) * div) / ((float) Math.max(team2Adaptability, team1Adaptability));
    }

    private int calcTeamAdaptability(final int offset, @Nonnull final int[] idArray, @Nonnull final UserInfo[] userInfoList) {
        return userInfoList[idArray[offset]].totalPref +
                userInfoList[idArray[offset + 1]].totalPref +
                userInfoList[idArray[offset + 2]].totalPref +
                userInfoList[idArray[offset + 3]].totalPref +
                userInfoList[idArray[offset + 4]].totalPref +
                userInfoList[idArray[offset + 5]].totalPref;
    }
}
