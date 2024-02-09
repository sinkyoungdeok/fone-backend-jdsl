package com.fone.jobOpening.presentation.controller

import com.fone.common.response.CommonResponse
import com.fone.jobOpening.application.ValidateJobOpeningFacade
import com.fone.jobOpening.presentation.dto.ValidateJobOpeningDto
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["03. Job Opening Info"], description = "구인구직 모집 서비스")
@RestController
@RequestMapping("/api/v1/job-openings/validate")
class ValidateJobOpeningController(
    private val validateJobOpeningFacade: ValidateJobOpeningFacade,
) {
    @PostMapping("/contact")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "구인구직 검증 API - 페이지1")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    suspend fun contactPageValidation(
        @RequestBody
        request: ValidateJobOpeningDto.FirstPage,
    ): CommonResponse<Unit> {
        validateJobOpeningFacade.validateContactPage(request)
        return CommonResponse.success()
    }

    @PostMapping("/title")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "구인구직 검증 API - 페이지2")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    suspend fun titlePageValidation(
        @RequestBody
        request: ValidateJobOpeningDto.SecondPage,
    ): CommonResponse<Unit> {
        validateJobOpeningFacade.validateTitlePage(request)
        return CommonResponse.success()
    }

    @PostMapping("/role")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "구인구직 검증 API - 페이지3")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    suspend fun rolePageValidation(
        @RequestBody
        request: ValidateJobOpeningDto.ThirdPage,
    ): CommonResponse<Unit> {
        validateJobOpeningFacade.validateRolePage(request)
        return CommonResponse.success()
    }

    @PostMapping("/project")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "구인구직 검증 API - 페이지4")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    suspend fun projectPageValidation(
        @RequestBody
        request: ValidateJobOpeningDto.FourthPage,
    ): CommonResponse<Unit> {
        validateJobOpeningFacade.validateProjectPage(request)
        return CommonResponse.success()
    }

    @PostMapping("/project-details")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "구인구직 검증 API - 페이지5")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    suspend fun projectDetailsPageValidation(
        @RequestBody
        request: ValidateJobOpeningDto.FifthPage,
    ): CommonResponse<Unit> {
        validateJobOpeningFacade.validateProjectDetailsPage(request)
        return CommonResponse.success()
    }

    @PostMapping("/summary")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "구인구직 검증 API - 페이지6")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    suspend fun projectSummaryPageValidation(
        @RequestBody
        request: ValidateJobOpeningDto.SixthPage,
    ): CommonResponse<Unit> {
        validateJobOpeningFacade.validateSummaryPage(request)
        return CommonResponse.success()
    }

    @PostMapping("/manager")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "구인구직 검증 API - 페이지7")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    suspend fun projectManagerPageValidation(
        @RequestBody
        request: ValidateJobOpeningDto.SeventhPage,
    ): CommonResponse<Unit> {
        validateJobOpeningFacade.validateManagerPage(request)
        return CommonResponse.success()
    }
}