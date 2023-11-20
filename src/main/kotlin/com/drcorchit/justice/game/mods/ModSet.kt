package com.drcorchit.justice.game.mods

import com.drcorchit.justice.game.mods.Mod.Companion.load

//Maintains a list of mods, keeping the latest versions only
class ModSet {
    //tracks the latest version of a mod
    private val latestMods: HashMap<String, ModID> = HashMap()

    operator fun contains(id: ModID): Boolean {
        return latestMods.containsKey(id.normalName)
    }

    fun loadMod(url: String) {
        try {
            val mod = load(url)
            latestMods[mod.id.normalName] = mod.id.latestVersion
            mod.reqMods.keys.forEach { loadMod(it) }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val mods: Iterable<Mod>
        get() = latestMods.values.map { Mod.getMod(it) }
}