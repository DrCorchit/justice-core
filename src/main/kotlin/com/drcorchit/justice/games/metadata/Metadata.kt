package com.drcorchit.justice.games.metadata

import com.drcorchit.justice.games.Game
import com.drcorchit.justice.utils.Version

interface Metadata {
    val game: Game

    val start: Long
    val age: Long
    val lastModified: Long

    //Location from which the game is saved and loaded.
    //May be an S3 or http url, or a file path.
    val path: String

    //Unique identifier for the game.
    val id: String

    //Client-visible identifier for the game.
    val name: String

    //Explains the game rules and objectives
    val description: String
    //Image that represents this game mode for the client
    val image: String
    val author: String
    val version: Version

    val isAutosaveEnabled: Boolean
    val isNotificationsEnabled: Boolean
}