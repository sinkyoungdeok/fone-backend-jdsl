package com.fone.question.presentation.controller

import com.fone.common.response.CommonResponse
import com.fone.question.application.RegisterQuestionFacade
import com.fone.question.presentation.dto.RegisterQuestionDto.RegisterQuestionRequest
import com.fone.question.presentation.dto.RegisterQuestionDto.RegisterQuestionResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["02. Question Info"], description = "문의등록 서비스")
@RestController
@RequestMapping("/api/v1/questions")
class RegisterQuestionController(
    val registerQuestionFacade: RegisterQuestionFacade,
) {

    @PostMapping
    @ApiOperation(value = "문의등록 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
    )
    suspend fun registerQuestion(
        @Valid @RequestBody request: RegisterQuestionRequest
    ): CommonResponse<RegisterQuestionResponse> {
        val response = registerQuestionFacade.registerQuestion(request)
        return CommonResponse.success(response)
    }
}
