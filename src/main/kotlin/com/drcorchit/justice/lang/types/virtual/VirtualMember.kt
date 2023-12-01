package com.drcorchit.justice.lang.types.virtual

import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type

interface VirtualMember<T : Any> {
    fun makeMember(parameters: List<Type<*>>): Member<T>

}