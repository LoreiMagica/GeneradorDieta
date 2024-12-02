import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.Settings.Secure.getString
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.model.Receta
import com.lorei.generadorDieta.ui.fragment.DietaFragment
import org.json.JSONObject
import kotlin.math.log


/**
 * Clase la cual obtiene los eventos de Isla Cristina desde firebase y los manda al adapter
 */
class DietaViewModel : ViewModel() {

    private val _mostrarToast = MutableLiveData<Int>()
    val mostrarToast: LiveData<Int> get() = _mostrarToast

    val dataPrimerPlato = mutableListOf<Receta>()
    val dataAcompanamiento = mutableListOf<Receta>()
    val dataMediodia = mutableListOf<Receta>()
    val dataDesayuno = mutableListOf<Receta>()
    val dataMediamanana = mutableListOf<Receta>()
    val dataMerienda = mutableListOf<Receta>()
    val dataCena = mutableListOf<Receta>()
    val dataPostre = mutableListOf<Receta>()

    fun generarDieta(baseGuardado: SQLiteDatabase) {
        val todoCorrecto = cargarRecetas(baseGuardado)
        if (todoCorrecto) {
            var carne = 7
            val pescado = 2
            var legumbres = (0..1).random()
            var cereal = (0..1).random()
            var huevo = (0..1).random()

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

            carne -= (pescado + legumbres + cereal + huevo)

            var mediodiaLunes = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")
            var acompanamientoMediodiaLunes = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")

            var mediodiaMartes = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")
            var acompanamientoMediodiaMartes = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")

            var mediodiaMiercoles = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")
            var acompanamientoMediodiaMiercoles = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")

            var mediodiaJueves = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")
            var acompanamientoMediodiaJueves = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")

            var mediodiaViernes = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")
            var acompanamientoMediodiaViernes = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")

            var mediodiaSabado = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")
            var acompanamientoMediodiaSabado = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")

            var mediodiaDomingo = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")
            var acompanamientoMediodiaDomingo = Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a")


            // Crear un mapa con las variables y sus valores
            val semanaComidas = mapOf(
                "Carne" to carne,
                "Pescado" to pescado,
                "Legumbres" to legumbres,
                "Cereal" to cereal,
                "Huevo" to huevo
            )

            // Crear una lista con las variables repetidas el número de veces que tienen almacenado
            val listaRepeticiones = semanaComidas.flatMap { (nombre, cantidad) ->
                List(cantidad) { nombre }
            }

            var dia = 0

            // Imprimir las variables de forma aleatoria
            listaRepeticiones.shuffled().forEach { println(it)
                when (dia) {
                    0 -> {
                        mediodiaLunes=  obtenerMediodia(it, mediodiaLunes.id!!.toInt(), mediodiaMartes.id!!.toInt(), mediodiaMiercoles.id!!.toInt(), mediodiaJueves.id!!.toInt(), mediodiaViernes.id!!.toInt(), mediodiaSabado.id!!.toInt(), mediodiaDomingo.id!!.toInt())
                        Log.d("lunes", mediodiaLunes.nombre.toString())
                        if (mediodiaLunes.categoria.contains("Primer plato")){
                            acompanamientoMediodiaLunes =
                                dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        }
                        dia += 1
                    }
                    1 -> {
                        mediodiaMartes=  obtenerMediodia(it, mediodiaLunes.id!!.toInt(), mediodiaMartes.id!!.toInt(), mediodiaMiercoles.id!!.toInt(), mediodiaJueves.id!!.toInt(), mediodiaViernes.id!!.toInt(), mediodiaSabado.id!!.toInt(), mediodiaDomingo.id!!.toInt())
                        Log.d("martes", mediodiaMartes.nombre.toString())
                        if (mediodiaMartes.categoria.contains("Primer plato")){
                            acompanamientoMediodiaMartes =
                                dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        }
                        dia += 1
                    }
                    2 -> {
                        mediodiaMiercoles=  obtenerMediodia(it, mediodiaLunes.id!!.toInt(), mediodiaMartes.id!!.toInt(), mediodiaMiercoles.id!!.toInt(), mediodiaJueves.id!!.toInt(), mediodiaViernes.id!!.toInt(), mediodiaSabado.id!!.toInt(), mediodiaDomingo.id!!.toInt())
                        Log.d("miercoles", mediodiaMiercoles.nombre.toString())
                        if (mediodiaMiercoles.categoria.contains("Primer plato")){
                            acompanamientoMediodiaMiercoles =
                                dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        }
                        dia += 1
                    }
                    3 -> {
                        mediodiaJueves=  obtenerMediodia(it, mediodiaLunes.id!!.toInt(), mediodiaMartes.id!!.toInt(), mediodiaMiercoles.id!!.toInt(), mediodiaJueves.id!!.toInt(), mediodiaViernes.id!!.toInt(), mediodiaSabado.id!!.toInt(), mediodiaDomingo.id!!.toInt())
                        Log.d("jueves", mediodiaJueves.nombre.toString())
                        if (mediodiaJueves.categoria.contains("Primer plato")){
                            acompanamientoMediodiaJueves =
                                dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        }
                        dia += 1
                    }
                    4 -> {
                        mediodiaViernes=  obtenerMediodia(it, mediodiaLunes.id!!.toInt(), mediodiaMartes.id!!.toInt(), mediodiaMiercoles.id!!.toInt(), mediodiaJueves.id!!.toInt(), mediodiaViernes.id!!.toInt(), mediodiaSabado.id!!.toInt(), mediodiaDomingo.id!!.toInt())
                        Log.d("viernes", mediodiaViernes.nombre.toString())
                        if (mediodiaViernes.categoria.contains("Primer plato")){
                            acompanamientoMediodiaViernes =
                                dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        }
                        dia += 1
                    }
                    5 -> {
                        mediodiaSabado=  obtenerMediodia(it, mediodiaLunes.id!!.toInt(), mediodiaMartes.id!!.toInt(), mediodiaMiercoles.id!!.toInt(), mediodiaJueves.id!!.toInt(), mediodiaViernes.id!!.toInt(), mediodiaSabado.id!!.toInt(), mediodiaDomingo.id!!.toInt())
                        Log.d("sabado", mediodiaSabado.nombre.toString())
                        if (mediodiaSabado.categoria.contains("Primer plato")){
                            acompanamientoMediodiaSabado =
                                dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        }
                        dia += 1
                    }
                    6 -> {
                        mediodiaDomingo=  obtenerMediodia(it, mediodiaLunes.id!!.toInt(), mediodiaMartes.id!!.toInt(), mediodiaMiercoles.id!!.toInt(), mediodiaJueves.id!!.toInt(), mediodiaViernes.id!!.toInt(), mediodiaSabado.id!!.toInt(), mediodiaDomingo.id!!.toInt())
                        Log.d("domingo", mediodiaDomingo.nombre.toString())
                        if (mediodiaDomingo.categoria.contains("Primer plato")){
                            acompanamientoMediodiaDomingo =
                                dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                        }
                        dia += 1
                    }
                }
            }
        }
    }


    //Función para buscar recetas en la base de datos y mostrarlas
    fun cargarRecetas(baseGuardado: SQLiteDatabase): Boolean {
        //Comprobamos que la tablas recetas existe
        val cursor: Cursor = baseGuardado.rawQuery(
            "Select name from sqlite_master where type = 'table' and name like 'recetas' ",
            null
        )

        //Empezamos a recorrer la respuesta recibida
        if (cursor.count > 0) {
            //Accedemos a la tabla recetas en la base de datos
            val cursorReceta: Cursor = baseGuardado.rawQuery(
                "select numero, nombre, categorias, preparacion, ingredientes, calorias, url from recetas",
                null
            )

            //Procesamos los datos obtenidos
            if (cursorReceta.count > 0) {
                while (cursorReceta.moveToNext()) {

                    //Transformamos los arrays de categoría
                    val json1 = JSONObject(cursorReceta.getString(2))
                    val jsonCategorias = json1.optJSONArray("listaCategorias")
                    val listaCategorias: ArrayList<String> = ArrayList()

                    for (i in 0 until jsonCategorias!!.length()) {
                        listaCategorias.add(jsonCategorias[i].toString());
                    }

                    //Y también con ingredientes
                    val json2 = JSONObject(cursorReceta.getString(4))
                    val jsonIngredientes = json2.optJSONArray("listaIngredientes")
                    val listaIngredientes: ArrayList<String> = ArrayList()

                    for (i in 0 until jsonIngredientes!!.length()) {
                        listaIngredientes.add(jsonIngredientes[i].toString());
                    }

                    //Creamos el objeto receta
                    val receta = Receta(
                        cursorReceta.getInt(0),
                        cursorReceta.getString(1),
                        listaCategorias,
                        cursorReceta.getString(3),
                        listaIngredientes,
                        cursorReceta.getInt(5),
                        cursorReceta.getString(6)

                    )

                    for (cat in listaCategorias) {
                        when (cat) {
                            "Primer plato" -> {
                                dataPrimerPlato.add(receta)
                            }
                            "Acompañamiento" -> {
                                dataAcompanamiento.add(receta)
                            }
                            "Comida mediodía" -> {
                                dataMediodia.add(receta)
                            }
                            "Desayuno" -> {
                                dataDesayuno.add(receta)
                            }
                            "Mediamañana" -> {
                                dataMediamanana.add(receta)
                            }
                            "Merienda" -> {
                                dataMerienda.add(receta)
                            }
                            "Cena" -> {
                                dataCena.add(receta)
                            }
                            "Postre" -> {
                                dataPostre.add(receta)
                            }
                        }
                        }
                        //Y lo agregamos a la lista para el recyclerview
                        //data.add(receta)
                    }
                }
            }

        if (dataPrimerPlato.size >= 1) {
            if (dataAcompanamiento.size >= 1) {
                if (dataMediodia.size >= 7 )  {
                    return if (dataCena.size >= 7)  {
                        if (dataDesayuno.size >= 1)  {
                            true
                        } else {
                            Log.d("nombre", "lol")
                            _mostrarToast.value = R.string.aviso_desayuno
                            false
                        }
                    } else {
                        Log.d("nombre", "lol")
                        _mostrarToast.value = R.string.aviso_cena
                        false
                    }
                } else {
                    Log.d("nombre", "lol")
                    _mostrarToast.value = R.string.aviso_mediodia
                    return false
                }
            } else {
                Log.d("nombre", "lol")
                _mostrarToast.value = R.string.aviso_acompanamiento
                return false
            }
        } else {
            if (dataMediodia.size >= 7 )  {
                return if (dataCena.size >= 7)  {
                    if (dataDesayuno.size >= 1)  {
                        true
                    } else {
                        Log.d("nombre", "lol")
                        _mostrarToast.value = R.string.aviso_desayuno
                        false
                    }
                } else {
                    Log.d("nombre", "lol")
                    _mostrarToast.value = R.string.aviso_cena
                    false
                }
            } else {
                Log.d("nombre", "lol")
                _mostrarToast.value = R.string.aviso_mediodia
                return false
            }
        }
        }

    private fun obtenerMediodia(cat : String, idLunes : Int,idMartes : Int, idMiercoles : Int, idJueves : Int, idViernes : Int, idSabado : Int, idDomingo : Int) : Receta {
        when(cat) {
            "Carne" -> {
                var intento = 0
                var elegido : Receta?
                do {
                    do {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataMediodia.filter { receta ->
                            (receta.categoria as? List<String>)?.contains("Carne") == true
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataMediodia.randomOrNull()!!
                    }
                } while ( elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles|| elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado|| elegido.id == idDomingo)
                return elegido
            }

            "Pescado" -> {
                var intento = 0
                var elegido : Receta?
                do {
                    do {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataMediodia.filter { receta ->
                            (receta.categoria as? List<String>)?.contains("Pescado") == true
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataMediodia.randomOrNull()!!
                    }
                } while ( elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles|| elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado|| elegido.id == idDomingo)

                return elegido

            }

            "Legumbres" -> {
                var intento = 0
                var elegido : Receta?
                do {
                    do {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataMediodia.filter { receta ->
                            (receta.categoria as? List<String>)?.contains("Legumbres") == true
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataMediodia.randomOrNull()!!
                    }
                } while ( elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles|| elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado|| elegido.id == idDomingo)
                return elegido

            }

            "Cereal" -> {
                var intento = 0
                var elegido : Receta?
                do {
                    do {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataMediodia.filter { receta ->
                            (receta.categoria as? List<String>)?.contains("Cereal") == true
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataMediodia.randomOrNull()!!
                    }
                } while ( elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles|| elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado|| elegido.id == idDomingo)
                return elegido

            }

            "Huevo" -> {
                var intento = 0
                var elegido : Receta?
                do {
                    do {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataMediodia.filter { receta ->
                            (receta.categoria as? List<String>)?.contains("Huevo") == true
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataMediodia.randomOrNull()!!
                    }
                } while ( elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles|| elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado|| elegido.id == idDomingo)
                return elegido

            }

            else -> {
                var intento = 0
                var elegido : Receta?
                do {
                    do {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataMediodia.filter { receta ->
                            (receta.categoria as? List<String>)?.contains("Verdura") == true
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataMediodia.randomOrNull()!!
                    }
                } while ( elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles|| elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado|| elegido.id == idDomingo)
                return elegido

            }
        }
    }
    }
