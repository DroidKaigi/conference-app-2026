import androidx.compose.material3.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.model.MutationTag
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ReadsAndWritesLocalThroughValue() {
    val <!STATE_MUST_BE_DELEGATED!>selectedDay<!> = remember { mutableStateOf("Day1") }
    selectedDay.value = "Day2"
    Text(selectedDay.value)
}

@Composable
fun ReadsDerivedStateThroughValue(source: State<String>) {
    val <!STATE_MUST_BE_DELEGATED!>label<!> = derivedStateOf { "Day: " + source.value }
    Text(label.value)
}

class SelectionHolder {
    private val <!STATE_MUST_BE_DELEGATED!>selectedDay<!> = mutableStateOf("Day1")

    fun select(day: String) {
        selectedDay.value = day
    }

    fun current(): String = selectedDay.value
}

@Composable
fun DelegatesTheState() {
    var selectedDay by remember { mutableStateOf("Day1") }
    selectedDay = "Day2"
    Text(selectedDay)
}

@Composable
private fun Observe(state: State<String>) {
    Text(state.value)
}

@Composable
fun PassesTheStateAsArgument() {
    val selectedDay = remember { mutableStateOf("Day1") }
    Column {
        Text(selectedDay.value)
        Observe(selectedDay)
    }
}

fun ReturnsTheState(): State<String> {
    val selectedDay = mutableStateOf("Day1")
    selectedDay.value = "Day2"
    return selectedDay
}

@Composable
fun DestructuresTheState() {
    val selectedDay = remember { mutableStateOf("Day1") }
    val (current, setCurrent) = selectedDay
    Text(current)
    setCurrent("Day2")
}

@Composable
fun CallsAnotherMemberOnTheState() {
    val selectedDay = remember { mutableStateOf("Day1") }
    Text(selectedDay.toString())
}

class ReassignableHolder {
    private var selectedDay: MutableState<String> = mutableStateOf("Day1")

    fun select(day: String) {
        selectedDay.value = day
    }
}

class WidelyVisibleHolder {
    val selectedDay = mutableStateOf("Day1")

    fun select(day: String) {
        selectedDay.value = day
    }
}

class ComputedHolder {
    private val backing = mutableStateOf("Day1")
    private val selectedDay: State<String> get() = backing

    fun current(): String = selectedDay.value
}

class FlowHolder {
    private val selectedDay = MutableStateFlow("Day1")

    fun select(day: String) {
        selectedDay.value = day
    }

    fun current(): String = selectedDay.value
}

class TagHolder {
    private val tag = MutationTag("bookmark")

    fun name(): String = tag.value
}

@Composable
fun NeverReadsTheState() {
    val selectedDay = remember { mutableStateOf("Day1") }
}

private val <!STATE_MUST_BE_DELEGATED!>topLevelState<!> = mutableStateOf("Day1")

fun readTopLevelState(): String = topLevelState.value

@Composable
fun RemembersAndReturnsTheState(): MutableState<String> {
    val selectedDay = remember { mutableStateOf("Day1") }
    return selectedDay
}
