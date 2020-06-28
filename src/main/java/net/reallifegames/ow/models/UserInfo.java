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
package net.reallifegames.ow.models;

import javax.annotation.Nonnull;

public class UserInfo {

    public final int id;

    public final String name;

    public final int tankPreference;

    public final int supportPreference;

    public final int dpsPreference;

    public final int tankSr;

    public final int supportSr;

    public final int dpsSr;

    public UserInfo(final int id,
                    @Nonnull final String name,
                    final int tankPreference,
                    final int supportPreference,
                    final int dpsPreference,
                    final int tankSr,
                    final int supportSr,
                    final int dpsSr) {
        this.id = id;
        this.name = name;
        this.tankPreference = tankPreference;
        this.supportPreference = supportPreference;
        this.dpsPreference = dpsPreference;
        this.tankSr = tankSr;
        this.supportSr = supportSr;
        this.dpsSr = dpsSr;
    }
}
