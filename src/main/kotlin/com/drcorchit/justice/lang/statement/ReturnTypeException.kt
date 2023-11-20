package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.lang.evaluators.Evaluator

class ReturnTypeException(val type: Evaluator<*>): Exception()