package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator

class LambdaDataMember<T>(
    name: String,
    description: String,
    returnType: Evaluator<*>,
    impl: (T) -> Any?
) : LambdaFieldMember<T>(name, description, returnType, impl), DataFieldMember<T>