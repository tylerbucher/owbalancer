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
package net.reallifegames.ow.api.v1.authentication.post;

import io.javalin.http.Context;
import net.reallifegames.ow.Balancer;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.SecurityModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;
import java.io.IOException;

/**
 * Attempts to log a client in and return them a authentication cookie used for accessing secure endpoints.
 *
 * @author Tyler Bucher
 */
public class AuthenticationPostController {

    /**
     * The static logger for this version of the api.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationPostController.class);

    /**
     * Attempts to create a new user from the post data.
     *
     * @param context the REST request context to modify.
     */
    public static void postAuthentication(@Nonnull final Context context) {
        postAuthentication(context, Balancer.getDbModule(), Balancer.getSecurityModule());
    }

    /**
     * Attempts to create a new user from the post data.
     *
     * @param context  the REST request context to modify.
     * @param dbModule the module instance to use.
     */
    public static void postAuthentication(@Nonnull final Context context,
                                          @Nonnull final DbModule dbModule,
                                          @Nonnull final SecurityModule securityModule) {
        // Set the response type
        final AuthenticationPostRequest postRequest;
        try {
            postRequest = Balancer.objectMapper.readValue(context.body(), AuthenticationPostRequest.class);
        } catch (IOException e) {
            LOGGER.debug("postAuthentication: ", e);
            context.status(400);
            context.result("Bad Request");
            return;
        }
        if (!postRequest.validate(dbModule)) {
            context.status(406);
            context.result("Not Acceptable");
        } else {
            setAuthToken(context, postRequest, securityModule);
            setHasAuthToken(context, postRequest, securityModule);

            context.status(200);
            context.result("Success");
        }
    }

    /**
     * Sets the login auth token information.
     *
     * @param context        the REST request context to modify.
     * @param postRequest    the module instance to use.
     * @param securityModule the module instance to use.
     */
    private static void setAuthToken(@Nonnull final Context context,
                                     @Nonnull final AuthenticationPostRequest postRequest,
                                     @Nonnull final SecurityModule securityModule) {
        final String token = postRequest.generateToken(securityModule);
        final Cookie cookie = new Cookie("cgbAuthToken", token);
        cookie.setDomain(Balancer.getConfig().getDomain());
        cookie.setPath("/");
        cookie.setMaxAge(postRequest.rememberMe ? (int) Balancer.getConfig().getJwtExpireTime() : -1);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        context.cookie(cookie);
    }

    /**
     * Sets the has auth token information.
     *
     * @param context        the REST request context to modify.
     * @param postRequest    the module instance to use.
     * @param securityModule the module instance to use.
     */
    private static void setHasAuthToken(@Nonnull final Context context,
                                        @Nonnull final AuthenticationPostRequest postRequest,
                                        @Nonnull final SecurityModule securityModule) {
        final Cookie cookie = new Cookie("cgbHasAuthToken", "true");
        cookie.setDomain(Balancer.getConfig().getDomain());
        cookie.setPath("/");
        cookie.setMaxAge(postRequest.rememberMe ? (int) Balancer.getConfig().getJwtExpireTime() : -1);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        context.cookie(cookie);
    }
}
