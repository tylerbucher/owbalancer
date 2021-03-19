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
package net.reallifegames.ow.api.v1.invites.post;

import io.javalin.http.Context;
import net.reallifegames.ow.Balancer;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.Permissions;
import net.reallifegames.ow.SecurityModule;
import net.reallifegames.ow.api.v1.ApiController;
import net.reallifegames.ow.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Attempts to create a new invite.
 *
 * @author Tyler Bucher
 */
public class InvitePostController {

    /**
     * The static logger for this version of the api.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(InvitePostController.class);

    /**
     * Default list of permissions for this endpoint.
     */
    private static final List<Integer> PERMISSIONS = Arrays.asList(
            Permissions.IS_USER_ADMIN.value,
            Permissions.IS_USER_SUPER_ADMIN.value,
            Permissions.CAN_USER_INVITE.value
    );

    /**
     * Attempts to create a new user from the post data.
     *
     * @param context the REST request context to modify.
     */
    public static void postInvite(@Nonnull final Context context) {
        postInvite(context, Balancer.getDbModule(), Balancer.getSecurityModule());
    }

    /**
     * Attempts to create a new user from the post data.
     *
     * @param context        the REST request context to modify.
     * @param dbModule       the module instance to use.
     * @param securityModule the module instance to use.
     */
    public static void postInvite(@Nonnull final Context context,
                                  @Nonnull final DbModule dbModule,
                                  @Nonnull final SecurityModule securityModule) {
        final UserModel userModel = ApiController.beforeApiAuthentication(context, dbModule, securityModule, PERMISSIONS);
        // Set the response type
        final InvitePostRequest postRequest;
        try {
            postRequest = Balancer.objectMapper.readValue(context.body(), InvitePostRequest.class);
        } catch (IOException e) {
            LOGGER.debug("postInvite: ", e);
            context.status(400);
            context.result("Bad Request");
            return;
        }
        if (!postRequest.validate()) {
            context.status(406);
            context.result("Not Acceptable");
            return;
        }
        if (!postRequest.createNewInvite(dbModule, userModel.hasPermission(Permissions.ADMIN_PERMISSIONS))) {
            context.status(409);
            context.result("Conflict");
        } else {
            context.status(200);
            context.result("Success");
        }
    }
}