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
package net.reallifegames.ow.api.v1.users.post;

import io.javalin.http.Context;
import net.reallifegames.ow.Balancer;
import net.reallifegames.ow.DbModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Controller for handling the creation of new users.
 *
 * @author Tyler Bucher
 */
public class UsersPostController {

    /**
     * The static logger for this version of the api.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(UsersPostController.class);

    /**
     * Attempts to create a new user from the post data.
     *
     * @param context the REST request context to modify.
     */
    public static void postUser(@Nonnull final Context context) {
        postUser(context, Balancer.getDbModule());
    }

    /**
     * Attempts to create a new user from the post data.
     *
     * @param context  the REST request context to modify.
     * @param dbModule the module instance to use.
     */
    public static void postUser(@Nonnull final Context context,
                                @Nonnull final DbModule dbModule) {
        final UsersPostRequest postRequest;
        try {
            postRequest = Balancer.objectMapper.readValue(context.body(), UsersPostRequest.class);
        } catch (IOException e) {
            LOGGER.debug("postUser: ", e);
            context.status(400);
            context.result("Bad Request");
            return;
        }
        if (!postRequest.validate()) {
            context.status(406);
            context.result("Not Acceptable");
            return;
        }
        if (!postRequest.createNewUser(dbModule)) {
            context.status(409);
            context.result("Conflict");
        } else {
            context.status(200);
            context.result("Success");
        }
    }
}
