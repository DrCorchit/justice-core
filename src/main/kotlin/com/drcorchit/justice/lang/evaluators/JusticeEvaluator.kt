package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.lang.annotations.*
import com.drcorchit.justice.lang.members.*
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

open class JusticeEvaluator<T : Any>(final override val clazz: KClass<T>) : Evaluator<T> {
    final override val members: ImmutableMap<String, Member<T>>

    init {
        val membersList = convertClass(clazz)

        val temp = membersList.associateBy { it.name }.toMutableMap()

        members = ImmutableMap.copyOf(temp)
    }

    companion object {
        private val justiceFieldAnnotations = ImmutableSet.of(
            DataField::class,
            DerivedField::class,
            CachedField::class
        )

        private val justiceFunctionAnnotations = ImmutableSet.of(
            JFunction::class
        )

        fun <T : Any> convertClass(thing: KClass<T>): List<Member<T>> {
            return thing.members.mapNotNull { convertMember(it) }
                .map { it as Member<T> }.toList()
        }

        private fun convertMember(member: KCallable<*>): Member<*>? {
            val fieldAnnotations = member.annotations.filter {
                justiceFieldAnnotations.contains(it.annotationClass)
            }
            val functionAnnotations = member.annotations.filter {
                justiceFunctionAnnotations.contains(it.annotationClass)
            }

            if (fieldAnnotations.isEmpty() && functionAnnotations.isEmpty()) {
                //It's not a Justice member.
                return null
            } else if (fieldAnnotations.isNotEmpty() && functionAnnotations.isNotEmpty()) {
                throw IllegalArgumentException("Cannot construct JMember: Class member $member has both field and function annotations.")
            } else if (fieldAnnotations.isNotEmpty()) {
                require(fieldAnnotations.size == 1) {
                    "Cannot construct JMember: Class member $member has conflicting field annotations: $fieldAnnotations"
                }
                return when (val annotation = fieldAnnotations.first()) {
                    is CachedField -> WrappedCachedMember<Any>(member, annotation)
                    is DerivedField -> WrappedDerivedMember<Any>(member, annotation)
                    is DataField -> {
                        val mutableMember = member as KMutableProperty<*>
                        WrappedDataMember<Any>(mutableMember, annotation)
                    }
                    else -> throw IllegalArgumentException("Unknown field annotation: $annotation")
                }
            } else {
                val annotation = functionAnnotations.filterIsInstance<JFunction>().first()
                return WrappedFunctionMember<Any>(member, annotation)
            }
        }
    }
}