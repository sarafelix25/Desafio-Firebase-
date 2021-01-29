package com.example.desafiofirebase

import java.io.Serializable

data class Game(
    var name: String = "",
    var description: String = "",
    var year: String = "",
    var img: String = "",
    var id: String = ""
) : Serializable

data class User(val name: String, val email: String, val password: String, var id: String = "") :
    Serializable