import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

interface Completable<C> {
    fun getCompletions(typed: String, context: C): List<String>
}

fun <C> Completable<C>.getBrigadierSuggestions(builder: SuggestionsBuilder, context: C): CompletableFuture<Suggestions> =
    getCompletions(builder.input, context).forEach { builder.suggest(it) }.run { builder.buildFuture() }