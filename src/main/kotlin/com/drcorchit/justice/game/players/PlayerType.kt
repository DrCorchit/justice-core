package com.drcorchit.justice.game.players

import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.source.TypeSource

object PlayerType : ReflectionType<Player>(TypeSource.universe, Player::class)