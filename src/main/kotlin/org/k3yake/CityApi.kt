package org.k3yake

import org.k3yake.domain.CityDomain
import org.k3yake.repository.CityDomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.validation.Valid


/**
 * Created by katsuki-miyake on 18/02/15.
 */
@ControllerAdvice
@RestController
class CityController: ResponseEntityExceptionHandler() {

    @Autowired
    lateinit var cityService:CityService

    @PostMapping("/city")
    fun createCity(@Valid @RequestBody requet: CityRequest):CityResponse {
        val created = cityService.create(CityDomain(requet.name, requet.country))
        return CityResponse(created.id)
    }

    data class CityRequest(val name:String="",val country:String="")
    data class CityResponse(val id:Int)
}

@Transactional
@Service
class CityService {

    @Autowired
    lateinit var cityDomainRepositoryRepository: CityDomainRepository

    fun create(city: CityDomain):CityDomain {
        return cityDomainRepositoryRepository.create(city)
    }
}

