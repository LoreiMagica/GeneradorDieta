package com.lorei.generadorDieta.model

/**
 * Clase contenedora de los datos de los links
 */
data class Receta(
    var id : Int? = null, //Id único de esta receta
    var nombre: String? = null, // Nombre
    var categoria: List<Int>, //categorías a las que pertenece la receta
    var horas: List<Int>, //Horas a las que pertenece la receta
    var preparacion: String? = null, //Cómo se prepara esta receta
    var ingredientes: List<String>, //ingredientes necesarios para la receta
    var calorias: Int? = null, //Cantidad de calorías que tiene esta receta
    var url: String? = null, // Url para encontrar la receta en internet
)