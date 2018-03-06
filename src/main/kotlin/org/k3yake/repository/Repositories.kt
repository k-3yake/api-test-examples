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

    fun findCity(name: String): CityDomain {
        val city = cityRepository.findByName(name)
        return CityDomain(city.id,city.name,city.country.name)
    }

    fun create(city: CityDomain) {
        val existCountry = countryRepository.findByName(city.country)
        if(existCountry == null){
            cityRepository.save(City(name = city.name, country = countryRepository.save(Country(name = city.country))))
        } else {
            cityRepository.save(City(name = city.name, country = existCountry))
        }
    }
}

@Repository
interface CityRepository : JpaRepository<City,Long> {
    fun findByNameAndCountryId(name: String,id: Long): City
    fun findByName(name: String): City
}

@Entity
data class City(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(nullable = false)
    val name: String = "",
    @Column
    val state: String = "",
    @Column
    val map: String = "",
    @ManyToOne
    var country: Country = Country()
){
    fun country():String{
        return this.country.name
    }
}

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
