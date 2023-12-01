package com.drcorchit.justice.lang.annotations

@Target(AnnotationTarget.PROPERTY)
annotation class DataField(val description: String = "", val mutable: Boolean = false, val defaultValue: String = "")