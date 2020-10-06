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
import net.reallifegames.ow.models.UserInfoTableModel;
import net.reallifegames.ow.models.UserNamesTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A module to handle all database functions in sql.
 *
 * @author Tyler Bucher
 */
public abstract class SqlModule implements DbModule {

    public static final Logger LOGGER = LoggerFactory.getLogger(SqlModule.class);

    private static final String QUERY_USER_LIST = "SELECT * FROM `alt_user_names`;";
    private static final String QUERY_USER_NAMES_FOR_ID = "SELECT `name` FROM `alt_user_names` WHERE `id` = ?;";
    private static final String DELETE_USER_NAMES_FOR_ID = "DELETE FROM `alt_user_names` WHERE `id` = ?;";
    private static final String QUERY_SINGLE_USER = "SELECT * FROM `user_info` WHERE `id`=?;";
    private static final String QUERY_GET_USER_INFO = "SELECT * FROM `user_info` WHERE `id` IN ";
    private static final String QUERY_USER_EXISTS = "SELECT `id` FROM `user_info` WHERE lower(`name`)=lower(?);";
    private static final String QUERY_USER_NAMES = "SELECT `id`, `name` FROM `user_info`;";
    private static final String INSERT_NEW_USER = "INSERT INTO `user_info`(`name`, `tank_pref`, `dps_pref`, `support_pref`, `tank_sr`, `dps_sr`, `support_sr`) VALUES (?,?,?,?,?,?,?);";
    private static final String INSERT_NEW_OW_NAME = "INSERT INTO `alt_user_names`(`id`, `name`) VALUES (?, ?);";
    private static final String UPDATE_USER_INFO = "UPDATE `user_info` SET `name`=?,`tank_pref`=?,`dps_pref`=?,`support_pref`=?,`tank_sr`=?,`dps_sr`=?,`support_sr`=? WHERE `id`=?;";
    private static final String DELETE_USER_INFO = "DELETE FROM `user_info` WHERE `id`=?;";
    private static final String QUERY_USER_DATA_LIST = "SELECT * FROM `user_info`;";

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

    /**
     * @param userIds the list of ids to make a query for.
     * @return a constructed query from user ids.
     */
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

    @Override
    public Map<Integer, String> getUserNameList() {
        final Map<Integer, String> userMap = new HashMap<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_NAMES);
            final ResultSet resultSet = queryStatement.executeQuery();
            while (resultSet.next()) {
                userMap.put(resultSet.getInt("id"), resultSet.getString("name"));
            }
            resultSet.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user list sql error.", e);
        }
        return userMap;
    }

    @Override
    public boolean userExists(@Nonnull final String username) {
        boolean returnResult = true;
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_EXISTS);
            queryStatement.setString(1, username);
            // Parse result information
            final ResultSet result = queryStatement.executeQuery();
            returnResult = result.next();
            result.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Check if user exists sql error", e);
        }
        return returnResult;
    }

    @Override
    public boolean createUser(@Nonnull final String username,
                              final int tankSr,
                              final int tankPreference,
                              final int dpsSr,
                              final int dpsPreference,
                              final int supportSr,
                              final int supportPreference) {
        // Prep user INSERT query
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(INSERT_NEW_USER);
            // Set query data
            queryStatement.setString(1, username);
            queryStatement.setInt(2, tankPreference);
            queryStatement.setInt(3, dpsPreference);
            queryStatement.setInt(4, supportPreference);
            queryStatement.setInt(5, tankSr);
            queryStatement.setInt(6, dpsSr);
            queryStatement.setInt(7, supportSr);
            // Execute insert statement
            queryStatement.executeUpdate();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Create user sql error", e);
            return false;
        }
        return true;
    }

    @Override
    public int getUserId(@Nonnull final String username) {
        int id = -1;
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_EXISTS);
            queryStatement.setString(1, username);
            // Parse result information
            final ResultSet result = queryStatement.executeQuery();
            if (result.next()) {
                id = result.getInt("id");
            }
            result.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user list sql error.", e);
        }
        return id;
    }

    @Override
    public boolean addOverwatchName(final int id, @Nonnull final String[] overwatchNames) {
        // Prep user INSERT query
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(INSERT_NEW_OW_NAME);
            // Set query data
            for (final String name : overwatchNames) {
                queryStatement.setInt(1, id);
                queryStatement.setString(2, name);
                queryStatement.execute();
            }
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Create user sql error", e);
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    public UserInfo getUserInfo(int id) {
        UserInfo userInfo = null;
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_SINGLE_USER);
            queryStatement.setInt(1, id);
            // Execute update
            final ResultSet resultSet = queryStatement.executeQuery();
            if (resultSet.next()) {
                userInfo = new UserInfo(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("tank_pref"),
                        resultSet.getInt("support_pref"),
                        resultSet.getInt("dps_pref"),
                        resultSet.getInt("tank_sr"),
                        resultSet.getInt("support_sr"),
                        resultSet.getInt("dps_sr")
                );
            }
            resultSet.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user information sql error.", e);
        }
        return userInfo;
    }

    @Override
    public List<String> getNameListForId(final int id) {
        final List<String> nameList = new ArrayList<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_NAMES_FOR_ID);
            queryStatement.setInt(1, id);
            final ResultSet resultSet = queryStatement.executeQuery();
            while (resultSet.next()) {
                nameList.add(resultSet.getString("name"));
            }
            resultSet.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user list sql error.", e);
        }
        return nameList;
    }

    @Override
    public boolean deleteOwNamesForId(int id) {
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(DELETE_USER_NAMES_FOR_ID);
            queryStatement.setInt(1, id);
            queryStatement.executeUpdate();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user list sql error.", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateUser(final int id,
                              @Nonnull final String username,
                              final int tankSr,
                              final int tankPreference,
                              final int dpsSr,
                              final int dpsPreference, final int supportSr,
                              final int supportPreference) {
        // Prep user INSERT query
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(UPDATE_USER_INFO);
            // Set query data
            queryStatement.setString(1, username);
            queryStatement.setInt(2, tankPreference);
            queryStatement.setInt(3, dpsPreference);
            queryStatement.setInt(4, supportPreference);
            queryStatement.setInt(5, tankSr);
            queryStatement.setInt(6, dpsSr);
            queryStatement.setInt(7, supportSr);
            queryStatement.setInt(8, id);
            // Execute insert statement
            queryStatement.executeUpdate();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Create user sql error", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean deletePlayer(final int id) {
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(DELETE_USER_INFO);
            queryStatement.setInt(1, id);
            queryStatement.execute();
            queryStatement.close();
            final PreparedStatement queryStatement2 = connection.prepareStatement(DELETE_USER_NAMES_FOR_ID);
            queryStatement2.setInt(1, id);
            queryStatement2.execute();
            queryStatement2.close();
        } catch (SQLException e) {
            LOGGER.debug("Delete user sql error", e);
            return false;
        }
        return true;
    }

    /**
     * @return the entire user table.
     */
    @Override
    public List<UserInfoTableModel> getAllUserTableData() {
        List<UserInfoTableModel> userInfoList = new ArrayList<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_DATA_LIST);
            // Execute update
            final ResultSet resultSet = queryStatement.executeQuery();
            while (resultSet.next()) {
                userInfoList.add(new UserInfoTableModel(
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

    /**
     * @return the entire user names table.
     */
    @Override
    public List<UserNamesTableModel> getAllUsersNames() {
        final List<UserNamesTableModel> userList = new ArrayList<>();
        try (final Connection connection = getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_LIST);
            final ResultSet resultSet = queryStatement.executeQuery();
            while (resultSet.next()) {
                userList.add(new UserNamesTableModel(
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
