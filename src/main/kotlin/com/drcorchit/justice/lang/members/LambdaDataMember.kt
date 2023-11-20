package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator

class LambdaDataMember<T>(
    name: String,
    description: String,
    argTypes: List<Evaluator<*>>,
    returnType: Evaluator<*>,
    impl: (T) -> Any?
) : LambdaFieldMember<T>(name, description, argTypes, returnType, impl), DataFieldMember<T>