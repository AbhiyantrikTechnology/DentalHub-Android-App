package com.abhiyantrik.dentalhub.models

class FormField(
    var fieldType: String,
    var fieldLabel: String,
    var fieldName: String,
    var fieldHint: String,
    var values: List<String>?
)