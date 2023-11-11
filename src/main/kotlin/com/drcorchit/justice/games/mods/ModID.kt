package com.drcorchit.justice.games.mods

import com.drcorchit.justice.utils.Strings.Companion.normalize
import com.drcorchit.justice.utils.Strings.Companion.whitelist
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.createCache
import com.drcorchit.justice.utils.json.JsonUtils.Companion.parseFromUrl
import com.google.common.cache.LoadingCache
import com.google.gson.JsonObject
import java.util.concurrent.ConcurrentHashMap

class ModID private constructor(pair: Pair<String, String>) {
    val name: String
    val normalName: String
    val value: String
    val version: Version
    val latestVersion: ModID
        get() = getLatestVersion(normalName)!!

    init {
        name = pair.first
        normalName = name.normalize()
        version = Version(pair.second)
        value = "${normalName}_v$version"
        val current: ModID? = LATEST_VERSIONS[normalName]
        if (current == null || version > current.version) {
            LATEST_VERSIONS[normalName] = this
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ModID) {
            other.value == value
        } else false
    }

    override fun toString(): String {
        return value
    }

    companion object {
        private val MOD_IDS: LoadingCache<Pair<String, String>, ModID> =
            createCache(1000) { pair: Pair<String, String> -> ModID(pair) }
        private val LATEST_VERSIONS: ConcurrentHashMap<String, ModID> = ConcurrentHashMap<String, ModID>()

        @JvmStatic
        fun getModID(url: String): ModID {
            val info: JsonObject = parseFromUrl(url).asJsonObject
            val name: String = info.get("name").asString.whitelist()
            val version = info["version"].asString
            return getModID(name, version)
        }

        @JvmStatic
        fun getModID(name: String, version: String): ModID {
            return MOD_IDS.get(name to version)
        }

        @JvmStatic
        fun getLatestVersion(normalName: String): ModID? {
            return LATEST_VERSIONS[normalName]
        }
    }
}