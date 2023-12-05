package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.evaluation.environment.Parameters.Companion.toEnv
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaFieldMember
import com.drcorchit.justice.lang.members.lambda.LambdaMember
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.drcorchit.justice.lang.types.primitives.StringType
import com.google.common.collect.ImmutableMap

object TypeType : NonSerializableType<Type<*>>(Type::class) {
    override val members: ImmutableMap<String, Member<Type<*>>> = ImmutableMap.copyOf(listOf(
        LambdaFieldMember(
            this,
            "name",
            "The name of the type",
            StringType
        ) { it.clazz.qualifiedName!! },
        LambdaMember(
            this,
            "is",
            "Returns true if this type is a supertype or same as the other type.",
            mapOf("object" to TypeType).toEnv(),
            BooleanType,
            false
        ) { inst, args -> inst.accept(args.first() as Type<*>) },
    ).associateBy { it.name })
}