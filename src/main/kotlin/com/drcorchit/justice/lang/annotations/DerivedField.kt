package com.drcorchit.justice.lang.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class DerivedField(val description: String)