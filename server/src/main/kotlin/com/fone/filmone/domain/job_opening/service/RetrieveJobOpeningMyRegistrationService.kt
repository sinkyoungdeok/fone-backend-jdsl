package com.fone.filmone.domain.job_opening.service

import com.fone.common.exception.NotFoundUserException
import com.fone.filmone.domain.job_opening.repository.JobOpeningRepository
import com.fone.filmone.domain.job_opening.repository.JobOpeningScrapRepository
import com.fone.filmone.domain.user.repository.UserRepository
import com.fone.filmone.presentation.job_opening.RetrieveJobOpeningMyRegistrationDto.RetrieveJobOpeningMyRegistrationResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RetrieveJobOpeningMyRegistrationService(
    private val userRepository: UserRepository,
    private val jobOpeningRepository: JobOpeningRepository,
    private val jobOpeningScrapRepository: JobOpeningScrapRepository,
) {


    @Transactional(readOnly = true)
    suspend fun retrieveJobOpeningMyRegistrations(
        pageable: Pageable,
        email: String,
    ): RetrieveJobOpeningMyRegistrationResponse {
        val user = userRepository.findByNicknameOrEmail(null, email)
            ?: throw NotFoundUserException()

        return coroutineScope {

            val jobOpenings = async {
                jobOpeningRepository.findAllByUserId(pageable, user.id!!).content
            }

            val userJobOpeningScraps = async {
                jobOpeningScrapRepository.findByUserId(user.id!!)
            }

            RetrieveJobOpeningMyRegistrationResponse(
                jobOpenings.await(),
                userJobOpeningScraps.await(),
                pageable
            )
        }
    }
}