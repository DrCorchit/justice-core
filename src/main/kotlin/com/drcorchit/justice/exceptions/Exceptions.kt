package com.drcorchit.justice.exceptions

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.utils.logging.Uri

//Compile exceptions
open class CompileException(message: String = "") : Exception(message)
class IllegalAssignmentException(message: String = "") : CompileException(message)

//The URI doesn't point to anything
class IllegalUriException(uri: String) : Exception(uri)

//The uri is malformed
class InvalidUriException(uri: Uri) : Exception("Invalid uri: $uri")

//Thrown by "throw" or "throws" statements
class JusticeException(message: String) : Exception(message)
open class JusticeRuntimeException(message: String = "") : Exception(message)

//A member was declared with an illegal name, or fails to extend an inherited member correctly
class MemberDefinitionException(reason: String) : CompileException(reason)
class MemberNotFoundException(instance: Any, name: String) :
    CompileException("No such member: ${instance::class.qualifiedName}\$$name")

//Nothing is wrong; this is normal program flow.
class ReturnException(val value: Any?) : Exception()
class ReturnTypeException(val type: Evaluator<*>) : Exception() {
}

class SerializationException(message: String = "") : Exception(message)
class DeserializationException(message: String = "") : Exception(message)

class TypeException(message: String) : CompileException(message) {
    constructor(field: String, expected: String, actual: String) : this("$field expected $expected; got $actual")

    constructor(field: String, expected: Evaluator<*>, actual: Evaluator<*>) : this(
        field,
        expected.clazz.qualifiedName ?: "<anonymous>",
        actual.clazz.qualifiedName ?: "anonymous"
    )
}

class UnimplementedMemberException(uri: Uri, member: Member<*>) :
    CompileException("Concrete type $uri has unimplemented inherited abstract member: ${member.name}")

class UnrecognizedBinaryOpException(op: String) : CompileException(op)
class UnrecognizedUnaryOpException(op: String) : CompileException(op)
class UnsupportedTypeException(string: String) : Exception("Type \"$string\" is not currently supported.") {
    constructor(clazz: Class<*>): this(clazz.name)
}