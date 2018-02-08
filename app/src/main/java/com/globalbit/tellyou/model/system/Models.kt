package com.globalbit.tellyou.model.system

import com.google.gson.annotations.SerializedName

/**
 * Created by alex on 30/11/2017.
 */

class LimitationSettingsKT (
        val minLength: Int,
        val maxLength: Int,
        val regex: String
)

class ValidationKT (
        val password: LimitationSettingsKT,
        val username: LimitationSettingsKT,
        val firstName: LimitationSettingsKT,
        val lastName: LimitationSettingsKT,
        val bio: LimitationSettingsKT,
        val postTextLength: LimitationSettingsKT,
        val sharePostTextLength: LimitationSettingsKT,
        val pollOptionTextLength: LimitationSettingsKT,
        val photoPollOptionTextLength: LimitationSettingsKT,
        val commentText: LimitationSettingsKT,
        val postDuration: LimitationSettingsKT
)

class PagesKT (
        val about: String,
        val privacy: String,
        val terms: String,
        val postShare: String,
        val libraries: String
)

class SystemPreferencesKT (
        val pages: PagesKT,
        val confirmationCodeLength: Int,
        val validations: ValidationKT
)

open class BaseResponseKT (
        val errorCode: Int,
        val errorDescription: String
)

class SystemPreferencesResponseKT (
        @SerializedName("vars") val systemPreferences: SystemPreferencesKT,
        errorCode: Int,
        errorDescription: String
) : BaseResponseKT(errorCode, errorDescription)

