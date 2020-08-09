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

import net.reallifegames.ow.api.v1.ApiResponse;
import net.reallifegames.ow.balancer.BalanceInspector;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A standard users api fetch response structure.
 *
 * @author Tyler Bucher
 */
public class BalanceResponse extends ApiResponse {

    /**
     * The list of users in this application.
     */
    public final List<List<BalancedPlayer>> userList;

    public final List<BalanceInspector> balancerMeta;

    /**
     * Response constructor for Jackson json marshalling.
     *
     * @param apiResponse the root api response.
     * @param userList    list of users in this application.
     */
    public BalanceResponse(@Nonnull final ApiResponse apiResponse, @Nonnull final List<List<BalancedPlayer>> userList, @Nonnull final List<BalanceInspector> balanceMeta) {
        super(apiResponse.version);
        this.userList = userList;
        this.balancerMeta = balanceMeta;
    }
}
