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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperator;
import dev.morphia.query.experimental.updates.UpdateOperators;
import net.reallifegames.ow.models.PlayerModel;
import net.reallifegames.ow.models.SettingModel;
import net.reallifegames.ow.models.UserInviteModel;
import net.reallifegames.ow.models.UserModel;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class MongoDbModule implements DbModule {

    /**
     * A static class to hold the singleton.
     */
    private static final class SingletonHolder {

        /**
         * The Sql module singleton.
         */
        private static final MongoDbModule INSTANCE = new MongoDbModule();
    }

    /**
     * @return {@link MongoDbModule} singleton.
     */
    public static MongoDbModule getInstance() {
        return MongoDbModule.SingletonHolder.INSTANCE;
    }

    private final MongoClient mongoClient;
    private final Datastore datastore;

    public MongoDbModule() {
        mongoClient = MongoClients.create(Balancer.getConfig().getMongoConnectionUri());
        datastore = Morphia.createDatastore(mongoClient, Balancer.getConfig().getMongoDatabaseName());
        datastore.getMapper().mapPackage("net.reallifegames.ow.models");
        datastore.ensureIndexes();
    }

    @Override
    @Nullable
    public UserModel getUserModelByEmail(@NotNull final String email) {
        return datastore.find(UserModel.class)
                .filter(Filters.eq("email", email))
                .iterator()
                .tryNext();
    }

    @Override
    @Nullable
    public UserModel getUserModelByUsername(@NotNull final String username) {
        return datastore.find(UserModel.class)
                .filter(Filters.eq("username", username))
                .iterator()
                .tryNext();
    }

    @Nullable
    @Override
    public UserInviteModel getUserInviteModelByEmail(@NotNull final String email) {
        return datastore.find(UserInviteModel.class)
                .filter(Filters.eq("email", email))
                .iterator()
                .tryNext();
    }

    @Override
    public boolean createNewUser(@NotNull final String email,
                                 @NotNull final String username,
                                 @NotNull final String password) {
        final boolean isFirstUser = datastore.find(UserModel.class).count() == 0;
        final UserInviteModel userInviteModel = getUserInviteModelByEmail(email);

        final List<Integer> permList = isFirstUser ?
                Arrays.asList(Permissions.IS_USER_SUPER_ADMIN.value) : userInviteModel == null ?
                new ArrayList<>() : userInviteModel.permissions;
        if (getUserModelByEmail(email) == null) {
            if (Balancer.getSettingsModule().getAccountCreationType().value.equals("invite") && !isFirstUser) {
                if (userInviteModel == null) {
                    return false;
                }
            }
            datastore.save(new UserModel(
                    email,
                    username,
                    BCrypt.hashpw(password, BCrypt.gensalt()),
                    isFirstUser || (userInviteModel != null),
                    permList,
                    new ArrayList<>()
            ));
            if (userInviteModel != null) {
                datastore.delete(userInviteModel);
            }
        } else {
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public SettingModel getSettingByName(@Nonnull final String name) {
        return datastore.find(SettingModel.class)
                .filter(Filters.eq("name", name))
                .iterator()
                .tryNext();
    }

    @Override
    public boolean createSetting(@NotNull final SettingModel settingModel, final boolean createIfNotExists) {
        if (createIfNotExists) {
            final SettingModel dbModel = getSettingByName(settingModel.name);
            if (dbModel != null) {
                return true;
            }
        }
        datastore.save(settingModel);
        return true;
    }

    @Override
    public boolean createNewInvite(@NotNull final String email,
                                   @NotNull final List<Integer> permissions) {
        datastore.save(new UserInviteModel(email, permissions));
        return true;
    }

    @Override
    public List<UserInviteModel> getAllUserInviteModels() {
        return datastore.find(UserInviteModel.class)
                .iterator()
                .toList();
    }

    @Override
    public boolean updateInvite(@NotNull final String email, @NotNull final String newEmail, @Nullable final List<Integer> permissions) {
        final UserInviteModel userInviteModel = getUserInviteModelByEmail(newEmail);
        if (userInviteModel == null) {
            final List<UpdateOperator> operators = new ArrayList<>();
            if (permissions != null) {
                operators.add(UpdateOperators.set("permissions", permissions));
            }
            return datastore.find(UserInviteModel.class)
                    .filter(Filters.eq("email", email))
                    .update(UpdateOperators.set("email", newEmail), operators.toArray(new UpdateOperator[0]))
                    .execute().wasAcknowledged();

        }
        return false;
    }

    @Override
    public boolean deleteInvite(@Nonnull final String email) {
        final UserInviteModel userInviteModel = getUserInviteModelByEmail(email);
        if (userInviteModel != null) {
            return datastore.delete(userInviteModel).wasAcknowledged();
        }
        return true;
    }

    @Nonnull
    @Override
    public List<UserModel> getAllUserModels() {
        return datastore.find(UserModel.class)
                .iterator()
                .toList();
    }

    @Override
    public boolean updateUserModel(@Nonnull final String email,
                                   @Nonnull final String username,
                                   @Nonnull final String password,
                                   final boolean active,
                                   @Nullable final List<Integer> permissions) {
        final List<UpdateOperator> operators = new ArrayList<>();
        final UserModel userModel = getUserModelByEmail(email);
        if (userModel != null && !userModel.permissions.contains(Permissions.IS_USER_SUPER_ADMIN.value)) {
            operators.add(UpdateOperators.set("active", active));
            if (permissions != null) {
                operators.add(UpdateOperators.set("permissions", permissions));
            }
        }
        if (!password.equals("")) {
            operators.add(UpdateOperators.set("passwordHash", BCrypt.hashpw(password, BCrypt.gensalt())));
        }
        return datastore.find(UserModel.class)
                .filter(Filters.eq("email", email))
                .update(UpdateOperators.set("username", username), operators.toArray(new UpdateOperator[0]))
                .execute().wasAcknowledged();
    }

    @Override
    public boolean deleteUser(@NotNull String email) {
        final UserModel userModel = getUserModelByEmail(email);
        if (userModel != null) {
            return datastore.delete(userModel).wasAcknowledged();
        }
        return true;
    }

    @Override
    public boolean createNewPlayer(@Nonnull final String userId,
                                   @Nonnull final String playerName,
                                   final int tankPreference,
                                   final int supportPreference,
                                   final int dpsPreference,
                                   final int tankSr,
                                   final int supportSr,
                                   final int dpsSr,
                                   @Nonnull final String[] names,
                                   final boolean useUsername) {
        final UserModel userModel = useUsername ? getUserModelByUsername(userId) : getUserModelByEmail(userId);
        if (userModel != null) {
            String uuid;
            Optional<PlayerModel> lastPlayerModel;
            do {
                uuid = UUID.randomUUID().toString();
                final String fUuid = uuid;
                lastPlayerModel = datastore.find(UserModel.class)
                        .filter(Filters.eq("players.uuid", uuid))
                        .iterator(new FindOptions().projection().include("players"))
                        .toList().stream().map(model->model.players).flatMap(List::stream).filter(playerModel->playerModel.uuid.equals(fUuid)).findFirst();
            } while (lastPlayerModel.isPresent());
            userModel.players.add(new PlayerModel(uuid, playerName, tankPreference, supportPreference, dpsPreference, tankSr, supportSr, dpsSr, names));
            datastore.save(userModel);
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public List<PlayerModel> getAllPlayerModels() {
        return datastore.find(UserModel.class)
                .iterator(new FindOptions().projection().include("players"))
                .toList().stream().map(userModel->userModel.players).flatMap(List::stream).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<PlayerModel> getPlayerModelsFromUuids(@Nonnull final List<String> uuids) {
        return datastore.find(UserModel.class)
                .iterator(new FindOptions().projection().include("players"))
                .toList().stream().map(userModel->userModel.players).flatMap(List::stream).filter(playerModel->uuids.contains(playerModel.uuid)).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public PlayerModel getPlayerModel(@Nonnull final String uuid) {
        final UserModel userModel = datastore.find(UserModel.class)
                .filter(Filters.eq("players.uuid", uuid))
                .iterator(new FindOptions().projection().include("players"))
                .tryNext();
        if (userModel != null) {
            for (final PlayerModel playerModel : userModel.players) {
                if (playerModel.uuid.equals(uuid)) {
                    return playerModel;
                }
            }
        }
        return null;
    }

    @Override
    public boolean updatePlayer(@Nonnull final String uuid,
                                @Nonnull final String playerName,
                                final int tankPreference,
                                final int supportPreference,
                                final int dpsPreference,
                                final int tankSr,
                                final int supportSr,
                                final int dpsSr,
                                @Nonnull final String[] names) {
        final Optional<UserModel> optionalUserModel = datastore.find(UserModel.class)
                .filter(Filters.eq("players.uuid", uuid))
                .iterator()
                .toList().stream().findFirst();

        if (optionalUserModel.isEmpty()) {
            return false;
        }
        final UserModel model = optionalUserModel.get();
        model.players.removeIf(playerModel->playerModel.uuid.equals(uuid));
        model.players.add(new PlayerModel(
                uuid,
                playerName,
                tankPreference,
                supportPreference,
                dpsPreference,
                tankSr,
                supportSr,
                dpsSr,
                names
        ));
        datastore.save(model);
        return true;
    }

    @Override
    public boolean deletePlayer(final @NotNull String uuid) {
        final Optional<UserModel> optionalUserModel = datastore.find(UserModel.class)
                .filter(Filters.eq("players.uuid", uuid))
                .iterator()
                .toList().stream().findFirst();

        if (optionalUserModel.isEmpty()) {
            return false;
        }
        final UserModel model = optionalUserModel.get();
        model.players.removeIf(playerModel->playerModel.uuid.equals(uuid));
        datastore.save(model);
        return true;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
