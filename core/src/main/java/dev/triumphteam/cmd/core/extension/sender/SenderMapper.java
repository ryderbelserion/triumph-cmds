/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.extension.sender;

import org.jetbrains.annotations.NotNull;

/**
 * Map a sender into a new type of sender that can be used on the commands.
 * This is useful if you want to have a custom type for your project.
 * For example a "User" sender with more data than the platform's sender provides.
 *
 * @param <D> the default sender, aka the platforms default.
 * @param <S> the mapped/final sender.
 */
public interface SenderMapper<D, S> {

    /**
     * Map from a default sender into your custom sender.
     *
     * @param defaultSender the platform provided sender.
     * @return a final usable version of the sender.
     */
    @NotNull S map(@NotNull final D defaultSender);

    /**
     * Return back to the original sender.
     *
     * @param sender the mapped/final sender.
     * @return the platform provided sender, that was used to map into the current sender.
     */
    @NotNull D mapBackwards(@NotNull final S sender);

}