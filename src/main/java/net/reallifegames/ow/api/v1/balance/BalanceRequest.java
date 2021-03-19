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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.api.v1.ApiResponse;
import net.reallifegames.ow.balancer.TieredBalancer;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A user request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class BalanceRequest {

    /**
     * The username to check the password for.
     */
    private final List<String> users;

    /**
     * Constructor for Jackson json marshalling.
     *
     * @param users the requested login username.
     */
    @JsonCreator
    public BalanceRequest(@Nonnull @JsonProperty ("userIds") final List<String> users) {
        this.users = users;
    }

    /**
     * @return true if the there are less than 13 users and more than 1, false otherwise.
     */
    public boolean validate() {
        return users.size() > 1 && users.size() < 13;
    }

    /**
     * @param dbModule the module instance to use.
     * @return a user response for a user or null if an error.
     */
    public BalanceResponse getBalanceResponse(@Nonnull final ApiResponse apiResponse, @Nonnull final DbModule dbModule) {
        final TieredBalancer.TieredBalancerResponse tieredBalancerResponse = (new TieredBalancer()).balancePlayers(dbModule, users);
        return new BalanceResponse(apiResponse, tieredBalancerResponse.userInfoLists, tieredBalancerResponse.balanceInspectorList);
    }
}
