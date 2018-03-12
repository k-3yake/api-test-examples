package org.k3yake.repository

import org.k3yake.domain.CityDomain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.*


/**
 * Created by katsuki-miyake on 18/02/27.
 */
@Repository
class CityDomainRepository {
    @Autowired
    private lateinit var cityRepository: CityRepository
    @Autowired
    private lateinit var countryRepository: CountryRepository
    @Autowired
    lateinit var populationApi: PopulationApi

    fun create(city: CityDomain):CityDomain {
        val existCountry = countryRepository.findByName(city.country)
        val population = populationApi.get(city.name)
        if(existCountry == null){
            val saved = cityRepository.save(City(name = city.name, country = countryRepository.save(Country(name = city.country)),population = population.value))
            return city.copy(id=saved.id)
        } else {
            val saved = cityRepository.save(City(name = city.name, country = existCountry,population = population.value))
            return city.copy(id=saved.id)
        }
    }

    fun find(name: String): CityDomain? {
        val city = cityRepository.findByName(name)
        city?.let {
            return CityDomain(city.id, city.name, city.country.name)
        }
        return null
    }
}

@Repository
private interface CityRepository : JpaRepository<City,Long> {
    fun findByName(name: String): City?
}

@Entity
private data class City(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(nullable = false)
    val name: String = "",
    @ManyToOne
    var country: Country = Country(),
    @Column
    val population: Int? = -1
)

@Repository
private interface CountryRepository : JpaRepository<Country,Long> {
    fun findByName(name: String): Country?
}

@Entity
private data class Country(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false,unique = true)
    val name: String = ""
){
    constructor(name: String):this(id = 0,name = name)
}

@Repository
class PopulationApi {
    open class PopulationApiResponse(val name: String, val value: Int)

    fun  get(name: String): PopulationApiResponse {
        throw UnsupportedOperationException()
    }
}

