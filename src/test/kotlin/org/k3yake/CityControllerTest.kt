package org.k3yake

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.http.HttpStatus

class CityControllerTest{

    @Test
    fun City重複のエラー処理のテスト_409を返す(){
        val responseEntity = CityController().handleEntityNotFound(CityService.ExistCityError())
        Assertions.assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }
}
