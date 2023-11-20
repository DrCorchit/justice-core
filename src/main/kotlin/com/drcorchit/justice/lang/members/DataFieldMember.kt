package com.drcorchit.justice.lang.members

//Marker interface for members backed by an underlying field.
//Relevant during serialization.
interface DataFieldMember<T>: FieldMember<T> {
}