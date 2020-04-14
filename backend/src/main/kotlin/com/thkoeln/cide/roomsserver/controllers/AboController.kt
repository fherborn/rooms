package com.thkoeln.cide.roomsserver.controllers

import com.thkoeln.cide.roomsserver.extensions.fixEmbedded
import com.thkoeln.cide.roomsserver.models.ReservationRepository
import com.thkoeln.cide.roomsserver.models.RoomRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*

data class Slot(
        val start: OffsetDateTime,
        val end: OffsetDateTime,
        val minutes: Long
)

@RepositoryRestController
@RequestMapping("/rooms")
class RoomController @Autowired constructor(
        private val roomRepository: RoomRepository,
        private val reservationRepository: ReservationRepository
) {

    private val logger = LoggerFactory.getLogger(RoomController::class.java)

    //    @PreAuthorize("hasPermission(#id, 'read')")
    @GetMapping("/{id}/slots")
    fun getSlots(@PathVariable id: UUID, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) date: OffsetDateTime): ResponseEntity<CollectionModel<out Any>> {
        val dayStart = date.withHour(0).withMinute(0).withSecond(0)
        val dayEnd = date.withHour(23).withMinute(59).withSecond(59)
        val officeStart = date.withHour(7).withMinute(0).withSecond(0)
        val officeEnd = date.withHour(23).withMinute(0).withSecond(0)
        val reservations = reservationRepository.findForDate(id, dayStart, dayEnd)
        val result = mutableListOf<Slot>()

        if (reservations.isEmpty()) {
            result.add(Slot(officeStart, officeEnd, Duration.between(officeStart, officeEnd).toMinutes()))
        } else {
            var lastEnd = officeStart

            reservations.forEach { r ->
                val duration = Duration.between(lastEnd, r.start).toMinutes()
                if (duration >= 15) {
                    result.add(Slot(lastEnd, r.start, duration))
                }
                if (r.end.isAfter(lastEnd)) {
                    lastEnd = r.end
                }
            }

            if (lastEnd.isBefore(officeEnd)) {
                result.add(Slot(lastEnd, officeEnd, Duration.between(lastEnd, officeEnd).toMinutes()))
            }
        }

        return result.let {
            CollectionModel(it).apply {
                add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RoomController::class.java).getSlots(id, date)).withSelfRel())
            }.fixEmbedded(Slot::class.java)
        }.let { ResponseEntity(it, HttpStatus.OK) }

    }
}