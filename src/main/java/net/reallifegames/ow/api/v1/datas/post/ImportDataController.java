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
package net.reallifegames.ow.api.v1.datas.post;

import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.http.Context;
import net.reallifegames.ow.Balancer;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.api.v1.ApiController;
import net.reallifegames.ow.api.v1.balance.BalanceRequest;
import net.reallifegames.ow.api.v1.datas.get.ExportDataResponse;
import net.reallifegames.ow.models.UserInfoTableModel;
import net.reallifegames.ow.models.UserNamesTableModel;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImportDataController {

    private static final ImportDataResponse successResponse = new ImportDataResponse(ApiController.apiResponse, "success");

    /**
     * Returns all the data in this application.
     *
     * @param context the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    public static void postApplicationData(@Nonnull final Context context) throws IOException {
        postApplicationData(context, Balancer.getDbModule());
    }

    /**
     * Returns all the data in this application.
     *
     * @param context the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    @SuppressWarnings ("Duplicates")
    public static void postApplicationData(@Nonnull final Context context, @Nonnull final DbModule dbModule) throws IOException {
        // Set the response type
        final ImportDataRequest dataRequest;
        try {
            final JsonNode node = Balancer.objectMapper.readTree(context.body());
            final Iterator<JsonNode> itr = node.path("userInfo").elements();
            final Iterator<JsonNode> itr2 = node.path("userNames").elements();

            final List<UserInfoTableModel> userInfoTableModelList = new ArrayList<>();
            while (itr.hasNext()) {
                userInfoTableModelList.add(Balancer.objectMapper.readValue(itr.next().toString(), UserInfoTableModel.class));
            }
            final List<UserNamesTableModel> userNamesTableModelList = new ArrayList<>();
            while (itr2.hasNext()) {
                final String str = itr2.next().toString();
                userNamesTableModelList.add(Balancer.objectMapper.readValue(str, UserNamesTableModel.class));
            }
            dataRequest = new ImportDataRequest(userInfoTableModelList, userNamesTableModelList);
        } catch (IOException e) {
            ApiController.LOGGER.debug("Api data import controller request marshall error", e);
            context.status(400);
            context.result("Bad Request");
            return;
        }
        if(dataRequest.importData(dbModule)) {
            context.status(200);
            ApiController.jsonContextResponse(successResponse, context);
        } else {
            context.status(406);
            context.result("Not Acceptable");
        }
    }
}
