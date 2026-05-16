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
package io.github.yoursvalentiine.hyperkey.models

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import io.github.yoursvalentiine.hyperkey.scope.Pressed

sealed class KeyModifier {
    operator fun plus(key: Key): RawChord =
        RawChord(keys = setOf(key), modifiers = setOf(this))

    operator fun plus(mod: KeyModifier): RawChord =
        RawChord(keys = emptySet(), modifiers = setOf(this, mod))

    internal fun KeyModifier.asChord(eventType: KeyEventType) = HotKeyTrigger.Chord(
        keys = emptySet(),
        modifiers = setOf(this),
        eventType = eventType
    )

    infix fun andThen(key: Key): RawSequence =
        RawSequence(
            previewChord = listOf(
                this.asChord(eventType = Pressed)
            ),
            keys = setOf(key),
            modifiers = emptySet()
        )


    infix fun andThen(mod: KeyModifier): RawSequence =
        RawSequence(
            previewChord = listOf(
                this.asChord(eventType = Pressed)
            ),
            keys = emptySet(),
            modifiers = setOf(mod)
        )

    infix fun andThen(chord: RawChord): RawSequence =
        RawSequence(
            previewChord = listOf(
                this.asChord(eventType = Pressed)
            ),
            keys = chord.keys,
            modifiers = chord.modifiers
        )

    open fun expand(): Set<KeyModifier> = setOf(this)
}

object Ctrl : KeyModifier()
object Alt : KeyModifier()
object Shift : KeyModifier()
object Meta : KeyModifier()

object Hyper : KeyModifier() {
    override fun expand(): Set<KeyModifier> = setOf(Ctrl, Alt, Shift, Meta)
}

fun Set<KeyModifier>.expand(): Set<KeyModifier> =
    flatMap { it.expand() }.toSet()