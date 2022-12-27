package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.Gender

data class User(val uId: String?=null, val username: String?=null, val weight: String?=null, val age: String?=null, val gender: Gender?=null, val exp: String?=null)