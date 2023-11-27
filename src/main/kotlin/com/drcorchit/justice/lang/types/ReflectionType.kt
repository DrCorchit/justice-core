package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.JFunction
import com.drcorchit.justice.lang.members.*
import com.drcorchit.justice.lang.types.Type.Companion.toMemberMap
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

open class ReflectionType<T : Any>(
    clazz: KClass<T>,
    tempUniverse: TypeUniverse? = null,
    tempParent: Type<*> = AnyType
) : Type<T> {
    final override val clazz = clazz.java
    final override val parent: Type<in T>?
    private val universe by lazy { tempUniverse ?: TypeUniverse.getDefault() }

    init {
        if (tempParent == clazz.java) {
            throw IllegalArgumentException("A Type cannot be its own parent!")
        } else if (tempParent.clazz.isAssignableFrom(clazz.java)) {
            parent = tempParent as Type<in T>
        } else {
            throw IllegalArgumentException("Type ${tempParent.clazz.name} is not a supertype of ${clazz.qualifiedName}")
        }
    }

    final override val members: ImmutableMap<String, Member<T>> by lazy {
        convertClass(clazz).toMemberMap()
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
                is CachedField -> CachedReflectionMember(universe, clazz, member, annotation)
                is DerivedField -> DerivedReflectionMember(universe, clazz, member, annotation)
                is DataField -> {
                    val mutableMember = member as KMutableProperty<*>
                    ReflectionDataMember(universe, clazz, mutableMember, annotation)
                }

                else -> throw IllegalArgumentException("Unknown field annotation: $annotation")
            }
        } else {
            val annotation = functionAnnotations.filterIsInstance<JFunction>().first()
            return ReflectionFunctionMember(universe, clazz, member, annotation)
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