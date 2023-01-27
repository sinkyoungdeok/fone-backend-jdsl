package com.fone.filmone.domain.competition.service

import com.fone.filmone.common.exception.NotFoundCompetitionException
import com.fone.filmone.common.exception.NotFoundUserException
import com.fone.filmone.domain.competition.entity.CompetitionScrap
import com.fone.filmone.domain.competition.repository.CompetitionRepository
import com.fone.filmone.domain.competition.repository.CompetitionScrapRepository
import com.fone.filmone.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScrapCompetitionService(
    private val userRepository: UserRepository,
    private val competitionRepository: CompetitionRepository,
    private val competitionScrapRepository: CompetitionScrapRepository,
) {

    @Transactional
    suspend fun scrapCompetition(email: String, competitionId: Long) {
        val user = userRepository.findByNicknameOrEmail(null, email)
            ?: throw NotFoundUserException()

        competitionScrapRepository.findByUserIdAndCompetitionId(user.id!!, competitionId)
            ?.let {
                competitionScrapRepository.delete(it)
                return
            }

        val competition = competitionRepository.findById(competitionId)
            ?: throw NotFoundCompetitionException()

        competitionScrapRepository.save(CompetitionScrap(user.id!!, competition))
    }
}