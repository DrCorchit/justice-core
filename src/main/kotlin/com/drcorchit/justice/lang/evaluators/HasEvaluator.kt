package com.drcorchit.justice.lang.evaluators

interface HasEvaluator<T : Any> {
    fun getEvaluator(): Evaluator<T>
}