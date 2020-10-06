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
package net.reallifegames.ow.api.v1.datas.get;

import io.javalin.http.Context;
import net.reallifegames.ow.Balancer;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.api.v1.ApiController;

import javax.annotation.Nonnull;
import java.io.IOException;

public class ExportDataController {

    /**
     * Returns all the data in this application.
     *
     * @param context the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    public static void getApplicationData(@Nonnull final Context context) throws IOException {
        getApplicationData(context, Balancer.getDbModule());
    }

    /**
     * Returns all the data in this application.
     *
     * @param context the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    @SuppressWarnings ("Duplicates")
    public static void getApplicationData(@Nonnull final Context context, @Nonnull DbModule dbModule) throws IOException {
        // Set response status
        context.status(200);
        // Prep Jackson-JSON
        ApiController.jsonContextResponse(new ExportDataResponse(dbModule.getAllUserTableData(), dbModule.getAllUsersNames()), context);
    }
}
