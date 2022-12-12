package it.polimi.mobile.design.entities

import java.sql.Time
import java.time.LocalDateTime

data class Workout(val woId: String?=null,val userId: String?=null, val name: String?=null, val type: String?=null, val spotifyPlaylistLink: String?=null )
