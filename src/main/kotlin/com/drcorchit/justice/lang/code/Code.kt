package com.drcorchit.justice.lang.code

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.types.Type

interface Code {
    fun run(context: ExecutionContext): Thing<*>

    //Performs type checking and other sanity checks. Infers the Type of the returned result.
    fun dryRun(context: DryRunContext): Type<*>
}