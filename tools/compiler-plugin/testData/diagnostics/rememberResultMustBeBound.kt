import androidx.compose.material3.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import kotlinx.coroutines.launch

class RowScope {
    val label: String = "label"

    fun render() {
    }
}

@Composable
fun ReceiverOfCall() {
    <!REMEMBER_RESULT_MUST_BE_BOUND!>remember { RowScope() }<!>.render()
}

@Composable
fun ReceiverOfPropertyAccess() {
    Text(<!REMEMBER_RESULT_MUST_BE_BOUND!>remember { RowScope() }<!>.label)
}

@Composable
fun ReceiverOfExtensionCall() {
    Text(<!REMEMBER_RESULT_MUST_BE_BOUND!>remember { listOf("a") }<!>.first())
}

@Composable
fun BoundToLocal() {
    val scope = remember { RowScope() }
    scope.render()
    Text(scope.label)
}

@Composable
fun ArgumentPosition() {
    LaunchedEffect(remember { RowScope() }) { }
    Text(remember { "label" })
}

@Composable
fun ScopingFunctionArgument() {
    with(remember { RowScope() }) { render() }
}

@Composable
fun ScopingFunctionReceiver() {
    <!REMEMBER_RESULT_MUST_BE_BOUND!>remember { RowScope() }<!>.let { it.render() }
}

@Composable
fun Retained() {
    <!REMEMBER_RESULT_MUST_BE_BOUND!>retain { RowScope() }<!>.render()

    val scope = retain { RowScope() }
    scope.render()
}

@Composable
fun Saved() {
    Column {
        Text(<!REMEMBER_RESULT_MUST_BE_BOUND!>rememberSaveable { listOf("a") }<!>.first())
        Text(<!REMEMBER_RESULT_MUST_BE_BOUND!>rememberSerializable { mutableStateOf("label") }<!>.value)
        Text(<!REMEMBER_RESULT_MUST_BE_BOUND!>rememberUpdatedState("label")<!>.value)
    }
}

@Composable
fun HandleProducers() {
    rememberCoroutineScope().launch { }
}

@Composable
fun SafeCall(scope: RowScope?) {
    <!REMEMBER_RESULT_MUST_BE_BOUND!>remember(scope) { scope }<!>?.render()
}

@Composable
fun IndexAccess() {
    Text(<!REMEMBER_RESULT_MUST_BE_BOUND!>remember { listOf("a") }<!>[0])
}

@Composable
fun DelegatedProperty() {
    val label by remember { mutableStateOf("label") }
    Text(label)
}

@Composable
fun DestructuredResult() {
    val (first, second) = remember { "a" to "b" }
    Column {
        Text(first)
        Text(second)
    }
}

@Composable
fun ForLoopSubject() {
    Column {
        for (item in <!REMEMBER_RESULT_MUST_BE_BOUND!>remember { listOf("a") }<!>) {
            Text(item)
        }
    }
}
