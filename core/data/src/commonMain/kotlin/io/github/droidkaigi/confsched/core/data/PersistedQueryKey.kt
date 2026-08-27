package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.common.MustBeSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import soil.query.QueryId
import soil.query.QueryKey
import soil.query.QueryPreloadData
import soil.query.QueryReceiver
import soil.query.buildQueryKey

// Unknown keys are ignored so additive server changes do not invalidate the persisted payload.
@PublishedApi
internal val persistedQueryJson: Json = Json { ignoreUnknownKeys = true }

/**
 * A [QueryKey] whose successful responses are persisted. The persisted payload is the **server
 * response**, not the domain model: model-layer refactoring never invalidates the cache, and
 * [transformToDomainModel] re-runs on restore. A payload that no longer decodes (the server
 * contract changed) is treated as a cache miss.
 */
inline fun <T : Any, @MustBeSerializable reified RESPONSE : Any> buildPersistedQueryKey(
    id: QueryId<T>,
    persistKey: String,
    fileStorage: FileStorage,
    noinline fetchResponse: suspend QueryReceiver.() -> RESPONSE,
    noinline transformToDomainModel: (RESPONSE) -> T,
    noinline onPersisted: (() -> Unit)? = null,
): QueryKey<T> {
    val serializer: KSerializer<RESPONSE> = serializer()
    val delegate = buildQueryKey(
        id = id,
        fetch = {
            val response = fetchResponse()
            runCatching {
                fileStorage.put(persistKey, persistedQueryJson.encodeToString(serializer, response).encodeToByteArray())
                onPersisted?.invoke()
            }
            transformToDomainModel(response)
        },
    )
    return object : QueryKey<T> by delegate {
        override fun onPreloadData(): QueryPreloadData<T> = {
            runCatching {
                fileStorage.get(persistKey)
                    ?.decodeToString()
                    ?.let { persistedQueryJson.decodeFromString(serializer, it) }
                    ?.let(transformToDomainModel)
            }.getOrNull()
        }
    }
}
