package org.k3yake.domain

/**
 * Created by katsuki-miyake on 18/03/04.
 */
data class CityDomain(val id:Int = 0,val name: String,val country:String){
    constructor(name: String,country: String):this(id=0, name = name, country = country)
}