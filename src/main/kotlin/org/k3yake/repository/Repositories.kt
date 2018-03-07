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
    @Autowired lateinit var cityRepository: CityRepository
    @Autowired lateinit var countryRepository: CountryRepository

    fun findCity(name: String): CityDomain? {
        val city = cityRepository.findByName(name)?.let { it ->
            return CityDomain(it.id,it.name,it.country.name)
        }
        return null
    }

    fun create(city: CityDomain):CityDomain {
        val existCountry = countryRepository.findByName(city.country)
        if(existCountry == null){
            val saved = cityRepository.save(City(name = city.name, country = countryRepository.save(Country(name = city.country))))
            return city.copy(id=saved.id)
        } else {
            val saved = cityRepository.save(City(name = city.name, country = existCountry))
            return city.copy(id=saved.id)
        }
    }
}

@Repository
interface CityRepository : JpaRepository<City,Long> {
    fun findByName(name: String): City?
}

@Entity
data class City(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(nullable = false)
    val name: String = "",
    @ManyToOne
    var country: Country = Country()
)

@Repository
interface CountryRepository : JpaRepository<Country,Long> {
    fun findByName(name: String): Country?
}

@Entity
data class Country(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false,unique = true)
    val name: String = ""
){
    constructor(name: String):this(id = 0,name = name)
}
