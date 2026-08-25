package io.github.droidkaigi.confsched.core.testing

@DslMarker
annotation class RobotScenarioDsl

@RobotScenarioDsl
class ScenarioBuilder<R> {
    private val nodes = mutableListOf<Node<R>>()

    fun describe(description: String, block: ScenarioBuilder<R>.() -> Unit) {
        nodes += Node.Group(description, ScenarioBuilder<R>().apply(block).build())
    }

    fun doIt(block: R.() -> Unit) {
        nodes += Node.Setup(block)
    }

    fun itShould(description: String, block: R.() -> Unit) {
        nodes += Node.Check(description, block)
    }

    internal fun build(): List<Node<R>> = nodes
}

internal sealed interface Node<R> {
    data class Group<R>(val description: String, val children: List<Node<R>>) : Node<R>

    data class Setup<R>(val block: R.() -> Unit) : Node<R>

    data class Check<R>(val description: String, val block: R.() -> Unit) : Node<R>
}

class ScenarioLeaf<R> internal constructor(
    val name: String,
    val setups: List<R.() -> Unit>,
    val check: R.() -> Unit,
)

internal fun <R> List<Node<R>>.flatten(
    prefix: String = "",
    setups: List<R.() -> Unit> = emptyList(),
): List<ScenarioLeaf<R>> {
    val leaves = mutableListOf<ScenarioLeaf<R>>()
    var acc = setups
    for (node in this) {
        when (node) {
            is Node.Setup -> acc = acc + node.block

            is Node.Check -> leaves += ScenarioLeaf(
                name = joinName(prefix, node.description),
                setups = acc,
                check = node.block,
            )

            is Node.Group -> leaves += node.children.flatten(
                prefix = joinName(prefix, node.description),
                setups = acc,
            )
        }
    }
    return leaves
}

private fun joinName(prefix: String, description: String): String =
    if (prefix.isEmpty()) description else "$prefix › $description"
