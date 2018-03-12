package org.k3yake

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.junit.Test
import org.k3yake.domain.CityDomain
import org.k3yake.repository.CityDomainRepository

class CityServiceTest {
    @Tested
    lateinit var cityService: CityService
    @Injectable
    lateinit var cityDomainRepository: CityDomainRepository


    @Test(expected = CityService.ExistCityError::class)
    fun City登録のテスト_既に登録されている都市の場合_エラーを返す(){
        val city = CityDomain(1, "existed", "existed")
        object : Expectations(){init{
            cityDomainRepository.find("existed");result = city
        }}
        cityService.create(city)
    }
}