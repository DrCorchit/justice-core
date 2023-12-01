package com.drcorchit.justice.lang.annotations

@Target(AnnotationTarget.CLASS)
annotation class EvaluableClass(val supertype: String = "Any")