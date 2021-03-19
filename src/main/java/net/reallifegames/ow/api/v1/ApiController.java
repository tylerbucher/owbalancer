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
package net.reallifegames.ow.api.v1;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import net.reallifegames.ow.Balancer;
import net.reallifegames.ow.DbModule;
import net.reallifegames.ow.SecurityModule;
import net.reallifegames.ow.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * Base Api controller, handles initial authentication and api versioning responses.
 *
 * @author Tyler Bucher
 */
public class ApiController {

    /**
     * The static logger for this version of the api.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    /**
     * Global api version response.
     */
    public static final ApiResponse apiResponse = new ApiResponse("v1");

    /**
     * Should be called before all secure api end-points.
     *
     * @param context the REST request context to modify.
     */
    public static UserModel beforeApiAuthentication(@Nonnull final Context context,
                                               @Nonnull final DbModule dbModule,
                                               @Nonnull final SecurityModule securityModule,
                                               @Nonnull final List<Integer> permissions) {
        // Set response type
        context.contentType("application/json");
        // Check if user is authenticated
        final String email = securityModule.getJWSEmailClaim(context.cookie("cgbAuthToken"));
        final UserModel userModel = dbModule.getUserModelByEmail(email);
        if (email.equals("") || userModel == null || !userModel.hasPermission(permissions)) {
            context.status(401);
            context.result("Unauthorized");
            throw new UnauthorizedResponse("Unauthorized");
        }
        return userModel;
    }

    /**
     * Returns the current version of this api.
     *
     * @param context the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    @SuppressWarnings ("Duplicates")
    public static void getApiInformation(@Nonnull final Context context) throws IOException {
        // Set response status
        context.status(200);
        // Prep Jackson-JSON
        ApiController.jsonContextResponse(apiResponse, context);
    }

    /**
     * Pre packaged json response with an object.
     *
     * @param marshallObject the response to marshall.
     * @param context        the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    public static void jsonContextResponse(@Nonnull final Object marshallObject, @Nonnull final Context context) throws IOException {
        context.contentType("application/json");
        // Prep Jackson-JSON
        final SegmentedStringWriter stringWriter = new SegmentedStringWriter(Balancer.jsonFactory._getBufferRecycler());
        final JsonGenerator jsonGenerator = Balancer.jsonFactory.createGenerator(stringWriter);
        // Append api response
        Balancer.objectMapper.writeValue(jsonGenerator, marshallObject);
        jsonGenerator.flush();
        // Return payload
        context.result(stringWriter.getAndClear());
        jsonGenerator.close();
    }
}
