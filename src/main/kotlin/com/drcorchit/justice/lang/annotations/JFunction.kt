package com.drcorchit.justice.lang.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class JFunction(val description: String, val hasSideEffects: Boolean = false)