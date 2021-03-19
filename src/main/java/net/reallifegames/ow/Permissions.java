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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Permissions {

    public static class Permission {

        public final String name;
        public final String description;
        public final int value;

        private Permission(@Nonnull final String name, @Nonnull final String description, final int value) {
            this.name = name;
            this.description = description;
            this.value = value;
        }
    }

    public static final Permission IS_USER_SUPER_ADMIN = new Permission("Super Admin", "This user is the super admin. Or owner of this application.", 0);
    public static final Permission IS_USER_ADMIN = new Permission("Admin", "This user is an admin.", 1);
    public static final Permission CAN_USER_INVITE = new Permission("Invite", "This user can invite other users.", 2);
    public static final Permission CAN_USER_MOD_INVITE = new Permission("Modify Invite", "This user can modify invites of other users.", 3);
    public static final Permission CAN_USER_DELETE_INVITE = new Permission("Delete Invite", "This user can delete invites of other users.", 4);
    public static final Permission CAN_USER_MOD_USERS = new Permission("Modify User", "This user can modify other users.", 5);
    public static final Permission CAN_USER_DELETE_USERS = new Permission("Delete Users", "This user can delete other users.", 6);
    public static final Permission CAN_USER_BALANCE = new Permission("Balance Players", "Allows this user to use the balancer.", 7);
    public static final Permission CAN_USER_ADD_PLAYER = new Permission("Add Player", "Allows this user to add players to the balancer.", 8);
    public static final Permission CAN_USER_MOD_PLAYER = new Permission("Modify Player", "Allows this user to modify players in the balancer.", 9);
    public static final Permission CAN_USER_DELETE_PLAYER = new Permission("Delete Player", "Allows this user to delete players from the balancer.", 10);

    public static final List<Permission> ALL_PERMISSIONS = Arrays.asList(
            IS_USER_ADMIN,
            CAN_USER_INVITE,
            CAN_USER_MOD_INVITE,
            CAN_USER_DELETE_INVITE,
            CAN_USER_MOD_USERS,
            CAN_USER_BALANCE,
            CAN_USER_ADD_PLAYER,
            CAN_USER_MOD_PLAYER,
            CAN_USER_DELETE_PLAYER
    );

    public static final List<Integer> ALL_PERMISSION_VALUES = ALL_PERMISSIONS.stream().map(permission->permission.value).collect(Collectors.toList());

    public static final List<Integer> ADMIN_PERMISSIONS = Arrays.asList(
            IS_USER_ADMIN.value,
            IS_USER_SUPER_ADMIN.value
    );

    public static List<Integer> getValidPermissions(@Nonnull final List<Integer> permissions) {
        return permissions.stream().filter(ALL_PERMISSION_VALUES::contains).collect(Collectors.toList());
    }
}
