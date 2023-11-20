package com.drcorchit.justice.lang.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class CachedField(val description: String, val cacheSize: Int = 100)