package com.drcorchit.justice.games.mechanics

data class MechID<T : GameMechanic<*>>(val name: String, val impl: Class<T>) {

}