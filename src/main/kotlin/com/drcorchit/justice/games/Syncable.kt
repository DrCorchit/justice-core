package com.drcorchit.justice.games

interface Syncable<T: Any> {

    //This method is not intended to be called except by sync()
    fun preSync(info: T)

    fun sync(info: T)

    //This method is not intended to be called except by sync()
    fun postSync(info: T)

}