package com.fone.jobOpening.infrastructure

import com.fone.common.entity.Type
import com.fone.jobOpening.domain.entity.JobOpening
import com.fone.jobOpening.domain.entity.JobOpeningCategory
import com.fone.jobOpening.domain.entity.JobOpeningDomain
import com.fone.jobOpening.domain.entity.JobOpeningScrap
import com.fone.jobOpening.domain.repository.JobOpeningRepository
import com.fone.jobOpening.presentation.RetrieveJobOpeningDto.RetrieveJobOpeningsRequest
import com.linecorp.kotlinjdsl.query.spec.OrderSpec
import com.linecorp.kotlinjdsl.query.spec.predicate.EqualValueSpec
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.reactive.query.*
import com.linecorp.kotlinjdsl.spring.reactive.querydsl.SpringDataReactiveCriteriaQueryDsl
import com.linecorp.kotlinjdsl.spring.reactive.querydsl.SpringDataReactivePageableQueryDsl
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class JobOpeningRepositoryImpl(
    private val sessionFactory: Mutiny.SessionFactory,
    private val queryFactory: SpringDataHibernateMutinyReactiveQueryFactory,
) : JobOpeningRepository {

    override suspend fun findAllTop5ByType(pageable: Pageable, type: Type): Slice<JobOpening> {
        return queryFactory.pageQuery(pageable) {
            select(entity(JobOpening::class))
            from(entity(JobOpening::class))
            where(
                and(
                    typeEq(type)
                )
            )
        }
    }

    override suspend fun findByFilters(
        pageable: Pageable,
        request: RetrieveJobOpeningsRequest,
    ): Slice<JobOpening> {
        val domainJobOpeningIds = queryFactory.listQuery {
            select(col(JobOpeningDomain::jobOpeningId))
            from(entity(JobOpeningDomain::class))
            where(col(JobOpeningDomain::type).`in`(request.domains))
        }

        val categoryJobOpeningIds = queryFactory.listQuery {
            select(col(JobOpeningCategory::jobOpeningId))
            from(entity(JobOpeningCategory::class))
            where(col(JobOpeningCategory::type).`in`(request.categories))
        }

        if (domainJobOpeningIds.isEmpty() || categoryJobOpeningIds.isEmpty()) {
            return PageImpl(
                listOf(),
                pageable,
                0,
            )
        }

        val ids = queryFactory.pageQuery(pageable) {
            select(column(JobOpening::id))
            from(entity(JobOpening::class))
            where(
                and(
                    col(JobOpening::type).equal(request.type),
                    col(JobOpening::gender).`in`(request.genders),
                    or(
                        col(JobOpening::ageMax).greaterThanOrEqualTo(request.ageMin),
                        col(JobOpening::ageMin).lessThanOrEqualTo(request.ageMax),
                    ),
                    col(JobOpening::id).`in`(domainJobOpeningIds),
                    col(JobOpening::id).`in`(categoryJobOpeningIds),
                )
            )
        }.content

        val jobOpenings = queryFactory.listQuery {
            select(entity(JobOpening::class))
            from(entity(JobOpening::class))
            where(
                col(JobOpening::id).`in`(ids)
            )
            orderBy(
                orderSpec(pageable.sort),
            )
        }

        return PageImpl(
            jobOpenings, pageable, jobOpenings.size.toLong()
        )
    }

    override suspend fun findByTypeAndId(type: Type?, jobOpeningId: Long?): JobOpening? {
        return queryFactory.singleQueryOrNull {
            select(entity(JobOpening::class))
            from(entity(JobOpening::class))
            where(
                and(
                    typeEqOrNull(type),
                    jobOpeningIdEq(jobOpeningId),
                )
            )
        }
    }

    override suspend fun findAllByUserId(pageable: Pageable, userId: Long): Slice<JobOpening> {
        return queryFactory.pageQuery(pageable) {
            select(entity(JobOpening::class))
            from(entity(JobOpening::class))
            where(userIdEq(userId))
        }
    }

    override suspend fun findScrapAllByUserId(
        pageable: Pageable,
        userId: Long,
        type: Type,
    ): Slice<JobOpening> {
        val jobOpeningIds = queryFactory.subquery {
            select(column(JobOpeningScrap::id))
            from(entity(JobOpeningScrap::class))
            where(col(JobOpeningScrap::userId).equal(userId))
        }

        return queryFactory.pageQuery(pageable) {
            select(entity(JobOpening::class))
            from(entity(JobOpening::class))
            where(
                and(
                    col(JobOpening::id).`in`(jobOpeningIds),
                    typeEq(type),
                )
            )
        }
    }

    override suspend fun save(jobOpening: JobOpening): JobOpening {
        return jobOpening.also {
            queryFactory.withFactory { session, factory ->
                if (it.id == null) {
                    session.persist(it)
                } else {
                    session.merge(it)
                }
                    .flatMap { session.flush() }
                    .awaitSuspending()
            }
        }
    }

    private fun SpringDataReactiveCriteriaQueryDsl<JobOpening?>.jobOpeningIdEq(
        jobOpeningId: Long?,
    ) = col(JobOpening::id).equal(jobOpeningId)

    private fun SpringDataReactivePageableQueryDsl<JobOpening>.idIn(
        jobOpeningIds: List<Long>,
    ) = col(JobOpening::id).`in`(jobOpeningIds)

    private fun SpringDataReactivePageableQueryDsl<JobOpening>.userIdEq(
        userId: Long,
    ) = col(JobOpening::userId).equal(userId)

    private fun SpringDataReactiveCriteriaQueryDsl<JobOpening?>.typeEqOrNull(
        type: Type?,
    ): EqualValueSpec<Type>? {
        type ?: return null

        return col(JobOpening::type).equal(type)
    }

    private fun SpringDataReactiveCriteriaQueryDsl<JobOpening>.typeEq(
        type: Type?,
    ): EqualValueSpec<Type>? {
        type ?: return null

        return col(JobOpening::type).equal(type)
    }

    private fun SpringDataReactivePageableQueryDsl<JobOpening>.typeEq(
        type: Type?,
    ): EqualValueSpec<Type>? {
        type ?: return null
        return col(JobOpening::type).equal(type)
    }

    private fun SpringDataReactiveCriteriaQueryDsl<JobOpening?>.orderSpec(sort: Sort): List<OrderSpec> {
        val endDate = case(
            `when`(column(JobOpening::deadline).lessThanOrEqualTo(LocalDate.now())).then(
                literal(1)
            ),
            `else` = literal(0)
        ).asc()

        val res = sort.map {
            val columnSpec = when (it.property) {
                "viewCount" -> col(JobOpening::viewCount)
                "createdAt" -> col(JobOpening::createdAt)
                "scrapCount" -> col(JobOpening::scrapCount)
                else -> col(JobOpening::viewCount)
            }

            if (it.isAscending) {
                columnSpec.asc()
            } else {
                columnSpec.desc()
            }
        }.toList()

        return listOf(endDate) + res
    }
}