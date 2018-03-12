package org.k3yake

import org.k3yake.domain.CityDomain
import org.k3yake.repository.CityDomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.validation.Valid
import java.io.PrintWriter
import java.io.StringWriter




/**
 * Created by katsuki-miyake on 18/02/15.
 */
@ControllerAdvice
@RestController
class CityController: ResponseEntityExceptionHandler() {

    @ExceptionHandler(CityService.ExistCityError::class)
    fun handleEntityNotFound(ex: CityService.ExistCityError): ResponseEntity<Any> {
        return ResponseEntity(CityError("重複してますよ",null),HttpStatus.CONFLICT)
    }


    @ExceptionHandler(Throwable::class)
    fun handleEntityNotFound(ex: Throwable): ResponseEntity<Any> {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        ex.printStackTrace(pw)
        return ResponseEntity(CityError(ex.message.orEmpty(), sw.toString()),HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Autowired
    lateinit var cityService:CityService

    @PostMapping("/city")
    fun createCity(@Valid @RequestBody requet: CityRequest):CityResponse {
        val created = cityService.create(CityDomain(requet.name, requet.country))
        return CityResponse(created.id)
    }

    data class CityRequest(val name:String="",val country:String="")
    data class CityResponse(val id:Int)
    data class CityError(val message:String,val detail:String?)
}

@Transactional
@Service
class CityService {

    @Autowired
    lateinit var cityDomainRepositoryRepository: CityDomainRepository

    fun create(city: CityDomain):CityDomain {
        cityDomainRepositoryRepository.find(city.name)?.let {
            throw ExistCityError()
        }
        return cityDomainRepositoryRepository.create(city)
    }

    class ExistCityError :RuntimeException() {

    }
}


