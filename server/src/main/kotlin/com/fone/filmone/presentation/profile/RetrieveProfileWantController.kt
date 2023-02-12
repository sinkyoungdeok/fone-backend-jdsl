package com.fone.filmone.presentation.profile

import com.fone.common.response.CommonResponse
import com.fone.filmone.application.profile.RetrieveProfileWantFacade
import com.fone.filmone.domain.common.Type
import com.fone.filmone.presentation.profile.RetrieveProfileWantDto.RetrieveProfileWantResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@Api(tags = ["04. Profile Info"], description = "프로필 서비스")
@RestController
@RequestMapping("/api/v1/profiles")
class RetrieveProfileWantController(
    private val retrieveProfileWantFacade: RetrieveProfileWantFacade,
) {

    @GetMapping("/wants")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "내가 찜한 프로필 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
    )
    suspend fun retrieveProfileWant(
        pageable: Pageable,
        principal: Principal,
        @RequestParam type: Type,
    ): CommonResponse<RetrieveProfileWantResponse> {
        val response = retrieveProfileWantFacade.retrieveProfileWant(pageable, principal.name, type)

        return CommonResponse.success(response)
    }
}