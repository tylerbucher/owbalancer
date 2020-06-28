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

import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.models.TeamBalance;
import net.reallifegames.ow.models.UserInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TieredBalancer {

    /**
     * A static class to hold the singleton.
     */
    private static final class SingletonHolder {

        /**
         * The Sql module singleton.
         */
        private static final TieredBalancer INSTANCE = new TieredBalancer();
    }

    /**
     * @return {@link TieredBalancer} singleton.
     */
    public static TieredBalancer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public List<BalancedPlayer> balancePlayers(@Nonnull final DbModule dbModule, @Nonnull final List<Integer> players) {
        final List<BalancedPlayer> balancedPlayerList = new ArrayList<>();
        final List<UserInfo> userInfoList = dbModule.getUserResponses(players.stream().mapToInt(i->i).toArray());

        System.out.println(System.currentTimeMillis());
        final Map<Integer, UserInfo> userInfoMap = userInfoListToMap(userInfoList);
        final int[] balanceArray = new int[12];
        for (int i = 0; i < players.size(); i++) {
            balanceArray[i] = players.get(i);
        }

        final TeamBalance teamBalance = new TeamBalance(-1, balanceArray);
        permute(balanceArray, 0, teamBalance, userInfoMap);
        calcTeamSrDifference(balanceArray, userInfoMap);

        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 12; i++) {
            final int id = teamBalance.getTeam()[i];
            if (id != 0) {
                final UserInfo userInfo = userInfoMap.get(id);
                final int pos = getBalancedPlayerPosition(i);
                balancedPlayerList.add(new BalancedPlayer(userInfo.id, userInfo.name, i < 6 ? 1 : 2, pos, getUserInfoSr(id, pos, userInfo)));
            }
        }
        return balancedPlayerList;
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

    private Map<Integer, UserInfo> userInfoListToMap(@Nonnull final List<UserInfo> userInfoList) {
        final Map<Integer, UserInfo> userInfoMap = new HashMap<>();
        for (final UserInfo userInfo : userInfoList) {
            userInfoMap.put(userInfo.id, userInfo);
        }
        return userInfoMap;
    }

    private void permute(@Nonnull final int[] arr, int k, @Nonnull final TeamBalance teamBalance, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        for (int i = k; i < arr.length; i++) {
            swap(arr, i, k);
            permute(arr, k + 1, teamBalance, userInfoMap);
            swap(arr, k, i);
        }
        if (k == arr.length - 1) {
            //permutationList.add(arr.clone());
            float s = calcTeamScore(arr, userInfoMap);
            if (s > teamBalance.getScore()) {
                teamBalance.setScore(s);
                teamBalance.setTeam(arr.clone());
            }
        }
    }

    private void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    private float calcTeamScore(@Nonnull final int[] idArray, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        return calcTeamRoleDifference(idArray, userInfoMap) +
                calcTeamAdaptabilityScore(idArray, userInfoMap) +
                calcPlayerPrimaryScore(idArray, userInfoMap) +
                calcTeamSrDifference(idArray, userInfoMap);
    }

    private float calcTeamRoleDifference(@Nonnull final int[] idArray, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        final int team1TankSr = getUserSr(0, idArray[0], userInfoMap) + getUserSr(0, idArray[1], userInfoMap);
        final int team1DpsSr = getUserSr(1, idArray[2], userInfoMap) + getUserSr(1, idArray[3], userInfoMap);
        final int team1SupportSr = getUserSr(2, idArray[4], userInfoMap) + getUserSr(2, idArray[5], userInfoMap);
        final int team1Sr = team1TankSr + team1DpsSr + team1SupportSr;

        final int team2TankSr = getUserSr(0, idArray[6], userInfoMap) + getUserSr(0, idArray[7], userInfoMap);
        final int team2DpsSr = getUserSr(1, idArray[8], userInfoMap) + getUserSr(1, idArray[9], userInfoMap);
        final int team2SupportSr = getUserSr(2, idArray[10], userInfoMap) + getUserSr(2, idArray[11], userInfoMap);
        final int team2Sr = team2TankSr + team2DpsSr + team2SupportSr;

        final int totalDiff = Math.abs(team1TankSr - team2TankSr) + Math.abs(team1DpsSr - team2DpsSr) + Math.abs(team1SupportSr - team2SupportSr);
        if (team2Sr > team1Sr) {
            return (((float) (team2Sr - totalDiff)) * 8.0f) / ((float) team2Sr);
        } else {
            return (((float) (team1Sr - totalDiff)) * 8.0f) / ((float) team1Sr);
        }
    }

    private float calcTeamAdaptabilityScore(@Nonnull final int[] idArray, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        final int team1Adapt = calcTeamAdaptability(0, idArray, userInfoMap);
        final int team2Adapt = calcTeamAdaptability(6, idArray, userInfoMap);
        if (team2Adapt > team1Adapt) {
            return (((float) team1Adapt) * 4.0f) / ((float) team2Adapt);
        } else {
            return (((float) team2Adapt) * 4.0f) / ((float) team1Adapt);
        }
    }

    private int calcTeamAdaptability(final int offset, @Nonnull final int[] idArray, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        return getUserAdaptability(idArray[offset], userInfoMap) +
                getUserAdaptability(idArray[offset + 1], userInfoMap) +
                getUserAdaptability(idArray[offset + 2], userInfoMap) +
                getUserAdaptability(idArray[offset + 3], userInfoMap) +
                getUserAdaptability(idArray[offset + 4], userInfoMap) +
                getUserAdaptability(idArray[offset + 5], userInfoMap);
    }

    private int getUserAdaptability(final int id, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        if (id == 0) {
            return 0;
        } else {
            return userInfoMap.get(id).tankPreference + userInfoMap.get(id).dpsPreference + userInfoMap.get(id).supportPreference;
        }
    }

    private float calcPlayerPrimaryScore(@Nonnull final int[] idArray, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        final int team1Pos = calcTeamPrimaryPosition(0, idArray, userInfoMap);
        final int team2Pos = calcTeamPrimaryPosition(6, idArray, userInfoMap);
        if (team2Pos > team1Pos) {
            return (((float) team1Pos) * 2.0f) / ((float) team2Pos);
        } else {
            return (((float) team2Pos) * 2.0f) / ((float) team1Pos);
        }
    }

    private float calcTeamSrDifference(@Nonnull final int[] idArray, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        final int team1Sr = calcTeamSr(0, idArray, userInfoMap);
        final int team2Sr = calcTeamSr(6, idArray, userInfoMap);
        if (team2Sr > team1Sr) {
            return ((float) team1Sr) / ((float) team2Sr);
        } else {
            return ((float) team2Sr) / ((float) team1Sr);
        }
    }

    private int calcTeamPrimaryPosition(final int offset, @Nonnull final int[] idArray, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        return getUserPositionPreference(0, idArray[offset], userInfoMap) +
                getUserPositionPreference(0, idArray[offset + 1], userInfoMap) +
                getUserPositionPreference(1, idArray[offset + 2], userInfoMap) +
                getUserPositionPreference(1, idArray[offset + 3], userInfoMap) +
                getUserPositionPreference(2, idArray[offset + 4], userInfoMap) +
                getUserPositionPreference(2, idArray[offset + 5], userInfoMap);
    }

    private int getUserPositionPreference(final int position, final int id, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        if (id == 0) {
            return 0;
        } else {
            return getUserInfoPositionPreference(id, position, userInfoMap.get(id));
        }
    }

    private int calcTeamSr(final int offset, @Nonnull final int[] idArray, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        return getUserSr(0, idArray[offset], userInfoMap) +
                getUserSr(0, idArray[offset + 1], userInfoMap) +
                getUserSr(1, idArray[offset + 2], userInfoMap) +
                getUserSr(1, idArray[offset + 3], userInfoMap) +
                getUserSr(2, idArray[offset + 4], userInfoMap) +
                getUserSr(2, idArray[offset + 5], userInfoMap);
    }

    private int getUserSr(final int position, final int id, @Nonnull final Map<Integer, UserInfo> userInfoMap) {
        if (id == 0) {
            return 0;
        } else {
            return getUserInfoSr(id, position, userInfoMap.get(id));
        }
    }

    private int getUserInfoPositionPreference(final int id, final int position, @Nonnull final UserInfo userInfo) {
        if (position == BalancedPlayer.TANK_POSITION) {
            return userInfo.tankPreference;
        } else if (position == BalancedPlayer.DPS_POSITION) {
            return userInfo.dpsPreference;
        } else {
            return userInfo.supportPreference;
        }
    }

    private int getUserInfoSr(final int id, final int position, @Nonnull final UserInfo userInfo) {
        if (position == BalancedPlayer.TANK_POSITION) {
            return userInfo.tankSr;
        } else if (position == BalancedPlayer.DPS_POSITION) {
            return userInfo.dpsSr;
        } else {
            return userInfo.supportSr;
        }
    }
}
