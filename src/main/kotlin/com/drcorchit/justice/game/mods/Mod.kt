package com.drcorchit.justice.game.mods

import com.drcorchit.justice.utils.StringUtils.whitelist
import com.drcorchit.justice.utils.data.AcyclicGraph
import com.drcorchit.justice.utils.exceptions.MissingDependencyException
import com.drcorchit.justice.utils.json.JsonDiff
import com.drcorchit.justice.utils.json.JsonUtils.getString
import com.drcorchit.justice.utils.json.JsonUtils.parseFromAnywhere
import com.drcorchit.justice.utils.json.info
import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

class Mod private constructor(info: JsonObject) {
    val author: String
    val description: String

    //the name and version of the mod. determine equality
    @JvmField
    val id: ModID

    //a list of mechanics required by this mod
    val reqMechanics: ImmutableBiMap<String, String>

    //a map from urls to the modID of each url.
    //It is illegal to have urls that do not point to a JSON resource, or urls that point to the same mod
    @JvmField
    var reqMods: ImmutableBiMap<String, ModID>

    //a list of changes to mechanics to be made by this mod
    var diffMap: ImmutableMap<String, ImmutableSet<JsonDiff<JsonObject>>>

    init {
        val name = info["name"].asString
        val version = info["version"].asString
        id = ModID.getModID(name, version)
        //description = JsonUtils.getWhitelistedString(info, "description", );
        //author = JsonUtils.getWhitelistedString(info, "author");
        description = info.getString(
            "description",
            "No description is available for this mod"
        ).whitelist()

        author = info.get("author").asString.whitelist()

        reqMechanics = if (info.has("mechanics")) {
            val mechInfo = info.getAsJsonObject("mechanics")
            val mechBuilder = ImmutableBiMap.builder<String, String>()
            for ((key, value) in mechInfo.entrySet()) {
                mechBuilder.put(key, value.asString)
            }
            mechBuilder.build()
        } else ImmutableBiMap.of()

        reqMods = if (info.has("dependencies")) {
            val depInfo = info.getAsJsonArray("dependencies")
            val depBuilder = ImmutableBiMap.builder<String, ModID>()
            for (ele in depInfo) {
                val depUrl = ele.asString
                val depId = ModID.getModID(depUrl)
                depBuilder.put(depUrl, depId)
            }
            depBuilder.build()
        } else {
            ImmutableBiMap.of()
        }
        diffMap = if (info.has("diffs")) {
            val diffsBuilder = ImmutableMap.builder<String, ImmutableSet<JsonDiff<JsonObject>>>()
            val diffsInfo = info.getAsJsonObject("diffs")
            for ((key, value) in diffsInfo.entrySet()) {
                if (value.isJsonArray) {
                    val builder = ImmutableSet.builder<JsonDiff<JsonObject>>()
                    for (ele in value.asJsonArray) builder.add(JsonDiff.of(ele) as JsonDiff.ObjectDiff)
                    diffsBuilder.put(key, builder.build())
                } else if (value.isJsonObject) {
                    diffsBuilder.put(key, ImmutableSet.of(JsonDiff.of(value) as JsonDiff.ObjectDiff))
                } else {
                    throw IllegalArgumentException("Each diffs entry must be a single diff or an array of diffs.")
                }
            }
            diffsBuilder.build()
        } else {
            ImmutableBiMap.of()
        }
    }

    private fun checkStatus() {
        reqMods.keys.forEach { modName: String -> ensureLoaded(modName) }

        //check for cyclic dependencies
        val deps = reqMods.values.stream().map { id: ModID -> id.normalName }.collect(Collectors.toSet())
        DEPENDENCIES.assertAcyclic(id.normalName, deps)

        //make sure all diffs are targeting json objects.
        diffMap.values.forEach { set: ImmutableSet<JsonDiff<JsonObject>> ->
            set.forEach { diff: JsonDiff<JsonObject> -> checkType(diff) }
        }
    }

    private fun ensureLoaded(modName: String) {
        if (ModID.getLatestVersion(modName) == null) {
            val message = String.format("Could not load dependency at %s for mod %s", modName, id.name)
            throw RuntimeException(message)
        }
    }

    override fun toString(): String {
        return id.value
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Mod) {
            other.id == id
        } else false
    }

    fun loadMissingMechanics(mechInfo: MutableMap<String, JsonObject>) {
        reqMechanics.forEach { (str: String, url: String?) ->
            mechInfo.computeIfAbsent(str) { parseFromAnywhere(url!!).info.asJsonObject }
        }
    }

    fun modifyExistingMechanics(mechInfo: MutableMap<String, JsonObject>) {
        diffMap.forEach { (mechanic: String, diffs: ImmutableSet<JsonDiff<JsonObject>>) ->
            if (!mechInfo.containsKey(mechanic)) throw MissingDependencyException(mechanic)
            var temp = mechInfo[mechanic]!!
            for (diff in diffs) temp = diff.applyUnchecked(temp)
            mechInfo[mechanic] = temp
        }
    }

    companion object {
        //pathological game states:
        //mods with cyclic dependencies
        //game trying to install two versions of same mod
        //trying to install a mod, and also its dependency
        //trying to install mods with very similar names
        private val MODS = ConcurrentHashMap<ModID, Mod>()
        private val DEPENDENCIES = AcyclicGraph<String>()

        @JvmStatic
        @Throws(Exception::class)
        fun load(url: String): Mod {
            val id = ModID.getModID(url)
            if (MODS.containsKey(id)) return MODS[id]!!
            val info = parseFromAnywhere(url).info.asJsonObject
            val gm = Mod(info)
            gm.checkStatus()
            MODS[id] = gm
            return gm
        }

        @JvmStatic
        fun getMod(id: ModID): Mod {
            if (!MODS.containsKey(id)) {
                val message = String.format("No mod found with ID %s", id)
                throw IllegalArgumentException(message)
            }
            return MODS[id]!!
        }

        private fun checkType(diff: JsonDiff<*>) {
            require(diff is JsonDiff.ObjectDiff)
            { "Top-level modifications may only modify json objects." }
        }
    }
}