package io.github.yoursvalentiine.hyperkey.models

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType

data class RawSequence(
    private val previewChord: List<HotKeyTrigger.Chord>,
    private val keys: Set<Key>,
    private val modifiers: Set<KeyModifier>
) {
    constructor(
        previewChord: List<HotKeyTrigger.Chord>,
        key: Key,
        modifiers: Set<KeyModifier>
    ) : this(previewChord = previewChord, keys = setOf(key), modifiers = modifiers)

    infix fun andThen(key: Key) = RawSequence(
        previewChord = previewChord + HotKeyTrigger.Chord(
            keys = keys,
            modifiers = modifiers,
            eventType = KeyEventType.KeyDown
        ),
        keys = setOf(key),
        modifiers = emptySet()
    )

    infix fun andThen(mod: KeyModifier) = RawSequence(
        previewChord = previewChord + HotKeyTrigger.Chord(
            keys = keys,
            modifiers = modifiers,
            eventType = KeyEventType.KeyDown
        ),
        keys = emptySet(),
        modifiers = setOf(mod)
    )

    internal fun buildSequence(eventType: KeyEventType): HotKeyTrigger.Sequence {
        val lastChord = HotKeyTrigger.Chord(keys = keys, modifiers = modifiers, eventType = eventType)
        return HotKeyTrigger.Sequence(
            chords = previewChord + lastChord
        )
    }
}
