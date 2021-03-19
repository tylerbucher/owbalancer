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
package net.reallifegames.ow;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import net.reallifegames.ow.api.v1.ApiController;
import net.reallifegames.ow.api.v1.authentication.get.AuthenticationGetController;
import net.reallifegames.ow.api.v1.authentication.post.AuthenticationPostController;
import net.reallifegames.ow.api.v1.balance.BalanceController;
import net.reallifegames.ow.api.v1.invites.delete.InviteDeleteController;
import net.reallifegames.ow.api.v1.invites.get.InviteGetController;
import net.reallifegames.ow.api.v1.invites.patch.InvitePatchController;
import net.reallifegames.ow.api.v1.invites.post.InvitePostController;
import net.reallifegames.ow.api.v1.permissions.get.PermissionsGetController;
import net.reallifegames.ow.api.v1.players.PlayersController;
import net.reallifegames.ow.api.v1.players.delete.PlayersDeleteController;
import net.reallifegames.ow.api.v1.players.get.PlayersGetController;
import net.reallifegames.ow.api.v1.players.patch.PlayersPatchController;
import net.reallifegames.ow.api.v1.players.post.PlayersPostController;
import net.reallifegames.ow.api.v1.users.UsersController;
import net.reallifegames.ow.api.v1.users.delete.UsersDeleteController;
import net.reallifegames.ow.api.v1.users.get.UsersGetController;
import net.reallifegames.ow.api.v1.users.patch.UsersPatchController;
import net.reallifegames.ow.api.v1.users.post.UsersPostController;

import javax.annotation.Nonnull;

public class Balancer {

    /**
     * The global json factory.
     */
    public static final JsonFactory jsonFactory = new JsonFactory();

    /**
     * Object mapper for json marshalling.
     */
    public static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Static db module reference.
     */
    private static DbModule DB_MODULE;

    /**
     * Static db module reference.
     */
    private static SecurityModule SECURITY_MODULE;

    /**
     * Static db settings module reference.
     */
    private static SettingsModule SETTINGS_MODULE;

    /**
     * Global application configuration
     */
    private static Config CONFIG;

    /**
     * Main class for the Local Auth application.
     *
     * @param args the program arguments to run with.
     */
    public static void main(@Nonnull final String[] args) {
        Balancer.CONFIG = new Config();
        Balancer.DB_MODULE = MongoDbModule.getInstance();
        Balancer.SECURITY_MODULE = SecurityModule.getInstance();
        Balancer.SETTINGS_MODULE = new SettingsModule(DB_MODULE);
        final Javalin javalinApp = Javalin.create();
        // CORS information
        javalinApp.before("*/*", (context)->{
            context.header("Access-Control-Allow-Origin", "*");
            context.header("Access-Control-Allow-Methods", "POST, GET, PATCH, OPTIONS");
            context.header("Access-Control-Allow-Headers", "*");
            context.header("Access-Control-Request-Headers", "*");
            context.header("Access-Control-Allow-Credentials", "true");
        });
        // Api v1 pathing group
        javalinApp.routes(()->ApiBuilder.path("/api/v1", ()->{
            ApiBuilder.get("/", ApiController::getApiInformation);
            ApiBuilder.get("/authentication", AuthenticationGetController::getAuthentication);
            ApiBuilder.post("/authentication", AuthenticationPostController::postAuthentication);

            ApiBuilder.get("/users", UsersController::getUsers);
            ApiBuilder.get("/users/*", UsersGetController::getUsers);
            ApiBuilder.post("/users", UsersPostController::postUser);
            ApiBuilder.patch("/users", UsersPatchController::patchUser);
            ApiBuilder.delete("/users/*", UsersDeleteController::deleteUser);

            ApiBuilder.get("/permissions", PermissionsGetController::getPermissions);

            ApiBuilder.get("/invites", InviteGetController::getInvites);
            ApiBuilder.post("/invites", InvitePostController::postInvite);
            ApiBuilder.patch("/invites", InvitePatchController::patchInvite);
            ApiBuilder.delete("/invites/*", InviteDeleteController::deleteInvite);

            ApiBuilder.get("/players", PlayersGetController::getPlayers);
            ApiBuilder.get("/players/*", PlayersController::getPlayer);
            ApiBuilder.post("/players", PlayersPostController::postNewPlayer);
            ApiBuilder.patch("/players", PlayersPatchController::patchPlayer);
            ApiBuilder.delete("/players/*", PlayersDeleteController::deletePlayer);

            ApiBuilder.post("/balance", BalanceController::postBalance);
        }));
        javalinApp.start(8080);
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            DB_MODULE.close();
        }));
    }

    /**
     * @return the current database module instance.
     */
    public static DbModule getDbModule() {
        return DB_MODULE;
    }

    public static SecurityModule getSecurityModule() {
        return SECURITY_MODULE;
    }

    public static Config getConfig() {
        return CONFIG;
    }

    public static SettingsModule getSettingsModule() {
        return SETTINGS_MODULE;
    }
}
