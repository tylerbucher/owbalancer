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
package net.reallifegames.ow.api.v1.users.get;

import io.javalin.http.Context;
import net.reallifegames.ow.Balancer;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.Permissions;
import net.reallifegames.ow.SecurityModule;
import net.reallifegames.ow.api.v1.ApiController;
import net.reallifegames.ow.models.UserModel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Attempts to return the list of users in this application.
 *
 * @author Tyler Bucher
 */
public class UsersGetController {

    /**
     * Default list of permissions for this endpoint.
     */
    private static final List<Integer> PERMISSIONS = Arrays.asList(
            Permissions.IS_USER_SUPER_ADMIN.value,
            Permissions.IS_USER_ADMIN.value,
            Permissions.CAN_USER_MOD_USERS.value,
            Permissions.CAN_USER_DELETE_USERS.value,
            Permissions.CAN_USER_ADD_PLAYER.value
    );

    /**
     * Admin list of permissions for this endpoint.
     */
    private static final List<Integer> ADMIN_PERMISSIONS = Arrays.asList(
            Permissions.IS_USER_SUPER_ADMIN.value,
            Permissions.IS_USER_ADMIN.value,
            Permissions.CAN_USER_MOD_USERS.value,
            Permissions.CAN_USER_DELETE_USERS.value
    );

    /**
     * Attempts to create a new user from the post data.
     *
     * @param context the REST request context to modify.
     */
    public static void getUsers(@Nonnull final Context context) throws Exception {
        getUsers(context, Balancer.getDbModule(), Balancer.getSecurityModule());
    }

    /**
     * Attempts to create a new user from the post data.
     *
     * @param context        the REST request context to modify.
     * @param dbModule       the module instance to use.
     * @param securityModule the module instance to use.
     */
    public static void getUsers(@Nonnull final Context context,
                                @Nonnull final DbModule dbModule,
                                @Nonnull final SecurityModule securityModule) throws Exception {
        final UserModel userModel = ApiController.beforeApiAuthentication(context, dbModule, securityModule, PERMISSIONS);
        // Set the response type
        final boolean canSeeEmail = userModel.hasPermission(ADMIN_PERMISSIONS);
        ApiController.jsonContextResponse(new UsersGetResponse(convertUserModelList(dbModule.getAllUserModels(), canSeeEmail)), context);
    }

    /**
     * @param userModels the list to be converted.
     * @return the new converted SafeUserModel list.
     */
    private static List<SafeUserModel> convertUserModelList(@Nonnull final List<UserModel> userModels, final boolean canSeeEmails) {
        final List<SafeUserModel> safeUserModels = new ArrayList<>();
        userModels.forEach(userModel->safeUserModels.add(SafeUserModel.fromUserModel(userModel, canSeeEmails)));
        return safeUserModels;
    }
}
