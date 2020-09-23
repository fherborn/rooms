package com.thkoeln.cide.roomsserver.models

import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany


interface BaseUser : BaseEntity {
    val principal: String
    val givenName: String
    val familyName: String
    val email: String
    val imageUrl: String?
}


@Entity
class User(
        @Column(unique = true, columnDefinition = "LONGBLOB")
        override val principal: String,
        override val givenName: String,
        override val familyName: String,
        override val email: String,
        override val imageUrl: String? = null,

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", orphanRemoval = true)
        val abos: List<Abo> = listOf(),

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", orphanRemoval = true)
        val reservations: List<Reservation> = listOf(),

        id: UUID = UUID.randomUUID()
) : PersistableEntity(id), BaseUser