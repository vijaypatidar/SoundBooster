package com.vkpapps.soundbooster.model

import java.io.Serializable

/**
 * @author VIJAY PATIDAR
 */
class User : Serializable {
    var name: String? = null
    lateinit var userId: String
}