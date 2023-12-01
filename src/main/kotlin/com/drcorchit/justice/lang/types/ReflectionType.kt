package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.Evaluable
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.reflection.CachedReflectionMember
import com.drcorchit.justice.lang.members.reflection.DerivedReflectionMember
import com.drcorchit.justice.lang.members.reflection.ReflectionDataMember
import com.drcorchit.justice.lang.members.reflection.ReflectionFunctionMember
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.isSuperclassOf

open class ReflectionType<T : Any>(
    clazz: KClass<T>,
    tempUniverse: TypeUniverse? = null,
    tempParent: Type<*> = AnyType
) : Type<T>(clazz, tempParent as Type<in T>) {
    val universe by lazy { tempUniverse ?: TypeUniverse.getDefault() }

    init {
        val parentClass = tempParent.clazz
        if (parentClass == clazz) {
            throw IllegalArgumentException("A Type cannot be its own parent!")
        } else if (!parentClass.isSuperclassOf(clazz)) {
            throw IllegalArgumentException("Type ${tempParent.clazz} is not a supertype of ${clazz.qualifiedName}")
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

        //val typeAnnotations = member.annotations.filterIsInstance<TypeParameter>()

        if (fieldAnnotations.isEmpty() && functionAnnotations.isEmpty()) {
            //It's not a Justice member.
            return null
        } else if (fieldAnnotations.isNotEmpty() && functionAnnotations.isNotEmpty()) {
            throw IllegalArgumentException("Cannot construct JusticeMember: Class member $member has both field and function annotations.")
        } else if (fieldAnnotations.isNotEmpty()) {
            require(fieldAnnotations.size == 1) {
                "Cannot construct JMember: Class member $member has conflicting field annotations: $fieldAnnotations"
            }
            return when (val annotation = fieldAnnotations.first()) {
                is CachedField -> CachedReflectionMember(universe, this, member, annotation)
                is DerivedField -> DerivedReflectionMember(universe, this, member, annotation)
                is DataField -> {
                    val mutableMember = member as KMutableProperty<*>
                    ReflectionDataMember(universe, this, mutableMember, annotation)
                }

                else -> throw IllegalArgumentException("Unknown field annotation: $annotation")
            }
        } else {
            val annotation = functionAnnotations.filterIsInstance<Evaluable>().first()
            return ReflectionFunctionMember(universe, this, member, annotation)
        }
    }

    companion object {
        private val justiceFieldAnnotations = ImmutableSet.of(
            DataField::class,
            DerivedField::class,
            CachedField::class
        )

        private val justiceFunctionAnnotations = ImmutableSet.of(
            Evaluable::class
        )
    }
}