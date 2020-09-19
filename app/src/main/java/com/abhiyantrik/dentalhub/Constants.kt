package com.abhiyantrik.dentalhub

class Constants {

    companion object {
        @JvmStatic
        val CONTENT_TYPE_TEXT = "text/plain"
        @JvmStatic
        val CONTENT_TYPE_IMAGE = "image/*"
        @JvmStatic
        val CONTENT_TYPE_VIDEO = "video/*"

        @JvmStatic
        val DEFAULT_SELECTED_DISTRICT = 35 // id of Kaski

        @JvmStatic
        val PREF_AUTH_TOKEN = "AUTH-TOKEN"
        @JvmStatic
        val PREF_AUTH_EMAIL = "AUTH-EMAIL"
        @JvmStatic
        val PREF_AUTH_PASSWORD = "AUTH-PASSWORD"
        @JvmStatic
        val PREF_AUTH_SOCIAL = "AUTH-SOCIAL"

        @JvmStatic
        val PREF_SETUP_COMPLETE = "SETUP_COMPLETE"

        @JvmStatic
        val PREF_SELECTED_LOCATION_NAME = "SELECTED_LOCATION_NAME"
        @JvmStatic
        val PREF_SELECTED_LOCATION_ID = "SELECTED_LOCATION_ID"
        @JvmStatic
        val PREF_ACTIVITY_ID = "ACTIVITY_ID"
        @JvmStatic
        val PREF_ACTIVITY_NAME = "ACTIVITY_NAME"
        @JvmStatic
        val PREF_ACTIVITY_REMARKS = "ACTIVITY_REMARKS"
        @JvmStatic
        val PREF_ACTIVITY_SUGGESTIONS = "ACTIVITY_SUGGESTIONS"

        @JvmStatic
        val PREF_PROFILE_FULL_NAME = "USER_FULL_NAME"
        @JvmStatic
        val PREF_PROFILE_FIRST_NAME = "USER_FIRST_NAME"
        @JvmStatic
        val PREF_PROFILE_MIDDLE_NAME = "USER_MIDDLE_NAME"
        @JvmStatic
        val PREF_PROFILE_LAST_NAME = "USER_LAST_NAME"
        @JvmStatic
        val PREF_PROFILE_IMAGE = "USER_IMAGE"
        @JvmStatic
        val PREF_PROFILE_ID = "USER_ID"

        @JvmStatic
        val PREF_SELECTED_PATIENT = "SELECTED_PATIENT"

        @JvmStatic
        val PREF_LAST_SELECTED_PATIENT_POSITION = "LAST_SELECTED_PATIENT_POSITION"

        @JvmStatic
        val LOCATION_REQUEST = 1011
        @JvmStatic
        val GPS_REQUEST = 1012
    }


}