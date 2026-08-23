@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.Int8Array
import org.khronos.webgl.toByteArray
import org.khronos.webgl.toInt8Array
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// IndexedDB externals are hand-written because org.w3c.dom.indexeddb is absent from kotlinx-browser 0.5.0 (wasmJs).
private external interface IDBFactory : JsAny {
    fun open(name: String, version: Int): IDBOpenDBRequest
}

private external interface IDBRequest : JsAny {
    val result: JsAny?
    var onsuccess: (() -> Unit)?
    var onerror: (() -> Unit)?
}

private external interface IDBOpenDBRequest : IDBRequest {
    var onupgradeneeded: (() -> Unit)?
}

private external interface IDBDatabase : JsAny {
    fun transaction(storeNames: String, mode: String): IDBTransaction

    fun createObjectStore(name: String): JsAny
}

private external interface IDBTransaction : JsAny {
    fun objectStore(name: String): IDBObjectStore
}

private external interface IDBObjectStore : JsAny {
    fun put(value: JsAny?, key: String): IDBRequest

    fun get(key: String): IDBRequest

    fun delete(key: String): IDBRequest

    fun clear(): IDBRequest
}

private fun indexedDB(): IDBFactory = js("globalThis.indexedDB")

private fun openResultDb(req: IDBOpenDBRequest): IDBDatabase = js("req.result")

private fun resultBytes(req: IDBRequest): Int8Array? =
    js("req.result == null ? null : (req.result instanceof Int8Array ? req.result : new Int8Array(req.result))")

private const val DB_NAME = "droidkaigi-confsched"
private const val STORE_NAME = "blobs"
private const val DB_VERSION = 1

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class IndexedDbFileStorage : FileStorage {

    private suspend fun openDb(): IDBDatabase = suspendCancellableCoroutine { cont ->
        val request = indexedDB().open(DB_NAME, DB_VERSION)
        request.onupgradeneeded = {
            openResultDb(request).createObjectStore(STORE_NAME)
        }
        request.onsuccess = { cont.resume(openResultDb(request)) }
        request.onerror = { cont.resumeWithException(RuntimeException("IndexedDB open failed")) }
    }

    override suspend fun get(key: String): ByteArray? {
        val store = openDb().transaction(STORE_NAME, "readonly").objectStore(STORE_NAME)
        val req = store.get(key)
        return suspendCancellableCoroutine { cont ->
            req.onsuccess = { cont.resume(resultBytes(req)?.toByteArray()) }
            req.onerror = { cont.resumeWithException(RuntimeException("IndexedDB get failed")) }
        }
    }

    override suspend fun put(key: String, bytes: ByteArray) {
        val store = openDb().transaction(STORE_NAME, "readwrite").objectStore(STORE_NAME)
        val req = store.put(bytes.toInt8Array(), key)
        suspendCancellableCoroutine { cont ->
            req.onsuccess = { cont.resume(Unit) }
            req.onerror = { cont.resumeWithException(RuntimeException("IndexedDB put failed")) }
        }
    }

    override suspend fun delete(key: String) {
        val store = openDb().transaction(STORE_NAME, "readwrite").objectStore(STORE_NAME)
        val req = store.delete(key)
        suspendCancellableCoroutine { cont ->
            req.onsuccess = { cont.resume(Unit) }
            req.onerror = { cont.resumeWithException(RuntimeException("IndexedDB delete failed")) }
        }
    }

    override suspend fun clear() {
        val store = openDb().transaction(STORE_NAME, "readwrite").objectStore(STORE_NAME)
        val req = store.clear()
        suspendCancellableCoroutine { cont ->
            req.onsuccess = { cont.resume(Unit) }
            req.onerror = { cont.resumeWithException(RuntimeException("IndexedDB clear failed")) }
        }
    }
}
