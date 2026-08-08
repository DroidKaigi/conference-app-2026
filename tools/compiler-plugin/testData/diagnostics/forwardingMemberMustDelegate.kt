interface Renderer {
    val id: String
    fun render(width: Int): String
}

class ForwardsEveryMember(private val delegate: Renderer) : Renderer {
    override val <!FORWARDING_MEMBER_MUST_DELEGATE!>id<!>: String get() = delegate.id
    override fun <!FORWARDING_MEMBER_MUST_DELEGATE!>render<!>(width: Int): String = delegate.render(width)
}

class AdaptsArgument(private val delegate: Renderer) : Renderer {
    override val id: String = "adapted"
    override fun render(width: Int): String = delegate.render(width * 2)
}

class WrapsResult(private val delegate: Renderer) : Renderer {
    override val id: String = "wrapped"
    override fun render(width: Int): String = "[" + delegate.render(width) + "]"
}

class AddsLogic(private val delegate: Renderer, private val fallback: String) : Renderer {
    override val id: String = "logic"
    override fun render(width: Int): String {
        if (width <= 0) return fallback
        return delegate.render(width)
    }
}

interface Logger {
    fun debug(message: String)
}

interface LogSink {
    fun d(message: String)
}

class AdaptsCalleeName(private val sink: LogSink) : Logger {
    override fun debug(message: String) = sink.d(message)
}

class ForwardsAnyMembers(private val delegate: Renderer) : Renderer {
    override val id: String = "any"
    override fun render(width: Int): String = "any"
    override fun hashCode(): Int = delegate.hashCode()
    override fun toString(): String = delegate.toString()
}

class HoldsMutableSource(private var delegate: Renderer) : Renderer {
    override val id: String get() = delegate.id
    override fun render(width: Int): String = delegate.render(width)
}

class HoldsRecomputedSource(private val delegates: List<Renderer>) : Renderer {
    private val delegate: Renderer get() = delegates.first()
    override val id: String get() = delegate.id
    override fun render(width: Int): String = delegate.render(width)
}

class FixedRenderer : Renderer {
    override val id: String = "fixed"
    override fun render(width: Int): String = "fixed"
}

class HoldsSourceNotFromConstructor : Renderer {
    private val delegate: Renderer = FixedRenderer()
    override val id: String get() = delegate.id
    override fun render(width: Int): String = delegate.render(width)
}

class RedeclaresDelegatedMember(private val delegate: Renderer) : Renderer by delegate {
    override val id: String get() = delegate.id
}
