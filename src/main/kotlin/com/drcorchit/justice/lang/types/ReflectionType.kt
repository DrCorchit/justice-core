package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.JFunction
import com.drcorchit.justice.lang.members.*
import com.drcorchit.justice.lang.types.source.TypeSource
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

open class ReflectionType<T : Any>(val types: TypeSource, clazz: KClass<T>) : Type<T> {
    override val clazz = clazz.java
    final override val members: ImmutableMap<String, Member<T>> by lazy {
        convertClass(clazz).associateBy { it.name }.let { ImmutableMap.copyOf(it) }
    }

    private fun convertClass(thing: KClass<T>): List<Member<T>> {
        return thing.members.mapNotNull { convertMember(it) }.toList()
    }

    private fun convertMember(member: KCallable<*>): Member<T>? {
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
                is CachedField -> CachedReflectionMember(types, clazz, member, annotation)
                is DerivedField -> DerivedReflectionMember(types, clazz, member, annotation)
                is DataField -> {
                    val mutableMember = member as KMutableProperty<*>
                    ReflectionDataMember(types, clazz, mutableMember, annotation)
                }

                else -> throw IllegalArgumentException("Unknown field annotation: $annotation")
            }
        } else {
            val annotation = functionAnnotations.filterIsInstance<JFunction>().first()
            return ReflectionFunctionMember(types, clazz, member, annotation)
        }
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
    }
}