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

import net.reallifegames.ow.models.PlayerModel;

import javax.annotation.Nonnull;

public class TeamSRBalancer {

    private final float LOW_THRESHOLD = 120.0f;

    private final float HIGH_THRESHOLD = 150.0f;

    private final float LOW_INFLATION = 10.0f;

    private final float HIGH_INFLATION = 20.0f;

    public int team1Sr;

    public int team2Sr;

    public float calcTeamSrDifference(final float div,
                                      @Nonnull final int[] idArray,
                                      @Nonnull final PlayerModel[] userInfoList) {
        team1Sr = getTeamSr(0, idArray, userInfoList);
        final int team1Size = teamSize(0, idArray, userInfoList);
        final int iTeam1Sr = team1Size == 0 ?
                team1Sr : (int) (team1Sr + (team1Sr * inflateTeamSr(team1Sr / team1Size, 0, idArray, userInfoList) / 100));
        team2Sr = getTeamSr(6, idArray, userInfoList);
        final int team2Size = teamSize(6, idArray, userInfoList);
        final int iTeam2Sr = team2Size == 0 ?
                team2Sr : (int) (team2Sr + (team2Sr * inflateTeamSr(team2Sr / team2Size, 6, idArray, userInfoList) / 100));
        return (((float) Math.min(iTeam1Sr, iTeam2Sr)) * div) / ((float) Math.max(iTeam2Sr, iTeam1Sr));
    }

    private int teamSize(final int offset, @Nonnull final int[] idArray, @Nonnull final PlayerModel[] userInfoList) {
        int ts = 0;
        for (int i = 0; i < 6; i++) {
            if (!userInfoList[idArray[offset]].uuid.equals("")) {
                ts++;
            }
        }
        return ts;
    }

    private int getTeamSr(final int offset, @Nonnull final int[] idArray, @Nonnull final PlayerModel[] userInfoList) {
        return userInfoList[idArray[offset]].tankSr +
                userInfoList[idArray[offset + 1]].tankSr +
                userInfoList[idArray[offset + 2]].dpsSr +
                userInfoList[idArray[offset + 3]].dpsSr +
                userInfoList[idArray[offset + 4]].supportSr +
                userInfoList[idArray[offset + 5]].supportSr;
    }

    private float inflateTeamSr(final int teamSr, final int offset, @Nonnull final int[] idArray, @Nonnull final PlayerModel[] userInfoList) {
        return getInflation(teamSr, userInfoList[idArray[offset]].tankSr) +
                getInflation(teamSr, userInfoList[idArray[offset + 1]].tankSr) +
                getInflation(teamSr, userInfoList[idArray[offset + 2]].dpsSr) +
                getInflation(teamSr, userInfoList[idArray[offset + 3]].dpsSr) +
                getInflation(teamSr, userInfoList[idArray[offset + 4]].supportSr) +
                getInflation(teamSr, userInfoList[idArray[offset + 5]].supportSr);
    }

    private float getInflation(final int teamSr, final int sr) {
        final float percent = (sr * 100.0f) / (float) teamSr;
        return percent > HIGH_THRESHOLD ? HIGH_INFLATION : percent > LOW_THRESHOLD ? LOW_INFLATION : 0.0f;
    }
}
