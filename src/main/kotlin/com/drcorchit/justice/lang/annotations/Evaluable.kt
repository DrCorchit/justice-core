package com.drcorchit.justice.lang.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class Evaluable(val description: String, val hasSideEffects: Boolean = false)