package com.drcorchit.justice.lang.annotations

@Repeatable
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class TypeParameter(val type: String)
