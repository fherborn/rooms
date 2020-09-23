package com.thkoeln.cide.roomsserver.projections

import com.thkoeln.cide.roomsserver.models.*
import org.springframework.data.rest.core.config.Projection
import java.util.*

interface BaseProjection {
    val id: UUID
}

object Projections {
    const val BASE = "base"
    const val SIMPLE = "simple"
    const val DETAILED = "detailed"
}

@Projection(name = Projections.BASE, types = [Room::class])
interface BaseRoomProjection : BaseRoom, BaseProjection {
}

@Projection(name = Projections.SIMPLE, types = [Room::class])
interface SimpleRoomProjection : BaseRoomProjection {
    fun getDepartment(): SimpleDepartmentProjection
}

@Projection(name = Projections.DETAILED, types = [Room::class])
interface DetailedRoomProjection : BaseRoomProjection {
    fun getDepartment(): SimpleDepartmentProjection
    fun getReservations(): List<BaseReservationProjection>
}


@Projection(name = Projections.BASE, types = [Department::class])
interface BaseDepartmentProjection : BaseDepartment, BaseProjection


@Projection(name = Projections.SIMPLE, types = [Department::class])
interface SimpleDepartmentProjection : BaseDepartmentProjection


@Projection(name = Projections.DETAILED, types = [Department::class])
interface DetailedDepartmentProjection : BaseDepartmentProjection


@Projection(name = Projections.BASE, types = [Abo::class])
interface BaseAboProjection : BaseAbo, BaseProjection {
}

@Projection(name = Projections.SIMPLE, types = [Abo::class])
interface SimpleAboProjection : BaseAboProjection {
    fun getUser(): SimpleUserProjection
    fun getRooms(): List<BaseRoomProjection>
}

@Projection(name = Projections.DETAILED, types = [Abo::class])
interface DetailedAboProjection : BaseAboProjection {
    fun getUser(): SimpleUserProjection
    fun getRooms(): List<DetailedRoomProjection>
    fun getReservations(): List<SimpleReservationProjection>
}


/*
    User / Raum / Date
        Mehr -> weniger logik im FE
            -> weniger payload
            -> häufigere Abfragen

    [AboInfo {
        raum
        (start)
        (end)
        abo_id

        contingent
        used
        available
        reservations
    }]
 */
//@Projection(name = "info", types = [Abo::class])
//interface AboInfoProjection : BaseAboProjection {
//    fun getUser(): SimpleUserProjection
//    fun getRooms(): List<DetailedRoomProjection>
//
//    @Value("#{target.rooms.size}")
//    fun getUsed(): Long
//
//}


@Projection(name = Projections.BASE, types = [Reservation::class])
interface BaseReservationProjection : BaseReservation, BaseProjection {
}

@Projection(name = Projections.SIMPLE, types = [Reservation::class])
interface SimpleReservationProjection : BaseReservationProjection {
    fun getUser(): SimpleUserProjection
    fun getRoom(): BaseRoomProjection
//    fun getContingents(): List<SimpleContingentAllocationProjection>
}

@Projection(name = Projections.DETAILED, types = [Reservation::class])
interface DetailedReservationProjection : BaseReservationProjection {
    fun getUser(): SimpleUserProjection
    fun getRoom(): DetailedRoomProjection
}


@Projection(name = Projections.BASE, types = [User::class])
interface BaseUserProjection : BaseUser, BaseProjection

@Projection(name = Projections.SIMPLE, types = [User::class])
interface SimpleUserProjection : BaseUserProjection

@Projection(name = Projections.DETAILED, types = [User::class])
interface DetailedUserProjection : BaseUserProjection


//@Projection(name = Projections.BASE, types = [ContingentAllocation::class])
//interface BaseContingentAllocationProjection : BaseContingentAllocation, BaseProjection
//
//@Projection(name = Projections.SIMPLE, types = [ContingentAllocation::class])
//interface SimpleContingentAllocationProjection : BaseContingentAllocation, BaseProjection {
//    fun getAbo(): BaseAboProjection
//}
//
//@Projection(name = Projections.DETAILED, types = [ContingentAllocation::class])
//interface DetailedContingentAllocationProjection : BaseContingentAllocation, BaseProjection {
//    fun getAbo(): SimpleAboProjection
//}