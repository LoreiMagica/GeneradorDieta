import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.model.Receta
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Clase la cual obtiene los eventos de Isla Cristina desde firebase y los manda al adapter
 */
class DietaViewModel : ViewModel() {

    //Este LiveData es para mostrar un toast desde aquí
    private val _mostrarToast = MutableLiveData<Int>()
    val mostrarToast: LiveData<Int> get() = _mostrarToast

    //Este LiveData es para copiar la compra al portapapeles
    private val _copiarCompra = MutableLiveData<String>()
    val copiarCompra: LiveData<String> get() = _copiarCompra

    //Este LiveData es para actualizar la dieta de hoy
    private val _actualizarDieta = MutableLiveData<String>()
    val actualizarDieta: LiveData<String> get() = _actualizarDieta

    //Arrays para llenarlos del listado de recetas
    val dataPrimerPlato = mutableListOf<Receta>()
    val dataAcompanamiento = mutableListOf<Receta>()
    val dataMediodia = mutableListOf<Receta>()
    val dataDesayuno = mutableListOf<Receta>()
    val dataMediamanana = mutableListOf<Receta>()
    val dataMerienda = mutableListOf<Receta>()
    val dataCena = mutableListOf<Receta>()
    val dataPostre = mutableListOf<Receta>()

    //Creamos los arrays finales para almacenar las recetas
    var semanaMediodia = arrayListOf<Receta>()
    var semanaAcompanamientoMediodia = arrayListOf<Receta>()
    var semanaCena = arrayListOf<Receta>()
    var semanaAcompanamientoCena = arrayListOf<Receta>()
    var semanaDesayuno = arrayListOf<Receta>()
    var semanaPostre = arrayListOf<Receta>()
    var semanaMediamanana = arrayListOf<Receta>()
    var semanaMerienda = arrayListOf<Receta>()
    lateinit var arrayNombres: Array<String>


    fun generarDieta(baseGuardado: SQLiteDatabase, context: Context, diasSemana: List<String>, dieta : Int) {
        arrayNombres = context.resources.getStringArray(R.array.receta_categoria)
        //Cargamos los datos de recetas en sus arrays
        val todoCorrecto = cargarRecetas(baseGuardado, dieta)
        if (todoCorrecto) {
            var carne = 0
            var pescado = 0
            var legumbres = 0
            var cereal = 0
            var huevo = 0
            var verdura = 0
            //Omnívora
            when (dieta) {
                0 -> {
                    carne = 7
                    pescado = 2
                    legumbres = (0..1).random()
                    cereal = (0..1).random()
                    huevo = (0..1).random()

                    //Forzamos a que al menos haya dos comidas más para que no se llene toda la semana de carne
                    if (legumbres + cereal + huevo < 2) {
                        legumbres = 1
                        cereal = 1
                        /* when ((0..2).random()){
                                0 -> {
                                    legumbres = 1
                                    cereal = 1
                                }
                                1 -> {
                                    legumbres = 1
                                    huevo = 1
                                }
                                2 -> {
                                    cereal =1
                                    huevo = 1
                                }
                                else -> {
                                    legumbres = 1
                                    cereal = 1
                                    huevo = 1
                                }
                            }

                            */
                    }

                    //Restamos to,do a la carne para obtener el resultado de comidas en esta semana
                    carne -= (pescado + legumbres + cereal + huevo)

                    //Vegetariana
                }
                1 -> {
                    verdura = 7
                    legumbres = (0..2).random()
                    cereal = (0..2).random()
                    huevo = (0..2).random()

                    //Forzamos a que al menos haya dos comidas más para que no se llene toda la semana de carne
                    if (legumbres + cereal + huevo < 4) {
                        legumbres = 2
                        cereal = 2
                        huevo = 1
                        /* when ((0..2).random()){
                                0 -> {
                                    legumbres = 1
                                    cereal = 1
                                }
                                1 -> {
                                    legumbres = 1
                                    huevo = 1
                                }
                                2 -> {
                                    cereal =1
                                    huevo = 1
                                }
                                else -> {
                                    legumbres = 1
                                    cereal = 1
                                    huevo = 1
                                }
                            }

                            */
                    }

                    //Restamos to,do a la carne para obtener el resultado de comidas en esta semana
                    verdura -= (pescado + legumbres + cereal + huevo)
                    //Vegana
                }
                else -> {
                    verdura = 7
                    legumbres = (0..3).random()
                    cereal = (0..3).random()

                    //Forzamos a que al menos haya dos comidas más para que no se llene toda la semana de carne
                    if (legumbres + cereal + huevo < 5) {
                        legumbres = 3
                        cereal = 2
                        /* when ((0..2).random()){
                                0 -> {
                                    legumbres = 1
                                    cereal = 1
                                }
                                1 -> {
                                    legumbres = 1
                                    huevo = 1
                                }
                                2 -> {
                                    cereal =1
                                    huevo = 1
                                }
                                else -> {
                                    legumbres = 1
                                    cereal = 1
                                    huevo = 1
                                }
                            }

                            */
                    }

                    //Restamos to,do a la carne para obtener el resultado de comidas en esta semana
                    verdura -= (pescado + legumbres + cereal + huevo)
                }
            }


            //Rellenamos el primer array con el tipo de comida del día
            semanaDesayuno.add(
                Receta(
                    0,
                    arrayNombres.getOrElse(13) { "Desayuno" },
                    listOf(0),
                    listOf(0),
                    "a",
                    listOf(""),
                    0,
                    "a"
                )
            )
            semanaMediamanana.add(
                Receta(
                    0,
                    arrayNombres.getOrElse(14) { "Mediamañana" },
                    listOf(0),
                    listOf(0),
                    "a",
                    listOf(""),
                    0,
                    "a"
                )
            )
            semanaMediodia.add(
                Receta(
                    0,
                    arrayNombres.getOrElse(12) { "Comida mediodía" },
                    listOf(0),
                    listOf(0),
                    "a",
                    listOf(""),
                    0,
                    "a"
                )
            )
            semanaAcompanamientoMediodia.add(Receta(0, "", listOf(0), listOf(0), "a", listOf(""), 0, "a"))
            semanaPostre.add(Receta(0, "", listOf(0), listOf(0), "a", listOf(""), 0, "a"))
            semanaMerienda.add(
                Receta(
                    0,
                    arrayNombres.getOrElse(15) { "Merienda" },
                    listOf(0),
                    listOf(0),
                    "a",
                    listOf(""),
                    0,
                    "a"
                )
            )
            semanaCena.add(
                Receta(
                    0,
                    arrayNombres.getOrElse(16) { "Cena" },
                    listOf(0),
                    listOf(0),
                    "a",
                    listOf(""),
                    0,
                    "a"
                )
            )
            semanaAcompanamientoCena.add(Receta(0, "", listOf(0), listOf(0), "a", listOf(""), 0, "a"))

            //Rellenamos los arrays para la dieta de la semana
            for (i in 1 until 8) {
                semanaDesayuno.add(Receta(0, "", listOf(0), listOf(0),"a", listOf(""), 0, "a"))
                semanaMediamanana.add(Receta(0, "", listOf(0), listOf(0), "a", listOf(""), 0, "a"))
                semanaMediodia.add(Receta(0, "", listOf(0), listOf(0), "a", listOf(""), 0, "a"))
                semanaAcompanamientoMediodia.add(
                    Receta(
                        0,
                        "",
                        listOf(0),
                        listOf(0),
                        "a",
                        listOf(""),
                        0,
                        "a"
                    )
                )
                semanaPostre.add(Receta(0, "", listOf(0), listOf(0),"a", listOf(""), 0, "a"))
                semanaMerienda.add(Receta(0, "", listOf(0),listOf(0), "a", listOf(""), 0, "a"))
                semanaCena.add(Receta(0, "", listOf(0), listOf(0), "a", listOf(""), 0, "a"))
                semanaAcompanamientoCena.add(Receta(0, "", listOf(0), listOf(0), "a", listOf(""), 0, "a"))
            }

            // Crear un mapa con las variables y sus valores
            val semanaComidas = mapOf(
                1 to carne,
                2 to pescado,
                3 to legumbres,
                4 to cereal,
                5 to huevo,
                6 to verdura

            )

            // Crear una lista con las variables repetidas el número de veces que tienen almacenado
            val listaRepeticiones = semanaComidas.flatMap { (nombre, cantidad) ->
                List(cantidad) { nombre }
            }

            var dia = 1
            val arrayFav = ArrayList<Receta>()
            //Hacemos la consulta a la BBDD
            val cursorFav: Cursor = baseGuardado.rawQuery(
                "Select name from sqlite_master where type = 'table' and name like 'dietaFavs' ",
                null
            )
            if (cursorFav.count > 0) {
                //Accedemos a la tabla dietaActual en la base de datos
                val cursorReceta: Cursor = baseGuardado.rawQuery(
                    "select comida from dietaFavs ",
                    null
                )

                //Procesamos los datos obtenidos
                if (cursorReceta.count > 0) {
                    while (cursorReceta.moveToNext()) {
                        val gson = Gson()

                        //Obtenemos y convertimos los datos de gson
                        val jsonD =
                            cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("comida"))

                        //Comprobamos que los datos obtenidos sean un array y no esté vacío
                        if (jsonD != "") {
                            arrayFav.add(gson.fromJson(jsonD, Receta::class.java))
                        }
                    }
                }
                cursorReceta.close()
            }
            cursorFav.close()



            // Imprimir las variables de forma aleatoria. Se repite para obtener una semana completa
            listaRepeticiones.shuffled().forEach {

                if (dataDesayuno.size >= 1) {
                   /* var coincideD = false
                    var idD = 0
                    for (item in 0 until arrayFav.size) {
                        if (arrayFav[item].categoria.contains(arrayNombres.getOrElse(13) { "Desayuno" })
                        ) {
                            semanaDesayuno[dia] = arrayFav[item]
                            coincideD = true
                            idD = item
                        }
                    }
                    if (coincideD) {
                        arrayFav.removeAt(idD)
                        Log.d("or2", arrayFav.toString())

                    }


                    */
                    val receta = arrayFav.firstOrNull { it.horas.contains(13)  }
                    if (receta != null) {
                        semanaDesayuno[dia] = receta
                        arrayFav.remove(receta)
                    } else
                    if (semanaDesayuno[dia].nombre == "") {
                        when (dieta) {
                            0 -> {
                                semanaDesayuno[dia] = dataDesayuno.randomOrNull()!!
                            }
                            1 -> {
                                val resultados = dataDesayuno.filter { receta ->
                                    ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                                }
                                if (resultados.size > 0) {
                                    semanaDesayuno[dia] = resultados.randomOrNull()!!
                                } else {
                                    semanaDesayuno[dia] = dataDesayuno.randomOrNull()!!
                                }
                            }
                            2 -> {
                                val resultados = dataDesayuno.filter { receta ->
                                    ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(6) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(8) == false)
                                }
                                if (resultados.size > 0) {
                                    semanaDesayuno[dia] = resultados.randomOrNull()!!
                                } else {
                                    semanaDesayuno[dia] = dataDesayuno.randomOrNull()!!
                                }
                            }
                        }
                    }
                }
                if (dataMediamanana.size >= 1) {
                    /*var coincideMedia = false
                    var idMedia = 0
                    for (item in 0 until arrayFav.size) {
                        if (arrayFav[item].categoria.contains(arrayNombres.getOrElse(14) { "Mediamañana" })
                        ) {
                            semanaMediamanana[dia] = arrayFav[item]
                            coincideMedia = true
                            idMedia = item
                        }
                    }
                    if (coincideMedia) {
                        arrayFav.removeAt(idMedia)
                        Log.d("or2acco", arrayFav.toString())
                    }

                     */
                    val receta = arrayFav.firstOrNull { it.horas.contains(14) }
                    if (receta != null) {
                        semanaDesayuno[dia] = receta
                        arrayFav.remove(receta)
                    } else
                    if (semanaMediamanana[dia].nombre == "") {
                        when (dieta) {
                            0 -> {
                                semanaMediamanana[dia] = dataMediamanana.randomOrNull()!!
                            }
                            1 -> {
                                val resultados = dataMediamanana.filter { receta ->
                                    ((receta.categoria as? List<Int>)?.contains(1) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(2) == false)
                                }
                                if (resultados.size > 0) {
                                    semanaMediamanana[dia] = resultados.randomOrNull()!!
                                } else {
                                    semanaMediamanana[dia] = dataMediamanana.randomOrNull()!!
                                }
                            }
                            2 -> {
                                val resultados = dataMediamanana.filter { receta ->
                                    ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(6) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(8) == false)
                                }
                                if (resultados.size > 0) {
                                    semanaMediamanana[dia] = resultados.randomOrNull()!!
                                } else {
                                    semanaMediamanana[dia] = dataMediamanana.randomOrNull()!!
                                }
                            }
                        }
                    }

                }
                /*var coincideMedio = false
                var idMedio = 0
                for (item in 0 until arrayFav.size) {
                    if (arrayFav[item].categoria.contains(arrayNombres.getOrElse(12) { "Comida mediodía" })
                    ) {
                        semanaMediodia[dia] = arrayFav[item]
                        coincideMedio = true
                        idMedio = item
                    }
                }
                if (coincideMedio) {
                    arrayFav.removeAt(idMedio)
                    Log.d("or2com", arrayFav.toString())
                }
                if (semanaMediodia[dia].nombre == "") { */
                val semanaOcupada = setOf<Int>(
                    semanaMediodia[1].id!!.toInt(),
                    semanaMediodia[2].id!!.toInt(),
                    semanaMediodia[3].id!!.toInt(),
                    semanaMediodia[4].id!!.toInt(),
                    semanaMediodia[5].id!!.toInt(),
                    semanaMediodia[6].id!!.toInt(),
                    semanaMediodia[7].id!!.toInt())
                    semanaMediodia[dia] = obtenerMediodia(
                        it,
                        semanaOcupada,
                        dieta,
                        arrayFav
                    )


                if (semanaMediodia[dia].horas.contains(10)) {
                       /* var coincide = false
                        var id = 0
                        for (item in 0 until arrayFav.size) {
                            if (arrayFav[item].categoria.contains(arrayNombres.getOrElse(11) { "Acompañamiento" }) && arrayFav[item].categoria.contains(
                                    arrayNombres.getOrElse(12) { "Comida mediodía" }
                                )) {
                                semanaAcompanamientoMediodia[dia] = arrayFav[item]
                                coincide = true
                                id = item
                            }
                        }
                        if (coincide) {
                            arrayFav.removeAt(id)
                            Log.d("or2Po", arrayFav.toString())
                        }

                        */
                    val receta = arrayFav.firstOrNull { it.horas.contains(11) && it.horas.contains(12) }
                    if (receta != null) {
                        semanaDesayuno[dia] = receta
                        arrayFav.remove(receta)
                    } else
                        if (semanaAcompanamientoMediodia[dia].nombre == "") {
                            when (dieta) {
                                0 -> {
                                    val resultados = dataAcompanamiento.filter { receta ->
                                        ((receta.horas as? List<Int>)?.contains(12) == true)
                                    }
                                    semanaAcompanamientoMediodia[dia] =
                                        resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                                }
                                1 -> {
                                    val resultados = dataAcompanamiento.filter { receta ->
                                        ((receta.horas as? List<Int>)?.contains(12) == true) &&
                                                ((receta.categoria as? List<Int>)?.contains(1) == false)
                                                && ((receta.categoria as? List<Int>)?.contains(2) == false)
                                    }
                                    if (resultados.size > 0) {
                                        semanaAcompanamientoMediodia[dia] =
                                            resultados.randomOrNull()!!
                                    } else {
                                        semanaAcompanamientoMediodia[dia] =
                                            dataAcompanamiento.randomOrNull()!!
                                    }
                                }
                                2 -> {
                                    val resultados = dataAcompanamiento.filter { receta ->
                                        ((receta.horas as? List<Int>)?.contains(12) == true) &&
                                                ((receta.categoria as? List<Int>)?.contains(1) == false)
                                                && ((receta.categoria as? List<Int>)?.contains(2) == false)
                                                && ((receta.categoria as? List<Int>)?.contains(6) == false) &&
                                                ((receta.categoria as? List<Int>)?.contains(8) == false)
                                    }
                                    if (resultados.size > 0) {
                                        semanaAcompanamientoMediodia[dia] =
                                            resultados.randomOrNull()!!
                                    } else {
                                        semanaAcompanamientoMediodia[dia] =
                                            dataAcompanamiento.randomOrNull()!!
                                    }
                                }
                            }
                        //}
                    }
                }

              /*  var coincidePo = false
                var idPo = 0
            for (item in 0 until arrayFav.size) {
                if (arrayFav[item].categoria.contains(arrayNombres.getOrElse(17) { "Postre" })
                ) {
                    semanaPostre[dia] = arrayFav[item]
                    coincidePo = true
                    idPo = item
                }
            }
                if (coincidePo) {
                    arrayFav.removeAt(idPo)
                    Log.d("or2Po", arrayFav.toString())
                }

               */
                val receta = arrayFav.firstOrNull { it.horas.contains(17) }
                if (receta != null) {
                    semanaDesayuno[dia] = receta
                    arrayFav.remove(receta)
                } else
            if (semanaPostre[dia].nombre == "") {
                if (dataPostre.size >= 1) {
                    when (dieta) {
                        0 -> {
                            semanaPostre[dia] = dataPostre.randomOrNull()!!
                        }
                        1 -> {
                            val resultados = dataPostre.filter { receta ->
                                ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                                        ((receta.categoria as? List<Int>)?.contains(2) == false)
                            }
                            if (resultados.size > 0) {
                                semanaPostre[dia] = resultados.randomOrNull()!!
                            } else {
                                semanaPostre[dia] = dataPostre.randomOrNull()!!
                            }
                        }
                        2 -> {
                            val resultados = dataPostre.filter { receta ->
                                ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                                        ((receta.categoria as? List<Int>)?.contains(2) == false) &&
                                        ((receta.categoria as? List<Int>)?.contains(6) == false) &&
                                        ((receta.categoria as? List<Int>)?.contains(8) == false)
                            }
                            if (resultados.size > 0) {
                                semanaPostre[dia] = resultados.randomOrNull()!!
                            } else {
                                semanaPostre[dia] = dataPostre.randomOrNull()!!
                            }
                        }
                    }
                }
            }

                if (dataMerienda.size >= 1) {
                    /*
                    var coincideMer = false
                    var idMer = 0
                    for (item in 0 until arrayFav.size){
                        if (arrayFav[item].categoria.contains(arrayNombres.getOrElse(15) { "Merienda" })
                        ){
                            semanaMerienda[dia] = arrayFav[item]
                            coincideMer = true
                            idMer = item
                        }
                    }
                    if (coincideMer) {
                        arrayFav.removeAt(idMer)
                        Log.d("or2Mer", arrayFav.toString())

                    }
                    */
                    val receta = arrayFav.firstOrNull { it.horas.contains(15) }
                    if (receta != null) {
                        semanaDesayuno[dia] = receta
                        arrayFav.remove(receta)
                    } else
                    if (semanaMerienda[dia].nombre == "") {
                        when (dieta) {
                            0 -> {
                                semanaMerienda[dia] = dataMerienda.randomOrNull()!!
                            }
                            1 -> {
                                val resultados = dataMerienda.filter { receta ->
                                    ((receta.categoria as? List<Int>)?.contains(1) == false) && ((receta.categoria as? List<Int>)?.contains(2) == false)
                                }
                                if (resultados.size > 0) {
                                    semanaMerienda[dia] = resultados.randomOrNull()!!
                                } else {
                                    semanaMerienda[dia] = dataMerienda.randomOrNull()!!
                                }
                            }
                            2 -> {
                                val resultados = dataMerienda.filter { receta ->
                                    ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(6) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(8) == false)
                                }
                                if (resultados.size > 0) {
                                    semanaMerienda[dia] = resultados.randomOrNull()!!
                                } else {
                                    semanaMerienda[dia] = dataMerienda.randomOrNull()!!
                                }
                            }
                        }
                    }
                }

                /*var coincideCe = false
                var idCe = 0
                for (item in 0 until arrayFav.size){
                    if (arrayFav[item].categoria.contains(arrayNombres.getOrElse(16) { "Cena" })
                    ){
                        semanaCena[dia] = arrayFav[item]
                        coincideCe = true
                        idCe = item
                    }
                }
                if (coincideCe) {
                    arrayFav.removeAt(idCe)
                    Log.d("or2Ce", arrayFav.toString())
                }
                if (semanaCena[dia].nombre == "") {

                 */
                var cenaOcupada = setOf(
                    semanaCena[1].id!!.toInt(),
                    semanaCena[2].id!!.toInt(),
                    semanaCena[3].id!!.toInt(),
                    semanaCena[4].id!!.toInt(),
                    semanaCena[5].id!!.toInt(),
                    semanaCena[6].id!!.toInt(),
                    semanaCena[7].id!!.toInt())
                semanaCena[dia] = obtenerCena(
                    it,
                    cenaOcupada,
                    dieta,
                    arrayFav
                )
                if (semanaCena[dia].horas.contains(10)) {
                   /* var coincide = false
                    var id = 0
                    for (item in 0 until arrayFav.size){
                        if (arrayFav[item].categoria.contains(arrayNombres.getOrElse(11) { "Acompañamiento" }) && arrayFav[item].categoria.contains(
                                arrayNombres.getOrElse(16) { "Cena" }
                            )){
                            semanaAcompanamientoCena[dia] = arrayFav[item]
                            coincide = true
                            id = item
                        }
                    }
                    if (coincide) {
                        arrayFav.removeAt(id)
                        Log.d("or2AcMCe", arrayFav.toString())
                    }

                    */
                    val receta = arrayFav.firstOrNull { it.horas.contains(11) && it.horas.contains(16)}
                    if (receta != null) {
                        semanaDesayuno[dia] = receta
                        arrayFav.remove(receta)
                    } else
                    if (semanaAcompanamientoCena[dia].nombre == "") {
                        when (dieta) {
                            0 -> {
                                val resultados = dataAcompanamiento.filter { receta ->
                                    ((receta.horas as? List<Int>)?.contains(16) == true)
                                }
                                semanaAcompanamientoCena[dia] =
                                    resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                            }
                            1 -> {
                                val resultados = dataAcompanamiento.filter { receta ->
                                    ((receta.horas as? List<Int>)?.contains(16) == true) &&
                                            ((receta.categoria as? List<Int>)?.contains(1) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(2) == false)
                                }
                                if (resultados.size > 0) {
                                    semanaAcompanamientoCena[dia] = resultados.randomOrNull()!!
                                } else {
                                    semanaAcompanamientoCena[dia] =
                                        dataAcompanamiento.randomOrNull()!!
                                }
                            }
                            2 -> {
                                val resultados = dataAcompanamiento.filter { receta ->
                                    ((receta.horas as? List<Int>)?.contains(16) == true) &&
                                            ((receta.categoria as? List<Int>)?.contains(1) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(2) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(6) == false)
                                            && ((receta.categoria as? List<Int>)?.contains(8) == false)
                                }
                                if (resultados.size > 0) {
                                    semanaAcompanamientoCena[dia] = resultados.randomOrNull()!!
                                } else {
                                    semanaAcompanamientoCena[dia] =
                                        dataAcompanamiento.randomOrNull()!!
                                }
                            }
                        }
                    }
                //}
                }

                dia += 1
            }
            crearMenuPdf(context, diasSemana, baseGuardado)
        }
    }


    //Función para buscar recetas en la base de datos y mostrarlas
    fun cargarRecetas(baseGuardado: SQLiteDatabase, dieta : Int): Boolean {

        //Lipiamos todos los registros para evitar duplicados
        dataPrimerPlato.clear()
        dataAcompanamiento.clear()
        dataMediodia.clear()
        dataDesayuno.clear()
        dataMediamanana.clear()
        dataMerienda.clear()
        dataCena.clear()
        dataPostre.clear()

        //Comprobamos que la tablas recetas existe
        val cursor: Cursor = baseGuardado.rawQuery(
            "Select name from sqlite_master where type = 'table' and name like 'recetas' ",
            null
        )

        //Empezamos a recorrer la respuesta recibida
        if (cursor.count > 0) {
            //Accedemos a la tabla recetas en la base de datos
            val cursorReceta: Cursor = baseGuardado.rawQuery(
                "select numero, nombre, categorias, horasComida, preparacion, ingredientes, calorias, url from recetas",
                null
            )

            //Procesamos los datos obtenidos
            if (cursorReceta.count > 0) {
                while (cursorReceta.moveToNext()) {

                    //Transformamos los arrays de categoría
                    val json1 = JSONObject(cursorReceta.getString(2))
                    val jsonCategorias = json1.optJSONArray("listaCategorias")
                    val listaCategorias: ArrayList<Int> = ArrayList()

                    for (i in 0 until jsonCategorias!!.length()) {
                        listaCategorias.add(jsonCategorias[i].toString().toInt())
                    }

                    //Y también con ingredientes
                    val json2 = JSONObject(cursorReceta.getString(5))
                    val jsonIngredientes = json2.optJSONArray("listaIngredientes")
                    val listaIngredientes: ArrayList<String> = ArrayList()

                    for (i in 0 until jsonIngredientes!!.length()) {
                        listaIngredientes.add(jsonIngredientes[i].toString())
                    }

                    //Transformamos los arrays de categoría
                    val json3 = JSONObject(cursorReceta.getString(3))
                    val jsonHoras = json3.optJSONArray("listaHoras")
                    val listaHoras: ArrayList<Int> = ArrayList()

                    for (i in 0 until jsonHoras!!.length()) {
                        listaHoras.add(jsonHoras[i].toString().toInt())
                    }

                    //Creamos el objeto receta
                    val receta = Receta(
                        cursorReceta.getInt(0),
                        cursorReceta.getString(1),
                        listaCategorias,
                        listaHoras,
                        cursorReceta.getString(4),
                        listaIngredientes,
                        cursorReceta.getInt(6),
                        cursorReceta.getString(7)

                    )

                    //Comprobamos las categorías para saber a qué array agregarlo
                    for (cat in listaHoras) {
                        when (cat) {
                            10 -> {
                                dataPrimerPlato.add(receta)
                            }
                            11 -> {
                                dataAcompanamiento.add(receta)
                            }
                            12 -> {
                                dataMediodia.add(receta)
                            }
                            13 -> {
                                dataDesayuno.add(receta)
                            }
                            14 -> {
                                dataMediamanana.add(receta)
                            }
                            15 -> {
                                dataMerienda.add(receta)
                            }
                            16 -> {
                                dataCena.add(receta)
                            }
                            17 -> {
                                dataPostre.add(receta)
                            }
                        }
                    }
                    //Y lo agregamos a la lista para el recyclerview
                    //data.add(receta)
                }
            }
        }

        //Sumamos la cantidad de comidas del mediodía
        val cantidadMediodia = when (dieta) {
            0 -> {
                dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false
                }
            }
            1 -> {
                dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false &&
                            ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                }
            }
            2 -> {
                dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false &&
                    ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                    ((receta.categoria as? List<Int>)?.contains(2) == false) &&
                    ((receta.categoria as? List<Int>)?.contains(6) == false) &&
                    ((receta.categoria as? List<Int>)?.contains(8) == false)
                }
            }
            else -> {
                dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false
                }
            }
        }


        //Y la cantidad de cenas
        val cantidadCena = when (dieta) {
            0 -> {
                dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false
                }
            }
            1 -> {
                dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false &&
                            ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                }
            }
            2 -> {
                dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false &&
                            ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(2) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(6) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(8) == false)
                }
            }
            else -> {
                dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false
                }
            }
        }

        //Para asegurarnos de que hayan suficientes antes de crear una dieta. En caso contrario notificamos al usuario
        if (dataPrimerPlato.size >= 1) {
            if (dataAcompanamiento.size >= 1) {
                if (cantidadMediodia.size >= 7) {
                    return if (cantidadCena.size >= 7) {
                        if (dataDesayuno.size >= 1) {
                            true
                        } else {
                            _mostrarToast.value = R.string.aviso_desayuno
                            false
                        }
                    } else {
                        _mostrarToast.value = R.string.aviso_cena
                        false
                    }
                } else {
                    _mostrarToast.value = R.string.aviso_mediodia
                    return false
                }
            } else {
                _mostrarToast.value = R.string.aviso_acompanamiento
                return false
            }
        } else {
            if (dataMediodia.size >= 7) {
                return if (dataCena.size >= 7) {
                    if (dataDesayuno.size >= 1) {
                        true
                    } else {
                        _mostrarToast.value = R.string.aviso_desayuno
                        false
                    }
                } else {
                    _mostrarToast.value = R.string.aviso_cena
                    false
                }
            } else {
                _mostrarToast.value = R.string.aviso_mediodia
                return false
            }
        }
    }

    /**
     * Método para obtener un obtener las siete comidas del mediodía en la semana
     */
 /*   private fun obtenerMediodia(
        cat: String,
        idLunes: Int,
        idMartes: Int,
        idMiercoles: Int,
        idJueves: Int,
        idViernes: Int,
        idSabado: Int,
        idDomingo: Int,
        dieta : Int,
        arrayFav: java.util.ArrayList<Receta>
    ): Receta {
        when (cat) {
            arrayNombres.getOrElse(1) { "Carne" } -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(12) { "Comida mediodía" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(1) { "Carne" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataMediodia.filter { receta ->
                            (receta.categoria as? List<String>)?.contains(arrayNombres.getOrElse(1) { "Carne" }) == true
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        if (elegido == null) {
                            val resultados = dataMediodia.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido
            }

            arrayNombres.getOrElse(2) { "Pescado" } -> {
                var intento = 0
                    var elegido: Receta?
                    do {

                        val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(12) { "Comida mediodía" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(2) { "Pescado" }) == true) && ((it.categoria as? List<String>)?.contains(
                            arrayNombres.getOrElse(11) { "Acompañamiento" }
                        ) == false)}

                        if (receta != null) {
                            elegido = receta
                            arrayFav.remove(receta)
                        } else {

                            // Filtrar los registros cuyo segundo array contiene "carne"
                    val resultados = dataMediodia.filter { receta ->
                        (receta.categoria as? List<String>)?.contains(arrayNombres.getOrElse(2) { "Pescado" }) == true
                    }
                    // Elegir un registro al azar
                    elegido =
                        resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                    if (elegido ==null) {
                        val resultados = dataMediodia.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains(
                                arrayNombres.getOrElse(11) { "Acompañamiento" }
                            ) == false)
                        }
                        elegido = resultados.randomOrNull()!!
                    }
                    intento += 1
                            Log.d("erer", intento.toString())

                        }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)

                return elegido

            }

            arrayNombres.getOrElse(3) { "Legumbres" } -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(12) { "Comida mediodía" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(3) { "Legumbres" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {
                        Log.d("erer", intento.toString())

                        // Filtrar los registros cuyo segundo array contiene "carne"
                        lateinit var resultados: List<Receta>

                        when (dieta) {
                            0 -> {
                                resultados = dataMediodia.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            3
                                        ) { "Legumbres" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false)
                                }
                            }
                            1 -> {
                                resultados = dataMediodia.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            3
                                        ) { "Legumbres" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false)
                                }

                            }
                            2 -> {
                                resultados = dataMediodia.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            3
                                        ) { "Legumbres" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(6) { "Huevo" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(9) { "Lácteo" }
                                    ) == false)
                                }
                            }
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía

                        if (elegido == null) {
                            val resultados = dataMediodia.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido

            }

            arrayNombres.getOrElse(4) { "Cereal" } -> {
                var intento = 0
                var elegido : Receta?
                do {
                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(12) { "Comida mediodía" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(4) { "Cereal" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {

                        // Filtrar los registros cuyo segundo array contiene "carne"
                    lateinit var resultados: List<Receta>
                    when (dieta) {
                        0 -> {
                            resultados = dataMediodia.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(
                                        4
                                    ) { "Cereal" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                        }
                        1 -> {
                            resultados = dataMediodia.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(
                                        4
                                    ) { "Cereal" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(1) { "Carne" }
                                ) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(2) { "Pescado" }
                                ) == false)
                            }

                        }
                        2 -> {
                            resultados = dataMediodia.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(
                                        4
                                    ) { "Cereal" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(1) { "Carne" }
                                ) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(2) { "Pescado" }
                                ) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(6) { "Huevo" }
                                ) == false) && ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(9) { "Lácteo" }
                                ) == false)
                            }
                        }
                    }
                    // Elegir un registro al azar
                    elegido =
                        resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía

                    if (elegido == null) {
                        val resultados = dataMediodia.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains(
                                arrayNombres.getOrElse(11) { "Acompañamiento" }
                            ) == false)
                        }
                        elegido = resultados.randomOrNull()!!
                    }
                    intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido

            }

            arrayNombres.getOrElse(5) { "Huevo" } -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(12) { "Comida mediodía" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(5) { "Huevo" }) == true) }

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {

                        // Filtrar los registros cuyo segundo array contiene "carne"
                        lateinit var resultados: List<Receta>
                        when (dieta) {
                            0 -> {
                                resultados = dataMediodia.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            5
                                        ) { "Huevo" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false)
                                }
                            }
                            1 -> {
                                resultados = dataMediodia.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            5
                                        ) { "Huevo" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false)
                                }

                            }
                            2 -> {
                                resultados = dataMediodia.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            5
                                        ) { "Huevo" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(6) { "Huevo" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(9) { "Lácteo" }
                                    ) == false)
                                }
                            }
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        if (elegido == null) {
                            val resultados = dataMediodia.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido

            }

            else -> {
                var intento = 0
                var elegido : Receta?
                do {
                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(12) { "Comida mediodía" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(6) { "Verdura" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false)}

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {

                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataMediodia.filter { receta ->
                            (receta.categoria as? List<String>)?.contains(arrayNombres.getOrElse(5) { "Verdura" }) == true
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        if (elegido == null) {
                            val resultados = dataMediodia.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido

            }
        }
    }

  */
    private fun obtenerMediodia(
        categoria: Int,
        idsOcupados: Set<Int>,
        dieta: Int,
        arrayFav: ArrayList<Receta>
    ): Receta {

        fun filtrarRecetas(criterios: List<Int>): List<Receta> {
            return dataMediodia.filter { receta ->
                (receta.horas as? List<Int>)?.contains(12) == true && (receta.horas as? List<Int>)?.contains(11) == false && receta.categoria.containsAll(criterios) && !idsOcupados.contains(receta.id)
            }
        }

        // Filtrar recetas según la categoría y la dieta
        val criterios = when (categoria) {
            1 -> listOf(1)
            2 -> listOf(2)
            3 -> listOf(3)
            4 -> listOf(4)
            5 -> listOf(5)
            6 -> listOf(6)
            // ... otros casos
            else -> listOf(6)
        }

        val recetasCandidatas = filtrarRecetas(criterios)


        val arrayFiltrado = when (dieta) {
            0 -> {recetasCandidatas.filter { receta ->
                (receta.horas as? List<Int>)?.contains(11) == false
            } }
            1 -> { recetasCandidatas.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false &&
                            ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                }
            }
            2 -> {
                recetasCandidatas.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false &&
                            ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(2) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(6) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(8) == false)
                }
            }
            else -> {recetasCandidatas.filter { receta ->
                (receta.horas as? List<Int>)?.contains(11) == false
            }}
        }
        Log.d("crit", arrayFiltrado.toString())

        // Si no hay recetas candidatas que cumplan los criterios, relajar los criterios
        val recetasCandidatasAmpliadas = if (arrayFiltrado.isEmpty()) {
            when (dieta) {
                0 -> {dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(12) == true && (receta.horas as? List<Int>)?.contains(11) == false && !idsOcupados.contains(receta.id)}
                }
                1 -> { dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(12) == true && (receta.horas as? List<Int>)?.contains(11) == false && !idsOcupados.contains(receta.id) &&
                            ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                }
                }
                2 -> {
                    dataMediodia.filter { receta ->
                        (receta.horas as? List<Int>)?.contains(12) == true && (receta.horas as? List<Int>)?.contains(11) == false && !idsOcupados.contains(receta.id) &&
                                ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                                ((receta.categoria as? List<Int>)?.contains(2) == false) &&
                                ((receta.categoria as? List<Int>)?.contains(6) == false) &&
                                ((receta.categoria as? List<Int>)?.contains(8) == false)
                    }
                }
                else -> {dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(12) == true && (receta.horas as? List<Int>)?.contains(11) == false && !idsOcupados.contains(receta.id)}}
            }
        } else {
            arrayFiltrado
        }

        Log.d("ampl", recetasCandidatasAmpliadas.toString())

        //Comprobamos si alguna receta en la lista coincide con alguna en la lista de favoritas
        for (fav in 0 until  arrayFav.size) {
            for (element in arrayFiltrado)
                if (element.id == (arrayFav[fav].id)){
                val favorita = arrayFav[fav]
                arrayFav.removeAt(fav)
                return favorita
            }
        }
        // Seleccionar una receta aleatoria
        return recetasCandidatasAmpliadas.randomOrNull() ?: throw RuntimeException("No se encontraron recetas")
    }

    /**
     * Método para obtener las siete cenas de la semana.
     */
  /*  private fun obtenerCena(
        cat: String,
        idLunes: Int,
        idMartes: Int,
        idMiercoles: Int,
        idJueves: Int,
        idViernes: Int,
        idSabado: Int,
        idDomingo: Int,
        dieta : Int,
        arrayFav : java.util.ArrayList<Receta>
    ): Receta {
        when (cat) {
            arrayNombres.getOrElse(1) { "Carne" } -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(16) { "Cena" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(1) { "Carne" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }
                    Log.d("ioCe", receta.toString())

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains(arrayNombres.getOrElse(1) { "Carne" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                arrayNombres.getOrElse(11) { "Acompañamiento" }
                            ) == false)
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        intento += 1
                        if (elegido.nombre == "") {
                            val resultados = dataCena.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido
            }

            arrayNombres.getOrElse(2) { "Pescado" } -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(16) { "Cena" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(2) { "Pescado" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains(arrayNombres.getOrElse(2) { "Pescado" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                arrayNombres.getOrElse(11) { "Acompañamiento" }
                            ) == false)
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        if (elegido.nombre == "") {
                            val resultados = dataCena.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())
                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)

                return elegido

            }

            arrayNombres.getOrElse(3) { "Legumbres" } -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(16) { "Cena" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(3) { "Legumbres" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }
                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        lateinit var resultados: List<Receta>
                        when (dieta) {
                            0 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            3
                                        ) { "Legumbres" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false)
                                }
                            }
                            1 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            3
                                        ) { "Legumbres" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false)
                                }

                            }
                            2 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            3
                                        ) { "Legumbres" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(6) { "Huevo" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(9) { "Lácteo" }
                                    ) == false)
                                }
                            }
                        }

                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        if (elegido == null) {
                            resultados = dataCena.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido

            }

            arrayNombres.getOrElse(4) { "Cereal" } -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(16) { "Cena" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(4) { "Cereal" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {
                        lateinit var resultados: List<Receta>
                        when (dieta) {
                            0 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            4
                                        ) { "Cereal" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false)
                                }
                            }
                            1 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            4
                                        ) { "Cereal" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false)
                                }

                            }
                            2 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            4
                                        ) { "Cereal" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(6) { "Huevo" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(9) { "Lácteo" }
                                    ) == false)
                                }
                            }
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        if (elegido == null) {
                            val resultados = dataCena.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido

            }

            arrayNombres.getOrElse(5) { "Huevo" } -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(16) { "Cena" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(5) { "Huevo" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {
                        lateinit var resultados: List<Receta>
                        when (dieta) {
                            0 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            5
                                        ) { "Huevo" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false)
                                }
                            }
                            1 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            5
                                        ) { "Huevo" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false)
                                }

                            }
                            2 -> {
                                resultados = dataCena.filter { receta ->
                                    ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(
                                            5
                                        ) { "Huevo" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(1) { "Carne" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(2) { "Pescado" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(6) { "Huevo" }
                                    ) == false) && ((receta.categoria as? List<String>)?.contains(
                                        arrayNombres.getOrElse(9) { "Lácteo" }
                                    ) == false)
                                }
                            }
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        if (elegido.nombre == "") {
                            val resultados = dataCena.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido

            }

            else -> {
                var intento = 0
                var elegido : Receta?
                do {

                    val receta = arrayFav.firstOrNull { it.categoria.contains(arrayNombres.getOrElse(16) { "Cena" }) && ((it.categoria as? List<String>)?.contains(arrayNombres.getOrElse(6) { "Verdura" }) == true) && ((it.categoria as? List<String>)?.contains(
                        arrayNombres.getOrElse(11) { "Acompañamiento" }
                    ) == false) }
                    Log.d("ioCe", receta.toString())

                    if (receta != null) {
                        elegido = receta
                        arrayFav.remove(receta)
                    } else {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains(arrayNombres.getOrElse(6) { "Verdura" }) == false) && ((receta.categoria as? List<String>)?.contains(
                                arrayNombres.getOrElse(11) { "Acompañamiento" }
                            ) == false)
                        }
                        // Elegir un registro al azar
                        elegido =
                            resultados.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        if (elegido.nombre == "") {
                            val resultados = dataCena.filter { receta ->
                                ((receta.categoria as? List<String>)?.contains(
                                    arrayNombres.getOrElse(11) { "Acompañamiento" }
                                ) == false)
                            }
                            elegido = resultados.randomOrNull()!!
                        }
                        intento += 1
                        Log.d("erer", intento.toString())

                    }
                } while ((elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles || elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado || elegido.id == idDomingo) && intento <= 3)
                return elegido

            }
        }
    }
   */
    private fun obtenerCena(
        categoria: Int,
        idsOcupados: Set<Int>,
        dieta: Int,
        arrayFav: ArrayList<Receta>
    ): Receta {


        fun filtrarRecetas(criterios: List<Int>): List<Receta> {
            return dataCena.filter { receta ->
                (receta.horas as? List<Int>)?.contains(11) == false && receta.horas.contains(16) && !receta.categoria.containsAll(criterios) && !idsOcupados.contains(receta.id)
            }
        }

        // Filtrar recetas según la categoría y la dieta
        val criterios = when (categoria) {
            1 -> listOf(1)
            2 -> listOf( 2)
            3 -> listOf(3)
            4 -> listOf(4)
            5 -> listOf(5)
            6 -> listOf(6)
            // ... otros casos
            else -> listOf(6)
        }



        val recetasCandidatas = filtrarRecetas(criterios)


        val arrayFiltrado = when (dieta) {
            0 -> {recetasCandidatas.filter { receta ->
                (receta.horas as? List<Int>)?.contains(11) == false
            } }
            1 -> { recetasCandidatas.filter { receta ->
                (receta.horas as? List<Int>)?.contains(11) == false &&
                        ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                        ((receta.categoria as? List<Int>)?.contains(2) == false)
            }
            }
            2 -> {
                recetasCandidatas.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(11) == false &&
                            ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(2) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(6) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(8) == false)
                }
            }
            else -> {recetasCandidatas.filter { receta ->
                (receta.horas as? List<Int>)?.contains(11) == false
            }}
        }
        // Si no hay recetas candidatas que cumplan los criterios, relajar los criterios
        val recetasCandidatasAmpliadas = if (arrayFiltrado.isEmpty()) {
            when (dieta) {
                0 -> {dataCena.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(16) == true && (receta.horas as? List<Int>)?.contains(11) == false && !idsOcupados.contains(receta.id)}
                }
                1 -> { dataCena.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(16) == true && (receta.horas as? List<Int>)?.contains(11) == false && !idsOcupados.contains(receta.id) &&
                            ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                            ((receta.categoria as? List<Int>)?.contains(2) == false)
                }
                }
                2 -> {
                    dataCena.filter { receta ->
                        (receta.horas as? List<Int>)?.contains(16) == true && (receta.horas as? List<Int>)?.contains(11) == false && !idsOcupados.contains(receta.id) &&
                                ((receta.categoria as? List<Int>)?.contains(1) == false) &&
                                ((receta.categoria as? List<Int>)?.contains(2) == false) &&
                                ((receta.categoria as? List<Int>)?.contains(6) == false) &&
                                ((receta.categoria as? List<Int>)?.contains(8) == false)
                    }
                }
                else -> {dataMediodia.filter { receta ->
                    (receta.horas as? List<Int>)?.contains(12) == true && (receta.horas as? List<Int>)?.contains(11) == false && !idsOcupados.contains(receta.id)}}
            }
        } else {
            arrayFiltrado
        }

        //Comprobamos si alguna receta en la lista coincide con alguna en la lista de favoritas
        for (fav in 0 until  arrayFav.size) {
            for (element in arrayFiltrado)
                if (element.id == (arrayFav[fav].id)){
                    val favorita = arrayFav[fav]
                    arrayFav.removeAt(fav)
                    return favorita
                }
        }
        // Seleccionar una receta aleatoria
        return recetasCandidatasAmpliadas.randomOrNull() ?: throw RuntimeException("No se encontraron recetas")
    }

    /**
     * Método extralargo para dibujar y guardar la dieta en pdf.
     */
    fun crearMenuPdf(context: Context, diasSemana: List<String>, baseGuardado: SQLiteDatabase) {

        val pdfDocument = PdfDocument()
        //Creamos una hoja con las medidas A4
        val pageInfo = PdfDocument.PageInfo.Builder(800, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        //Creamos un canvas para dibujar la tabla
        val canvas: Canvas = page.canvas

        //Un pincel para dibujar la tabla
        val paint = Paint()
        paint.style = Paint.Style.STROKE // Solo dibuja los bordes
        paint.strokeWidth = 2f
        paint.color = Color.BLACK

        //Otro para escribir texto
        val textPaint = Paint()
        textPaint.textSize = 12f
        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL

        // Dimensiones de la tabla
        val startX = 40f
        val startY = 40f
        val cellWidth = 90f
        val headerHeight = 40f
        val medioHeight = 60f
        val cellHeight = 90f
        val granHeight = 130f


        // Dibuja las filas y columnas
        for (row in 0..5) {
            for (col in 0..7) {
                val left = startX + col * cellWidth
                //En función de la fila, le da un tamaño u otro
                val top =
                    if (row == 0) startY else if (row == 3 || row == 5) startY + (row - 1) * cellHeight else startY + headerHeight + (row - 1) * cellHeight
                val right = left + cellWidth
                //Y un alto u otro
                val bottom =
                    if (row == 0) top + headerHeight else if (row == 2 || row == 4) top + medioHeight else if (row == 3 || row == 5) top + granHeight else top + cellHeight

                // Dibuja el fondo blanco para las celdas
                val backgroundPaint = Paint()
                backgroundPaint.color = if (row == 0 || col == 0) ContextCompat.getColor(
                    context,
                    R.color.green_200
                ) else Color.WHITE
                backgroundPaint.style = Paint.Style.FILL
                canvas.drawRect(left, top, right, bottom, backgroundPaint)

                // Dibuja los bordes
                canvas.drawRect(left, top, right, bottom, paint)

                // Dibuja el texto dentro de la celda (evita índice fuera de rango)
                if (row == 0) {
                    // Días de la semana para el header
                    val day = diasSemana[col]
                    canvas.drawText(
                        day,
                        left + 10,
                        top + headerHeight / 2 + textPaint.textSize / 2,
                        textPaint
                    )
                } else if (row > 0 && col < 8) {
                    //Aquí obtenemos los nombres de comidas para escribirlos en las celdas, y lo almacenamos en text
                    var text = ""
                    when (row) {
                        1 -> {
                            text = semanaDesayuno[col].nombre.toString()
                        }
                        2 -> {
                            text = semanaMediamanana[col].nombre.toString()
                        }
                        3 -> {
                            //En el caso del mediodía, juntamos la comida, acompañamiento y postre
                            text =
                                "${semanaMediodia[col].nombre.toString()} \\n ${semanaAcompanamientoMediodia[col].nombre.toString()} \\n ${semanaPostre[col].nombre.toString()}"
                        }
                        4 -> {
                            text = semanaMerienda[col].nombre.toString()
                        }
                        5 -> {
                            //En el caso de la cena, juntamos la comida, acompañamiento y postre
                            text =
                                "${semanaCena[col].nombre.toString()} \\n ${semanaAcompanamientoCena[col].nombre.toString()} \\n ${semanaPostre[col].nombre.toString()}"

                        }
                    }
                    //Cortamos el texto para que no se salga
                    val wrappedLines =
                        wrapText(text, cellWidth - 20, textPaint) // Restamos márgenes
                    var lineY = top + 10 + textPaint.textSize // Línea inicial

                    //Y lo escribimos por línea
                    for (line in wrappedLines) {
                        if (lineY > bottom) break // Evita dibujar fuera de la celda
                        canvas.drawText(line, left + 10, lineY, textPaint)
                        lineY += textPaint.textSize + 2 // Avanza a la siguiente línea
                    }
                }
            }
        }

        //Terminamos la hoja
        pdfDocument.finishPage(page)


        //Obtenemos el día y la hora para evitar duplicados en el nombre
        val sdf = SimpleDateFormat("dd-M-yyyy hh.mm.ss")
        val currentDate = sdf.format(Date())
        val fileName = "MenuSemanal_$currentDate.pdf"

        //En función de la versión de android, lo imprimimos de una manera u otra
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            //Llamamos a MediaStorage para guardar en la ruta de documentos
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }

            //Obtenemos la ruta
            val uri = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            )
            try {
                uri?.let {
                    context.contentResolver.openOutputStream(it).use { outputStream ->
                        val pdfDocument = PdfDocument()
                        // Agregar contenido al PDF (ejemplo)
                        val pageInfo = PdfDocument.PageInfo.Builder(800, 600, 1).create()
                        val page = pdfDocument.startPage(pageInfo)
                        val canvas = page.canvas
                        canvas.drawText("Ejemplo de contenido", 100f, 100f, Paint())
                        pdfDocument.finishPage(page)

                        // Guardar el PDF
                        pdfDocument.writeTo(outputStream)
                        pdfDocument.close()

                        //Notificamos al usuario con un dialog
                        val ostiaPdf = AlertDialog.Builder(context)
                        ostiaPdf.setTitle(context.getString(R.string.pdf_titulo))
                        ostiaPdf.setMessage(context.getString(R.string.pdf_guardado) + " $outputStream$fileName")

                        ostiaPdf.setPositiveButton(R.string.ok) { dialog, which ->
                        }

                        //Damos la opción de copiar la lista de la compra
                        ostiaPdf.setNeutralButton(R.string.copiar_compra) { dialog, which ->
                            var listaCompra = ""
                            val arrayD = ArrayList<String>()

                            //Almacenamos todos los ingredientes en un array, y los no repetidos los guardamos también en un string
                            for (des in semanaDesayuno) {
                                for (ing in des.ingredientes) {
                                    if (!arrayD.contains(ing)) {
                                        listaCompra += "$ing, "
                                        arrayD.add(ing)
                                    }
                                }
                            }
                            for (des in semanaMediamanana) {
                                for (ing in des.ingredientes) {
                                    if (!arrayD.contains(ing)) {
                                        listaCompra += "$ing, "
                                        arrayD.add(ing)
                                    }
                                }
                            }
                            for (des in semanaMediodia) {
                                for (ing in des.ingredientes) {
                                    if (!arrayD.contains(ing)) {
                                        listaCompra += "$ing, "
                                        arrayD.add(ing)
                                    }
                                }
                            }
                            for (des in semanaAcompanamientoMediodia) {
                                for (ing in des.ingredientes) {
                                    if (!arrayD.contains(ing)) {
                                        listaCompra += "$ing, "
                                        arrayD.add(ing)
                                    }
                                }
                            }
                            for (des in semanaPostre) {
                                for (ing in des.ingredientes) {
                                    if (!arrayD.contains(ing)) {
                                        listaCompra += "$ing, "
                                        arrayD.add(ing)
                                    }
                                }
                            }
                            for (des in semanaMerienda) {
                                for (ing in des.ingredientes) {
                                    if (!arrayD.contains(ing)) {
                                        listaCompra += "$ing, "
                                        arrayD.add(ing)
                                    }
                                }
                            }
                            for (des in semanaCena) {
                                for (ing in des.ingredientes) {
                                    if (!arrayD.contains(ing)) {
                                        listaCompra += "$ing, "
                                        arrayD.add(ing)
                                    }
                                }
                            }
                            for (des in semanaAcompanamientoCena) {
                                for (ing in des.ingredientes) {
                                    if (!arrayD.contains(ing)) {
                                        listaCompra += "$ing, "
                                        arrayD.add(ing)
                                    }
                                }
                            }
                            //Y lo mandamos a la función de portapapeles
                            _copiarCompra.value = listaCompra
                            _mostrarToast.value = R.string.copiar_compra_exito
                        }
                        ostiaPdf.show()
                    }

                } ?: throw IOException("No se pudo crear el archivo")
            } catch (e: Exception) {
                e.printStackTrace()
                //Si algo malo sucede, se avisa al usuario

                //Toast.makeText(this, "Error al guardar el PDF: ${e.message}", Toast.LENGTH_LONG).show()
            }

        } else {
            // Para Android 28 o menor usa rutas tradicionales
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!directory.exists()) directory.mkdirs() // Crea el directorio si no existe

            val file = File(directory, fileName)
            try {
                FileOutputStream(file).use { outputStream ->
                    val pdfDocument = PdfDocument()
                    // Agregar contenido al PDF (ejemplo)
                    val pageInfo = PdfDocument.PageInfo.Builder(800, 600, 1).create()
                    val page = pdfDocument.startPage(pageInfo)
                    val canvas = page.canvas
                    canvas.drawText("Ejemplo de contenido", 100f, 100f, Paint())
                    pdfDocument.finishPage(page)

                    // Guardar el PDF
                    pdfDocument.writeTo(outputStream)
                    pdfDocument.close()
                    agregarDietaSemana(baseGuardado)

                    //Notificamos al usuario con un dialog
                    val ostiaPdf = AlertDialog.Builder(context)
                    ostiaPdf.setTitle(context.getString(R.string.pdf_titulo))
                    ostiaPdf.setMessage(context.getString(R.string.pdf_guardado) + " $directory$fileName")

                    ostiaPdf.setPositiveButton(R.string.ok) { dialog, which ->

                    }

                    //Damos la opción de copiar la lista de la compra
                    ostiaPdf.setNeutralButton(R.string.copiar_compra) { dialog, which ->
                        var listaCompra = ""
                        val arrayD = ArrayList<String>()

                        //Almacenamos todos los ingredientes en un array, y los no repetidos los guardamos también en un string
                        for (des in semanaDesayuno) {
                            for (ing in des.ingredientes) {
                                if (!arrayD.contains(ing)) {
                                    listaCompra += "$ing, "
                                    arrayD.add(ing)
                                }
                            }
                        }
                        for (des in semanaMediamanana) {
                            for (ing in des.ingredientes) {
                                if (!arrayD.contains(ing)) {
                                    listaCompra += "$ing, "
                                    arrayD.add(ing)
                                }
                            }
                        }
                        for (des in semanaMediodia) {
                            for (ing in des.ingredientes) {
                                if (!arrayD.contains(ing)) {
                                    listaCompra += "$ing, "
                                    arrayD.add(ing)
                                }
                            }
                        }
                        for (des in semanaAcompanamientoMediodia) {
                            for (ing in des.ingredientes) {
                                if (!arrayD.contains(ing)) {
                                    listaCompra += "$ing, "
                                    arrayD.add(ing)
                                }
                            }
                        }
                        for (des in semanaPostre) {
                            for (ing in des.ingredientes) {
                                if (!arrayD.contains(ing)) {
                                    listaCompra += "$ing, "
                                    arrayD.add(ing)
                                }
                            }
                        }
                        for (des in semanaMerienda) {
                            for (ing in des.ingredientes) {
                                if (!arrayD.contains(ing)) {
                                    listaCompra += "$ing, "
                                    arrayD.add(ing)
                                }
                            }
                        }
                        for (des in semanaCena) {
                            for (ing in des.ingredientes) {
                                if (!arrayD.contains(ing)) {
                                    listaCompra += "$ing, "
                                    arrayD.add(ing)
                                }
                            }
                        }
                        for (des in semanaAcompanamientoCena) {
                            for (ing in des.ingredientes) {
                                if (!arrayD.contains(ing)) {
                                    listaCompra += "$ing, "
                                    arrayD.add(ing)
                                }
                            }
                        }
                        _copiarCompra.value = listaCompra
                        _mostrarToast.value = R.string.copiar_compra_exito
                    }
                    ostiaPdf.show()

                }

            } catch (e: Exception) {
                e.printStackTrace()
                _mostrarToast.value = R.string.pdf_error

            }
        }


        // Guarda el PDF en almacenamiento externo
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, "MenuSemanal_$currentDate.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
    }

    // Función para dividir texto en varias líneas
    fun wrapText(text: String, maxWidth: Float, textPaint: Paint): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            var testLine = ""
            if (word == "\\n") {
                lines.add(currentLine)
                currentLine = word
            } else {
                testLine = if (currentLine.isEmpty()) word else "$currentLine $word"

            }
            if (textPaint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }

    /**
     * Método para guardar la dieta generada en la base de datos.
     */
    fun agregarDietaSemana(db: SQLiteDatabase) {
        val baseGuardado = db

        //Creamos por si acaso la tabla
        //Creamos la tabla en caso de no existir
        baseGuardado.execSQL("CREATE TABLE if not exists dietaActual (numero integer primary key AUTOINCREMENT, " +
                "desayuno VARCHAR(700), mediamanana VARCHAR(700), comida VARCHAR(700), acompanamientoComida VARCHAR(700), " +
                "postre VARCHAR(700), merienda VARCHAR(700), cena VARCHAR(700), acompanamientoCena VARCHAR(700))")

        //Obtenemos el total de registros de la tabla
        val cursorTotal = baseGuardado.rawQuery("SELECT COUNT(*) FROM dietaActual", null)
        var total = 0
        if (cursorTotal.count > 0) {
            cursorTotal.moveToFirst()
            total = cursorTotal.getInt(0)

        }
        cursorTotal.close()

        if (total < 1) {
            //En caso de no haber registros, se crea uno para evitar errores
            val registro = ContentValues()
            registro.put("desayuno", "desayuno")
            registro.put("mediamanana", "mediamanana")
            registro.put("comida", "mediodia")
            registro.put("acompanamientoComida", "acompanamientoComida")
            registro.put("postre", "postre")
            registro.put("merienda", "merienda")
            registro.put("cena", "cena")
            registro.put("acompanamientoCena", "acompanamientoCena")

            //Intertamos y cerramos la conexión
            baseGuardado.insert("dietaActual", null, registro )
        }
        //Creamos la variable donde almacenaremos la receta a agregar
        val registro = ContentValues()

        //Almacenamos todas las recetas de las comidas para guardarlas juntas
        val gson = Gson()
        val jsonDesayuno = gson.toJson(semanaDesayuno)

        val jsonMediama = gson.toJson(semanaMediamanana)
        val jsonMedio = gson.toJson(semanaMediodia)
        val jsonAcMedio = gson.toJson(semanaAcompanamientoMediodia)
        val jsonPostre = gson.toJson(semanaPostre)
        val jsonMerienda = gson.toJson(semanaMerienda)
        val jsonCena = gson.toJson(semanaCena)
        val jsonAcCena = gson.toJson(semanaAcompanamientoCena)

        _actualizarDieta.value = "dieta"

        //Guardamos to,dos los valores en la variable
        registro.put("desayuno", jsonDesayuno)
        registro.put("mediamanana", jsonMediama)
        registro.put("comida", jsonMedio)
        registro.put("acompanamientoComida", jsonAcMedio)
        registro.put("postre", jsonPostre)
        registro.put("merienda", jsonMerienda)
        registro.put("cena", jsonCena)
        registro.put("acompanamientoCena", jsonAcCena)

        //Intertamos y cerramos la conexión
        baseGuardado.update("dietaActual", registro, "numero=?", arrayOf((1).toString()) )
        baseGuardado.close()

    }

    /**
     * Método para volver a generar el pdf de la actual dieta
     */
    fun volverAGenerarPdf(context: Context, diasSemana: List<String>, baseGuardado: SQLiteDatabase){
        //Limpiamos los arrays para evitar duplicados
        semanaDesayuno.clear()
        semanaMediamanana.clear()
        semanaMediodia.clear()
        semanaAcompanamientoMediodia.clear()
        semanaPostre.clear()
        semanaMerienda.clear()
        semanaCena.clear()
        semanaAcompanamientoCena.clear()

        //Hacemos la consulta a la BBDD
        val cursor: Cursor = baseGuardado.rawQuery(
            "Select name from sqlite_master where type = 'table' and name like 'dietaActual' ",
            null
        )
        if (cursor.count > 0) {
            //Accedemos a la tabla dietaActual en la base de datos
            val cursorReceta: Cursor = baseGuardado.rawQuery(
                "select numero, desayuno, mediamanana, comida, acompanamientoComida, postre, merienda, cena, acompanamientoCena from dietaActual where numero=1",
                null
            )
            //Procesamos los datos obtenidos
            if (cursorReceta.count > 0) {
                while (cursorReceta.moveToNext()) {
                    val gson = Gson()

                    //Obtenemos y convertimos los datos de gson
                    val jsonD = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("desayuno"))
                    //Comprobamos que los datos obtenidos sean un array y no esté vacío
                    if(jsonD != "desayuno") {
                        //Y rellenamos los arrays de la dieta generada
                        val arrayDesayuno = gson.fromJson(jsonD, Array<Receta>::class.java).toList()
                        if(arrayDesayuno[0].nombre != null) {
                            for (item in arrayDesayuno)
                            semanaDesayuno.add(item)
                        }
                    } else {
                        for (i in 1 until 8)
                            semanaDesayuno.add(Receta(0, "", listOf(0),listOf(0), "a", listOf(""), 0, "a"))
                    }

                    //Y repetimos
                    val jsonMedia = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("mediamanana"))
                    if (jsonMedia != "mediamanana") {
                        val arrayMediamanana = gson.fromJson(jsonMedia, Array<Receta>::class.java).toList()
                        if(arrayMediamanana[0].nombre != null) {
                            for (item in arrayMediamanana)
                                semanaMediamanana.add(item)
                        }
                    }else {
                        for (i in 1 until 8)
                            semanaMediamanana.add(Receta(0, "", listOf(0),listOf(0),"a", listOf(""), 0, "a"))
                    }
                    val jsonMedio = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("comida"))
                    if (jsonMedio != "comida") {
                        val arrayMediodia = gson.fromJson(jsonMedio, Array<Receta>::class.java).toList()

                        if(arrayMediodia[0].nombre != "") {
                            Log.d("com", arrayMediodia.toString())
                            for (item in arrayMediodia)
                                semanaMediodia.add(item)
                        }
                    } else {
                        for (i in 1 until 8)
                            semanaMediodia.add(Receta(0, "", listOf(0),listOf(0), "a", listOf(""), 0, "a"))
                    }
                    val jsonAcMedio = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("acompanamientoComida"))
                    if(jsonAcMedio != "acompanamientoComida") {
                        val arrayAcMediodia = gson.fromJson(jsonAcMedio, Array<Receta>::class.java).toList()
                        if(arrayAcMediodia[0].nombre != null) {
                            Log.d("accom", arrayAcMediodia.toString())

                            for (item in arrayAcMediodia)
                                semanaAcompanamientoMediodia.add(item)
                        }
                    } else {
                        for (i in 1 until 8)
                            semanaAcompanamientoMediodia.add(Receta(0, "", listOf(0),listOf(0), "a", listOf(""), 0, "a"))
                    }
                    val jsonPo = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("postre"))
                    if (jsonPo != "postre") {
                        val arrayPostre = gson.fromJson(jsonPo, Array<Receta>::class.java).toList()
                        if(arrayPostre[0].nombre != null) {
                            Log.d("pos", arrayPostre.toString())

                            for (item in arrayPostre)
                                semanaPostre.add(item)
                        }
                    }else {
                        for (i in 1 until 8)
                            semanaPostre.add(Receta(0, "", listOf(0),listOf(0), "a", listOf(""), 0, "a"))
                    }
                    val jsonMer = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("merienda"))
                    if (jsonMer != "merienda") {
                        val arrayMerienda =
                            gson.fromJson(jsonMer, Array<Receta>::class.java).toList()
                        if(arrayMerienda[0].nombre != null) {
                            for (item in arrayMerienda)
                                semanaMerienda.add(item)
                        }
                    }else {
                        for (i in 1 until 8)
                            semanaMerienda.add(Receta(0, "", listOf(0),listOf(0), "a", listOf(""), 0, "a"))
                    }
                    val jsonCe = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("cena"))
                    if (jsonCe != "cena"&& jsonCe != "") {
                        val arrayCena =
                            gson.fromJson(jsonCe, Array<Receta>::class.java).toList()
                        if(arrayCena[0].nombre != null) {
                            for (item in arrayCena)
                                semanaCena.add(item)
                        }
                    }else {
                        for (i in 1 until 8)
                            semanaCena.add(Receta(0, "", listOf(0),listOf(0), "a", listOf(""), 0, "a"))
                    }
                    val jsonAcCe = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("acompanamientoCena"))
                    if (jsonAcCe != "acompanamientoCena" && jsonAcCe != "") {
                        val arrayAcCena =
                            gson.fromJson(jsonAcCe, Array<Receta>::class.java).toList()
                        if(arrayAcCena[0].nombre != null) {
                            for (item in arrayAcCena)
                                semanaAcompanamientoCena.add(item)
                        }
                    }else {
                        for (i in 1 until 8)
                            semanaAcompanamientoCena.add(Receta(0, "", listOf(0),listOf(0), "a", listOf(""), 0, "a"))
                    }
                }
            }
            cursorReceta.close()
        }
        cursor.close()
        //Llamamos al crear pdf
        crearMenuPdf(context, diasSemana, baseGuardado)

    }
}
