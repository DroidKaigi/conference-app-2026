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
import soil.query.compose.MutationErrorObject
import soil.query.compose.MutationObject

// Keyed by errorUpdatedAt (rememberSaveable) so each error is consumed once and never re-fires after restoration.
@Composable
fun MutationErrorEffect(
    mutation: MutationObject<*, *>,
    onError: suspend (Throwable) -> Unit,
) {
    val mutationState by rememberUpdatedState(mutation)
    val currentOnError by rememberUpdatedState(onError)
    var lastConsumedKey by rememberSaveable { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        snapshotFlow { mutationState as? MutationErrorObject }
            .filterNotNull()
            .collect {
                if (lastConsumedKey != it.errorUpdatedAt) {
                    lastConsumedKey = it.errorUpdatedAt
                    currentOnError(it.error)
                }
            }
    }
}
