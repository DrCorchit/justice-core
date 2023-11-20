package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.JFunction
import kotlin.reflect.KCallable

class WrappedFunctionMember<T>(member: KCallable<*>, annotation: JFunction) :
    WrappedMember<T>(member, annotation.description) {

}