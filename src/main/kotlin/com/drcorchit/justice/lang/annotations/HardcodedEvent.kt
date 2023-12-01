package com.drcorchit.justice.lang.annotations

@Target(AnnotationTarget.CLASS)
annotation class HardcodedEvent(val name: String, val description: String, val version: String = "1.0.0")
