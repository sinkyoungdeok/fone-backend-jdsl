package com.fone.filmone.presentation.competition

import com.fone.filmone.domain.competition.entity.Competition
import com.fone.filmone.domain.competition.entity.Prize
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

class RegisterCompetitionDto {

    data class RegisterCompetitionRequest(
        val title: String,
        val imageUrl: String,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        val startDate: LocalDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        val endDate: LocalDate,
        val agency: String,
        val details: String,
        val prizes: List<PrizeRequest>,
    ) {
        fun toEntity(userId: Long): Competition {
            return Competition(
                title = title,
                imageUrl = imageUrl,
                startDate = startDate,
                endDate = endDate,
                agency = agency,
                details = details,
                userId = userId,
                viewCount = 0,
            )
        }
    }

    data class PrizeRequest(
        val ranking: String,
        val prizeMoney: String,
        val agency: String,
        val competitionId: Long,
    ) {
        fun toEntity(): Prize {
            return Prize(
                ranking = ranking,
                prizeMoney = prizeMoney,
                agency = agency,
            )
        }
    }

    data class RegisterCompetitionResponse(
        val competition: CompetitionDto,
    ) {
        constructor(reqCompetition: Competition): this(
            competition = CompetitionDto(reqCompetition, mapOf())
        )
    }
}