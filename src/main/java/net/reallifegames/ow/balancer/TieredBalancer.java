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

import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.api.v1.balance.BalancedPlayer;
import net.reallifegames.ow.models.TeamBalanceResult;
import net.reallifegames.ow.models.UserInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TieredBalancer {

    private final TeamBalanceResult teamBalanceResult = new TeamBalanceResult(-1, new int[12]);
    private final UserInfo[] userInfoArray = new UserInfo[13];

    private final PlayerPositionBalancer playerPositionBalancer = new PlayerPositionBalancer();
    private final TeamAdaptabilityBalancer teamAdaptabilityBalancer = new TeamAdaptabilityBalancer();
    private final TeamRoleSRBalancer teamRoleSRBalancer = new TeamRoleSRBalancer();
    private final TeamSRBalancer teamSRBalancer = new TeamSRBalancer();

    private final BalanceInspector balanceInspector = new BalanceInspector();

    public TieredBalancerResponse balancePlayers(@Nonnull final DbModule dbModule, @Nonnull final List<Integer> players) {
        final List<UserInfo> userInfoList = dbModule.getUserResponses(players.stream().mapToInt(i->i).toArray());
        final int[] balanceArray = new int[12];
        userInfoArray[0] = new UserInfo(0, "", 0, 0, 0, 0, 0, 0);
        for (int i = 0; i < userInfoList.size(); i++) {
            balanceArray[i] = i + 1;
            userInfoArray[i + 1] = userInfoList.get(i);
        }

        long start = System.currentTimeMillis();
        System.out.println(start);

        permute(balanceArray, 0);

        long end = System.currentTimeMillis();
        System.out.println(end);
        System.out.println(((float) (end - start)) / 1000.0f);

        final List<BalancedPlayer> balancedPlayerList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            final UserInfo userInfo = userInfoArray[teamBalanceResult.getTeam()[i]];
            if (userInfo != null && userInfo.id != 0) {
                final int pos = getBalancedPlayerPosition(i);
                balancedPlayerList.add(new BalancedPlayer(i < 6 ? 1 : 2, pos, userInfo));
            }
        }
        return new TieredBalancerResponse(balancedPlayerList, teamBalanceResult.getBalanceInspector());
    }

    private int getBalancedPlayerPosition(final int i) {
        if (i == 0 || i == 1 || i == 6 || i == 7) {
            return BalancedPlayer.TANK_POSITION;
        } else if (i == 2 || i == 3 || i == 8 || i == 9) {
            return BalancedPlayer.DPS_POSITION;
        } else {
            return BalancedPlayer.SUPPORT_POSITION;
        }
    }

    private void permute(@Nonnull final int[] arr, final int k) {
        for (int i = k; i < arr.length; i++) {
            swap(arr, i, k);
            permute(arr, k + 1);
            swap(arr, k, i);
        }
        if (k == arr.length - 1) {
            final float teamSr = teamSRBalancer.calcTeamSrDifference(1.0f, arr, userInfoArray);
            final float adpScore = teamAdaptabilityBalancer.calcTeamAdaptabilityScore(2.0f, arr, userInfoArray);
            final float roleScore = teamRoleSRBalancer.calcTeamRoleDifference(4.0f, arr, userInfoArray);
            final float positionScore = playerPositionBalancer.calcPlayerPrimaryScore(8.0f, arr, userInfoArray);
            final float total = teamSr + adpScore + roleScore + positionScore;
            if (total > teamBalanceResult.getScore()) {
                teamBalanceResult.setScore(total);
                teamBalanceResult.setTeam(arr);
                teamBalanceResult.getBalanceInspector()
                        .setFromInspector(teamSRBalancer)
                        .setFromInspector(teamAdaptabilityBalancer)
                        .setFromInspector(teamRoleSRBalancer)
                        .setFromInspector(playerPositionBalancer);
            }
        }
    }

    private void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    public static class TieredBalancerResponse {

        public final List<BalancedPlayer> userInfoList;
        public final BalanceInspector balanceInspector;

        public TieredBalancerResponse(List<BalancedPlayer> userInfoList, BalanceInspector balanceInspector) {
            this.userInfoList = userInfoList;
            this.balanceInspector = balanceInspector;
            this.balanceInspector.finalCalc(userInfoList);
        }
    }
}
