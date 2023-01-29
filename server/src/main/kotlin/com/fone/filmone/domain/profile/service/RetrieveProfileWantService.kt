package com.fone.filmone.domain.profile.service

import com.fone.common.exception.NotFoundUserException
import com.fone.filmone.domain.common.Type
import com.fone.filmone.domain.profile.repository.ProfileRepository
import com.fone.filmone.domain.profile.repository.ProfileWantRepository
import com.fone.filmone.domain.user.repository.UserRepository
import com.fone.filmone.presentation.profile.RetrieveProfileWantDto.RetrieveProfileWantResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RetrieveProfileWantService(
    private val profileWantRepository: ProfileWantRepository,
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    suspend fun retrieveProfileWant(
        pageable: Pageable,
        email: String,
        type: Type,
    ): RetrieveProfileWantResponse {
        val user = userRepository.findByNicknameOrEmail(null, email)
            ?: throw NotFoundUserException()

        return coroutineScope {
            val profiles = async {
                profileRepository.findWantAllByUserId(pageable, user.id!!, type).content
            }

            val userProfileWants = async {
                profileWantRepository.findByUserId(user.id!!)
            }

            RetrieveProfileWantResponse(profiles.await(), userProfileWants.await(), pageable)
        }
    }
}