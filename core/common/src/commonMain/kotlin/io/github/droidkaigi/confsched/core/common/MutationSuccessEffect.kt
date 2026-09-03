package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.filterNotNull
import soil.query.compose.MutationObject
import soil.query.compose.MutationSuccessObject

// Keyed by replyUpdatedAt (a timestamp, not mutatedCount) so it shares the error side's one-shot keying rule.
@Composable
fun <T> MutationSuccessEffect(
    mutation: MutationObject<T, *>,
    onSuccess: suspend (T) -> Unit,
) {
    val mutationState by rememberUpdatedState(mutation)
    // The effect outlives the composition that launched it, so it reads the handler through a
    // state: the one it was launched with holds the values of that first composition.
    val currentOnSuccess by rememberUpdatedState(onSuccess)
    var lastConsumedKey by rememberSaveable { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        snapshotFlow { mutationState as? MutationSuccessObject<T, *> }
            .filterNotNull()
            .collect {
                if (lastConsumedKey != it.replyUpdatedAt) {
                    lastConsumedKey = it.replyUpdatedAt
                    currentOnSuccess(it.data)
                }
            }
    }
}
