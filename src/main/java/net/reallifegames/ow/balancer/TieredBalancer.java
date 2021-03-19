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
import net.reallifegames.ow.models.PlayerModel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TieredBalancer {

    /**
     *
     */
    private final List<TeamBalanceResult> teamBalanceResultList = new ArrayList<>();
    private final PlayerModel[] userInfoArray = new PlayerModel[13];

    private final PlayerPositionBalancer playerPositionBalancer = new PlayerPositionBalancer();
    private final TeamRoleSRBalancer teamRoleSRBalancer = new TeamRoleSRBalancer();
    private final TeamSRBalancer teamSRBalancer = new TeamSRBalancer();

    public TieredBalancer() {
        for (int i = 0; i < 5; i++) {
            teamBalanceResultList.add(new TeamBalanceResult(-1, new int[12]));
        }
    }

    public TieredBalancerResponse balancePlayers(@Nonnull final DbModule dbModule, @Nonnull final List<String> players) {
        final List<PlayerModel> userInfoList = dbModule.getPlayerModelsFromUuids(players);
        final int[] balanceArray = new int[12];
        userInfoArray[0] = new PlayerModel("", "", 0, 0, 0, 0, 0, 0, new String[0]);
        for (int i = 0; i < userInfoList.size(); i++) {
            balanceArray[i] = i + 1;
            userInfoArray[i + 1] = userInfoList.get(i);
        }

        long start = System.currentTimeMillis();
        permute(balanceArray);
        long end = System.currentTimeMillis();

        final List<List<BalancedPlayer>> balancedPlayerLists = new ArrayList<>();
        for (final TeamBalanceResult balanceResult : teamBalanceResultList) {
            balanceResult.getBalanceInspector().balanceTime = ((float) (end - start)) / 1000.0f;
            final List<BalancedPlayer> balancedPlayerList = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                final PlayerModel userInfo = userInfoArray[balanceResult.getTeam()[i]];
                if (userInfo != null && !userInfo.uuid.equals("")) {
                    final int pos = getBalancedPlayerPosition(i);
                    balancedPlayerList.add(new BalancedPlayer(i < 6 ? 1 : 2, pos, userInfo));
                }
            }

            balancedPlayerLists.add(balancedPlayerList);
        }
        return new TieredBalancerResponse(balancedPlayerLists, teamBalanceResultList);
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

    private void permute(@Nonnull final int[] arr) {
        for (int i = 0; i < 11; i++) {
            swap(arr, 1, 1 + i);
            for (int j = 0; j < 9; j++) {
                swap(arr, 3, 3 + j);
                for (int k = 0; k < 7; k++) {
                    swap(arr, 5, 5 + k);
                    for (int m = 0; m < 5; m++) {
                        swap(arr, 7, 7 + m);
                        for (int n = 0; n < 3; n++) {
                            swap(arr, 10, 10 - n);

                            for (int o = 0; o < 10; o += 2) {                             // Beginning of nested loops for team combos.
                                swap(arr, 2, 2 + o);
                                swap(arr, 3, 3 + o);
                                for (int p = 0 + o; p < 8; p += 2) {                      // End of nested loops for team combos.
                                    swap(arr, 4, 4 + p);
                                    swap(arr, 5, 5 + p);
                                    //############################################################################
                                    //############################################################################
                                    for (int q = 0; q < 6; q += 2) {                        // Beginning of nested loops for team 1 permutations.
                                        swap(arr, 0, 0 + q);
                                        swap(arr, 1, 1 + q);
                                        for (int r = 0; r < 4; r += 2) {                    // End of nested loops for team 1 permutations.
                                            swap(arr, 2, 2 + r);
                                            swap(arr, 3, 3 + r);
                                            //############################################################################
                                            for (int s = 0; s < 6; s += 2) {               // Beginning of nested loops for team 2 permutations.
                                                swap(arr, 6, 6 + s);
                                                swap(arr, 7, 7 + s);
                                                for (int t = 0; t < 4; t += 2) {           // End of nested loops for team 2 permutations.
                                                    swap(arr, 8, 8 + t);
                                                    swap(arr, 9, 9 + t);
                                                    final float roleScore = teamRoleSRBalancer.calcTeamRoleDifference(1.0f, arr, userInfoArray);
                                                    final float positionScore = playerPositionBalancer.calcPlayerPrimaryScore(2.0f, arr, userInfoArray);
                                                    final float teamSrScore = teamSRBalancer.calcTeamSrDifference(1.0f, arr, userInfoArray);
                                                    final float total = roleScore + positionScore;
                                                    for (TeamBalanceResult result : teamBalanceResultList) {
                                                        if (total > result.getScore()) {
                                                            result.setScore(total);
                                                            result.setTeam(arr);
                                                            result.getBalanceInspector()
                                                                    .setFromInspector(total)
                                                                    .setFromInspector(teamRoleSRBalancer)
                                                                    .setFromInspector(teamSRBalancer)
                                                                    .setFromInspector(playerPositionBalancer);
                                                            break;
                                                        }
                                                    }
                                                    swap(arr, 8, 8 + t);
                                                    swap(arr, 9, 9 + t);
                                                }
                                                swap(arr, 6, 6 + s);
                                                swap(arr, 7, 7 + s);
                                            }
                                            //############################################################################
                                            swap(arr, 2, 2 + r);
                                            swap(arr, 3, 3 + r);
                                        }
                                        swap(arr, 0, 0 + q);
                                        swap(arr, 1, 1 + q);
                                    }
                                    //############################################################################
                                    //############################################################################
                                    swap(arr, 4, 4 + p);
                                    swap(arr, 5, 5 + p);
                                }
                                swap(arr, 2, 2 + o);
                                swap(arr, 3, 3 + o);
                            }

                            swap(arr, 10, 10 - n);
                        }
                        swap(arr, 7, 7 + m);
                    }
                    swap(arr, 5, 5 + k);
                }
                swap(arr, 3, 3 + j);
            }
            swap(arr, 1, 1 + i);
        }
    }

    private void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    public static class TieredBalancerResponse {

        public final List<List<BalancedPlayer>> userInfoLists;
        public final List<BalanceInspector> balanceInspectorList;

        public TieredBalancerResponse(List<List<BalancedPlayer>> userInfoLists, List<TeamBalanceResult> teamBalanceResultList) {
            this.userInfoLists = userInfoLists;
            this.balanceInspectorList = new ArrayList<>();
            for (int i = 0; i < teamBalanceResultList.size(); i++) {
                teamBalanceResultList.get(i).getBalanceInspector().finalCalc(userInfoLists.get(i));
                this.balanceInspectorList.add(teamBalanceResultList.get(i).getBalanceInspector());
            }
        }
    }
}
