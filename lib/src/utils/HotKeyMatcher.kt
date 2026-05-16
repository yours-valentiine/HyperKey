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
package io.github.yoursvalentiine.hyperkey.utils

import androidx.compose.ui.input.key.*
import io.github.yoursvalentiine.hyperkey.models.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

class HotKeyMatcher(
    private val timeout: Long = 2000L
) {
    val pressed = mutableSetOf<Key>()

    private val sequenceProgress = mutableMapOf<HotKey, Int>()

    private val timeSource = TimeSource.Monotonic

    private var lastEventTime = timeSource.markNow()

    fun match(event: KeyEvent, hotkeys: List<HotKey>): Boolean {
        val now = timeSource.markNow()

        if (now - lastEventTime > timeout.milliseconds) {
            sequenceProgress.clear()
        }
        lastEventTime = now

        val isAutorepeat = event.type == KeyEventType.KeyDown && event.key in pressed

        when (event.type) {
            KeyEventType.KeyUp -> pressed -= event.key
            KeyEventType.KeyDown -> pressed += event.key
            else -> return false
        }

        if (isAutorepeat) return false

        hotkeys.forEach { hotkey ->

            val matched = when (val trigger = hotkey.trigger) {
                is HotKeyTrigger.Chord -> matchChord(event = event, chord = trigger)

                is HotKeyTrigger.Sequence -> matchSequence(
                    event = event,
                    hotkey = hotkey,
                    sequence = trigger,
                )
            }

            if (matched) {
                sequenceProgress.remove(hotkey)
                hotkey.action(event)
                return true
            }
        }
        return false
    }

    private fun matchChord(event: KeyEvent, chord: HotKeyTrigger.Chord): Boolean {
        if (chord.eventType != event.type) return false

        val expandedModifiers = chord.modifiers.expand()

        val modifiersMatch =
            event.isCtrlPressed == (Ctrl in expandedModifiers) &&
                    event.isAltPressed == (Alt in expandedModifiers) &&
                    event.isShiftPressed == (Shift in expandedModifiers) &&
                    event.isMetaPressed == (Meta in expandedModifiers)

        return modifiersMatch && when (chord.keys.size) {
            // If the modifier keys match, but the list of regular keys is empty,
            // we process them. (For example, Alt+Ctrl)
            0 -> true
            // If there is only one key, then we check whether it is the same one
            1 -> event.key == chord.keys.first()
            // If there are a lot of keys, then we check whether all the others are pressed.
            else -> event.key in chord.keys && chord.keys.all { it in pressed }
        }
    }

    private fun matchSequence(
        event: KeyEvent,
        hotkey: HotKey,
        sequence: HotKeyTrigger.Sequence
    ): Boolean {
        // We get the position of which key combination
        // from the sequence we are currently on.
        val currentProgress = sequenceProgress[hotkey] ?: 0
        // We get the required keys for this position in the sequence
        val expected = sequence.chords[currentProgress]

        if (expected.eventType != event.type) return false

        // We check each key, if it doesn’t match,
        // then we check if this is a new sequence.
        if (!matchChord(event = event, chord = expected)) {
            sequenceProgress.remove(hotkey)

            sequence.chords[0].let {
                if (matchChord(event = event, chord = it)) {
                    sequenceProgress[hotkey] = 1
                }
            }
            return false
        }

        val nextStep = currentProgress + 1

        // If after increasing the step we become equal to the size of the sequence,
        // then the sequence is completed
        return if (nextStep == sequence.chords.size) {
            true
        } else {
            sequenceProgress[hotkey] = nextStep
            false
        }
    }
}