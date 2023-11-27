package com.drcorchit.justice.lang.annotations

@Target(AnnotationTarget.CLASS)
annotation class JusticeClass(val supertype: String = "Any")
