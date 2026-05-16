/*
 * Copyright (c) 2026, yours-valentiine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.yoursvalentiine.hyperkey.scope

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import io.github.yoursvalentiine.hyperkey.annotation.HotKeyDsl
import io.github.yoursvalentiine.hyperkey.models.*

internal typealias Action = (KeyEvent) -> Unit

internal val Pressed = KeyEventType.KeyDown
internal val Released = KeyEventType.KeyUp

@HotKeyDsl
abstract class BaseHotKeyScope(
    val hotkeys: MutableList<HotKey>
) {
    protected fun register(
        trigger: HotKeyTrigger,
        preview: Boolean,
        action: Action
    ) {
        hotkeys += HotKey(
            trigger = trigger,
            preview = preview,
            action = action
        )
    }

    open infix fun RawChord.press(action: Action) =
        register(
            trigger = this.asChord(eventType = Pressed),
            preview = false,
            action = action
        )

    open infix fun Key.press(action: Action) =
        register(
            trigger = this.asChord(eventType = Pressed),
            preview = false,
            action = action
        )

    open infix fun KeyModifier.press(action: Action) =
        register(
            trigger = this.asChord(eventType = Pressed),
            preview = false,
            action = action
        )

    open infix fun RawSequence.press(action: Action) =
        register(
            trigger = buildSequence(eventType = Pressed),
            preview = false,
            action = action
        )

    open infix fun RawChord.up(action: Action) =
        register(
            trigger = this.asChord(eventType = Released),
            preview = false,
            action = action
        )

    open infix fun Key.up(action: Action) =
        register(
            trigger = this.asChord(eventType = Released),
            preview = false,
            action = action
        )

    open infix fun KeyModifier.up(action: Action) =
        register(
            trigger = this.asChord(eventType = Released),
            preview = false,
            action = action
        )

    open infix fun RawSequence.up(action: Action) =
        register(
            trigger = buildSequence(eventType = Released),
            preview = false,
            action = action
        )
}