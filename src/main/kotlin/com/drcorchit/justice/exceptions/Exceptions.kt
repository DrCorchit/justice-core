package com.drcorchit.justice.exceptions

import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.logging.Uri

//Compile exceptions
open class CompileException(message: String = "") : Exception(message)
class IllegalAssignmentException(message: String = "") : CompileException(message)

//Thrown by "throw" or "throws" statements
class JusticeException(message: String) : Exception(message)
open class JusticeRuntimeException(message: String = "") : Exception(message)

//A member was declared with an illegal name, or fails to extend an inherited member correctly
class MemberDefinitionException(reason: String) : CompileException(reason)
class MemberNotFoundException(clazz: Class<*>, name: String) :
    CompileException("No such member: ${clazz.name}\$$name")

//Nothing is wrong; this is normal program flow.
class ReturnException(val value: Thing<*>) : Exception()
class ReturnTypeException(val type: Type<*>) : Exception() {
}

class SerializationException(message: String = "") : Exception(message)
class DeserializationException(message: String = "") : Exception(message)

class TypeException(message: String) : CompileException(message) {
    constructor(field: String, expected: String, actual: String) : this("$field expected $expected; got $actual")

    constructor(field: String, expected: Type<*>, actual: Type<*>) : this(
        field,
        expected.clazz.name ?: "<anonymous>",
        actual.clazz.name ?: "<anonymous>"
    )
}

class UnimplementedMemberException(uri: Uri, member: Member<*>) :
    CompileException("Concrete type $uri has unimplemented inherited abstract member: ${member.name}")

class UnrecognizedBinaryOpException(op: String) : CompileException(op)
class UnrecognizedUnaryOpException(op: String) : CompileException(op)
