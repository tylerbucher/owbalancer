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

import net.reallifegames.ow.models.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A module to handle all database functions in sql.
 *
 * @author Tyler Bucher
 */
public abstract class SqlModule implements DbModule {

    /**
     * The static logger for this version of the api.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SqlModule.class);

    /**
     * Sql query for getting all users.
     */
    private static final String QUERY_USER_LIST = "SELECT * FROM `alt_user_names`;";

    /**
     * Sql query for checking if a user exists.
     */
    private static final String QUERY_GET_USER_INFO = "SELECT * FROM `user_info` WHERE `id` IN ";

    @Override
    public void createTables(@Nonnull final String... tableStatements) {
        try (final Connection connection = getConnection()) {
            for (final String statement : tableStatements) {
                final PreparedStatement queryStatement = connection.prepareStatement(statement);
                queryStatement.execute();
                queryStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UserInfo> getUserResponses(final int[] userIds) {
        final List<UserInfo> userInfoList = new ArrayList<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(this.getUserInfoSqlQuery(userIds));
            // Execute update
            final ResultSet resultSet = queryStatement.executeQuery();
            while (resultSet.next()) {
                userInfoList.add(new UserInfo(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("tank_pref"),
                        resultSet.getInt("support_pref"),
                        resultSet.getInt("dps_pref"),
                        resultSet.getInt("tank_sr"),
                        resultSet.getInt("support_sr"),
                        resultSet.getInt("dps_sr")
                ));
            }
            resultSet.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user information sql error.", e);
        }
        return userInfoList;
    }

    private String getUserInfoSqlQuery(final int[] userIds) {
        final StringBuilder builder = new StringBuilder(QUERY_GET_USER_INFO);
        builder.append("(");
        for (final int id : userIds) {
            builder.append(id);
            builder.append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(')');
        return builder.toString();
    }

    @Override
    public List<Map.Entry<Integer, String>> getUserList() {
        final List<Map.Entry<Integer, String>> userList = new ArrayList<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_LIST);
            final ResultSet resultSet = queryStatement.executeQuery();
            while (resultSet.next()) {
                userList.add(new AbstractMap.SimpleEntry<>(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                ));
            }
            resultSet.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user list sql error.", e);
        }
        return userList;
    }

    /**
     * @return a sql db connection.
     *
     * @throws SQLException if a connection to a db was able to be obtained.
     */
    abstract Connection getConnection() throws SQLException;
}
