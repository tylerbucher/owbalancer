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
import com.fasterxml.jackson.databind.SerializationFeature;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.staticfiles.Location;
import net.reallifegames.ow.api.v1.ApiController;
import net.reallifegames.ow.api.v1.balance.BalanceController;
import net.reallifegames.ow.api.v1.users.UsersController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;

public class Balancer {
    /**
     * The static logger for the application.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(Balancer.class);

    /**
     * The global json factory.
     */
    public static final JsonFactory jsonFactory = new JsonFactory();

    /**
     * Object mapper for json marshalling.
     */
    public static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The database jdbc type.
     */
    private static String JDBC_TYPE = "";

    /**
     * The database jdbc url.
     */
    private static String JDBC_URL = "";

    /**
     * The website domain.
     */
    public static String DOMAIN = "";

    /**
     * The JWT secretKey auto initialize.
     */
    private static boolean SECRET_KEY_AUTO = true;

    /**
     * The JWT secretKey.
     */
    private static SecretKey SECRET_KEY;

    /**
     * The amount of time in the future the token will expire.
     */
    public static long JWT_EXPIRE_TIME = 604800000L;

    /**
     * Static db module reference.
     */
    private static DbModule DB_MODULE;

    /**
     * Static list of hex characters.
     */
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes();

    /**
     * Main class for the Local Auth application.
     *
     * @param args the program arguments to run with.
     */
    public static void main(@Nonnull final String[] args) {
        Balancer.JDBC_TYPE = System.getenv("JDBC_TYPE");
        Balancer.JDBC_URL = System.getenv("JDBC_URL");
        Balancer.DOMAIN = System.getenv("DOMAIN");
        Balancer.objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        try {
            Balancer.DB_MODULE = Balancer.findDbModule(Balancer.JDBC_TYPE);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Error loading sq lite driver.", e);
        }
        // Create tables
        Balancer.DB_MODULE.createTables();
        // Set spark port
        final Javalin javalinApp = Javalin.create(config->{
            config.addStaticFiles(System.getProperty("user.dir") + "/public", Location.EXTERNAL);
            config.addSinglePageRoot("/", System.getProperty("user.dir") + "/public/" + "index.html", Location.EXTERNAL);
        });
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
            // Root api path controller
            ApiBuilder.get("/", ApiController::getApiInformation);
            // List of users api controller
            ApiBuilder.get("/users", UsersController::getUsers);
            ApiBuilder.post("/balance", BalanceController::postBalance);
        }));

        javalinApp.start(8080);
    }

    /**
     * @param hexString the string to convert.
     * @return the byte array for a hex string.
     */
    private static byte[] hexToBytes(@Nonnull final String hexString) {
        final int length = hexString.length();
        final byte[] byteArray = new byte[length / 2];
        for (int i = 0, j = 0; i < length; i += 2, j++) {
            byteArray[i / 2] = (byte) ((charToBase16(hexString.charAt(i)) << 4) + charToBase16(hexString.charAt(i + 1)));
        }
        return byteArray;
    }

    /**
     * Quick converts a valid base 16 character to an int.
     *
     * @param c the character to convert.
     * @return the character as a base 16 number.
     */
    private static int charToBase16(final char c) {
        return c < 58 ? c - 48 : c - 55;
    }

    private static DbModule findDbModule(@Nonnull final String key) throws ClassNotFoundException {
        switch (key) {
            case "mysql":
                return MySqlModule.getInstance();
            default:
                Class.forName("org.sqlite.JDBC");
                return SqLiteModule.getInstance();
        }
    }

    /**
     * @return the current database module instance.
     */
    public static DbModule getDbModule() {
        return DB_MODULE;
    }

    /**
     * @return the database jdbc url.
     */
    public static String getJdbcUrl() {
        return JDBC_URL;
    }
}
