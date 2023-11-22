package com.drcorchit.justice.game.metadata

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.IntEvaluator
import com.drcorchit.justice.lang.evaluators.NonSerializableEvaluator
import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.json.JsonUtils
import com.drcorchit.justice.utils.json.JsonUtils.getBool
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class JsonMetadata(override val parent: Game) : Metadata {
    private var json = JsonObject()
    private val evaluator by lazy { makeEvaluator() }

    override val start: Long get() = json["start"].asLong
    override val age: Long get() = System.currentTimeMillis() - start

    //Location from which the game is saved and loaded.
    //May be an S3 or http url, or a file path.
    override val path: String get() = json["path"].asString

    //Unique identifier for the game.
    override val id: String get() = json["id"].asString

    //Client-visible identifier for the game.
    override val name: String get() = json["name"].asString

    //Explains the game rules and objectives
    override val description: String get() = json["description"].asString
    override val image: String get() = json["image"].asString
    override val author: String get() = json["author"].asString
    override val version: Version get() = Version(json["version"].asString)

    override val isAutosaveEnabled: Boolean
        get() = json.getBool("autosaveEnabled", false)
    override val isNotificationsEnabled: Boolean
        get() = json.getBool("notificationsEnabled", false)

    override fun getProperty(property: String): JsonElement? {
        return json[property]
    }

    override fun setProperty(property: String, value: Any) {
        if (value is JsonElement) {
            json.add(property, value)
        } else {
            json.add(property, JsonUtils.GSON.toJsonTree(value))
        }
    }

    override fun serialize(): JsonObject {
        json.addProperty("lastModified", lastModified)
        json.addProperty("seed", parent.seed)
        json.addProperty("state", parent.getState().name)
        return json.deepCopy()
    }

    override fun deserialize(info: JsonObject) {
        json = info
    }

    override fun getEvaluator(): Evaluator<Metadata> {
        return evaluator
    }

    fun makeEvaluator(): Evaluator<Metadata> {
        val members = listOf(
            LambdaFieldMember<Metadata>("seed", "The seed of the game's primary RNG.", IntEvaluator) {
                it.parent.seed
            }
            //TODO add other members.
        )

        return object : NonSerializableEvaluator<Metadata>() {
            override val clazz = Metadata::class
            override val members = ImmutableMap.copyOf(members.associateBy { it.name })
        }
    }
}