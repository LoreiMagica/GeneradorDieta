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


            var semanaMediodia = arrayListOf<Receta>()
            var semanaAcompanamientoMediodia = arrayListOf<Receta>()
            var semanaCena = arrayListOf<Receta>()
            var semanaAcompanamientoCena = arrayListOf<Receta>()
            var semanaDesayuno = arrayListOf<Receta>()
            var semanaPostre = arrayListOf<Receta>()
            var semanaMediamanana = arrayListOf<Receta>()
            var semanaMerienda = arrayListOf<Receta>()

            for (i in 0 until 7){
                semanaDesayuno.add(Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a"))
                semanaMediamanana.add(Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a"))
                semanaMediodia.add(Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a"))
                semanaAcompanamientoMediodia.add(Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a"))
                semanaPostre.add(Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a"))
                semanaMerienda.add(Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a"))
                semanaCena.add(Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a"))
                semanaAcompanamientoCena.add(Receta(0, "a", listOf("a"),"a", listOf("a"), 0, "a"))

            }

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

                if (dataDesayuno.size >= 1) {
                    semanaDesayuno[dia] = dataDesayuno.randomOrNull()!!
                    Log.d("lunesDes", semanaDesayuno[dia].nombre.toString())
                }
                if (dataMediamanana.size >=1) {
                    semanaMediamanana[dia] = dataMediamanana.randomOrNull()!!
                    Log.d("lunesMedi", semanaMediamanana[dia].nombre.toString())
                }

                semanaMediodia[dia]=  obtenerMediodia(it, semanaMediodia[0].id!!.toInt(), semanaMediodia[1].id!!.toInt(), semanaMediodia[2].id!!.toInt(), semanaMediodia[3].id!!.toInt(), semanaMediodia[4].id!!.toInt(), semanaMediodia[5].id!!.toInt(), semanaMediodia[6].id!!.toInt())
                Log.d("lunesCo", semanaMediodia[dia].nombre.toString())
                if (semanaMediodia[dia].categoria.contains("Primer plato")){
                    semanaAcompanamientoMediodia[dia] =
                        dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                    Log.d("lunesCo", semanaAcompanamientoMediodia[dia].nombre.toString())
                }

                if (dataPostre.size >= 1) {
                    semanaPostre[dia] = dataPostre.randomOrNull()!!
                    Log.d("lunesPos", semanaPostre[dia].nombre.toString())
                }
                if (dataMerienda.size >= 1) {
                    semanaMerienda[dia] = dataMerienda.randomOrNull()!!
                    Log.d("lunesMeri", semanaMerienda[dia].nombre.toString())
                }

                semanaCena[dia]=  obtenerCena(it, semanaCena[0].id!!.toInt(), semanaCena[1].id!!.toInt(), semanaCena[2].id!!.toInt(), semanaCena[3].id!!.toInt(), semanaCena[4].id!!.toInt(), semanaCena[5].id!!.toInt(), semanaCena[6].id!!.toInt())
                Log.d("lunesCe", semanaCena[dia].nombre.toString())
                if (semanaCena[dia].categoria.contains("Primer plato")){
                    semanaAcompanamientoCena[dia] =
                        dataAcompanamiento.randomOrNull()!! // randomOrNull devuelve null si la lista está vacía
                    Log.d("lunesCe", semanaAcompanamientoCena[dia].nombre.toString())
                }



                dia += 1
            }
        }
    }


    //Función para buscar recetas en la base de datos y mostrarlas
    fun cargarRecetas(baseGuardado: SQLiteDatabase): Boolean {

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



        val cantidadMediodia = dataMediodia.filter { receta ->
            (receta.categoria as? List<String>)?.contains("Acompañamiento") == false
        }
        val cantidadCena = dataCena.filter { receta ->
            (receta.categoria as? List<String>)?.contains("Acompañamiento") == false
        }
        if (dataPrimerPlato.size >= 1) {
            if (dataAcompanamiento.size >= 1) {
                if (cantidadMediodia.size >= 7 )  {
                    return if (cantidadCena.size >= 7)  {
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

    private fun obtenerCena(cat : String, idLunes : Int,idMartes : Int, idMiercoles : Int, idJueves : Int, idViernes : Int, idSabado : Int, idDomingo : Int) : Receta {
        when(cat) {
            "Carne" -> {
                var intento = 0
                var elegido : Receta?
                do {
                    do {
                        // Filtrar los registros cuyo segundo array contiene "carne"
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains("Carne") == false) && ((receta.categoria as? List<String>)?.contains("Acompañamiento") == false)

                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataCena.randomOrNull()!!
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
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains("Pescado") == false) && ((receta.categoria as? List<String>)?.contains("Acompañamiento") == false)
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataCena.randomOrNull()!!
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
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains("Legumbres") == false) && ((receta.categoria as? List<String>)?.contains("Acompañamiento") == false)
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataCena.randomOrNull()!!
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
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains("Cereal") == false) && ((receta.categoria as? List<String>)?.contains("Acompañamiento") == false)
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataCena.randomOrNull()!!
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
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains("Huevo") == false) && ((receta.categoria as? List<String>)?.contains("Acompañamiento") == false)
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataCena.randomOrNull()!!
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
                        val resultados = dataCena.filter { receta ->
                            ((receta.categoria as? List<String>)?.contains("Verdura") == false) && ((receta.categoria as? List<String>)?.contains("Acompañamiento") == false)
                        }
                        // Elegir un registro al azar
                        elegido = resultados.randomOrNull() // randomOrNull devuelve null si la lista está vacía
                        intento +=1
                    }while (elegido == null && intento <= 3)
                    if (elegido == null){
                        elegido = dataCena.randomOrNull()!!
                    }
                } while ( elegido!!.id == idLunes || elegido.id == idMartes || elegido.id == idMiercoles|| elegido.id == idJueves || elegido.id == idViernes || elegido.id == idSabado|| elegido.id == idDomingo)
                return elegido

            }
        }
    }

    }
