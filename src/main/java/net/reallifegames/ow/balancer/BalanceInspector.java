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

import net.reallifegames.ow.api.v1.balance.BalancedPlayer;

import javax.annotation.Nonnull;
import java.util.List;

public class BalanceInspector {

    public float balanceScore;

    public float balanceTime;

    public int team1TotalSr;

    public int team1AverageSr;

    public int team1TotalSrDistribution;

    public int team1TotalAverageSr;

    public int team2TotalSr;

    public int team2AverageSr;

    public int team2TotalSrDistribution;

    public int team2TotalAverageSr;

    public int team1Adaptability;

    public int team1TankAdaptability;

    public int team1DpsAdaptability;

    public int team1SupportAdaptability;

    public int team2Adaptability;

    public int team2TankAdaptability;

    public int team2DpsAdaptability;

    public int team2SupportAdaptability;

    public int team1PositionPreferenceCount;

    public int team2PositionPreferenceCount;

    public int totalSRDifference;

    public BalanceInspector setFromInspector(@Nonnull final PlayerPositionBalancer playerPositionInspector) {
        this.team1PositionPreferenceCount = playerPositionInspector.team1PositionPreferenceCount;
        this.team2PositionPreferenceCount = playerPositionInspector.team2PositionPreferenceCount;
        return this;
    }

    public BalanceInspector setFromInspector(@Nonnull final TeamAdaptabilityBalancer teamAdaptabilityInspector) {
        this.team1Adaptability = teamAdaptabilityInspector.team1Adaptability;
        this.team2Adaptability = teamAdaptabilityInspector.team2Adaptability;
        return this;
    }

    public BalanceInspector setFromInspector(@Nonnull final TeamRoleSRBalancer teamRoleSRInspector) {
        this.totalSRDifference = teamRoleSRInspector.totalSRDifference;
        return this;
    }

    public BalanceInspector setFromInspector(@Nonnull final TeamSRBalancer teamSRInspector) {
        this.team1TotalSr = teamSRInspector.team1Sr;
        this.team2TotalSr = teamSRInspector.team2Sr;
        return this;
    }

    public BalanceInspector setFromInspector(final float score) {
        this.balanceScore = score;
        return this;
    }

    public void finalCalc(@Nonnull final List<BalancedPlayer> userInfoList) {
        final int[] teamCount = {0, 0};
        final int[] teamTotalSrDistribution = {0, 0};
        final int[] teamAdaptability = {0, 0, 0, 0, 0, 0};
        userInfoList.forEach(balancedPlayer->{
            int i = balancedPlayer.team == 1 ? 0 : 1;
            int j = i == 0 ? 0 : 3;
            teamCount[i]++;
            teamTotalSrDistribution[i] += balancedPlayer.user.tankSr + balancedPlayer.user.dpsSr + balancedPlayer.user.supportSr;
            teamAdaptability[j] += balancedPlayer.user.tankPreference;
            teamAdaptability[j + 1] += balancedPlayer.user.dpsPreference;
            teamAdaptability[j + 2] += balancedPlayer.user.supportPreference;
        });
        this.team1AverageSr = team1TotalSr / teamCount[0];
        this.team2AverageSr = team2TotalSr / teamCount[1];
        this.team1TotalSrDistribution = teamTotalSrDistribution[0];
        this.team2TotalSrDistribution = teamTotalSrDistribution[1];
        this.team1TotalAverageSr = teamTotalSrDistribution[0] / 3 /teamCount[0];
        this.team2TotalAverageSr = teamTotalSrDistribution[1] / 3 / teamCount[1];
        this.team1Adaptability = (this.team1Adaptability * 100) / 36;
        this.team1TankAdaptability = (teamAdaptability[0] * 100) / 12;
        this.team1DpsAdaptability = (teamAdaptability[1] * 100) / 12;
        this.team1SupportAdaptability = (teamAdaptability[2] * 100) / 12;
        this.team2Adaptability = (this.team2Adaptability * 100) / 36;
        this.team2TankAdaptability = (teamAdaptability[3] * 100) / 12;
        this.team2DpsAdaptability = (teamAdaptability[4] * 100) / 12;
        this.team2SupportAdaptability = (teamAdaptability[5] * 100) / 12;
    }
}
