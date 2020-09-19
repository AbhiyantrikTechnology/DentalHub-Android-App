package com.abhiyantrik.dentalhub.utils

import android.util.Patterns

class EmailValidator {
    companion object {
        fun isEmailValid(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.toRegex().matches(email)
        }
    }
}