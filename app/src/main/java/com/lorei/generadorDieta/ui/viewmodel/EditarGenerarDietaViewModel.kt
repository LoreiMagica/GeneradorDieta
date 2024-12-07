import android.app.Application
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.openOrCreateDatabase
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.lorei.generadorDieta.model.Receta
import com.lorei.generadorDieta.ui.adapter.ListaRecetasAdapter
import org.json.JSONObject
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext


/**
 * Clase la cual obtiene los eventos de Isla Cristina desde firebase y los manda al adapter
 */
class EditarGenerarDietaViewModel() : ViewModel() {

    private var _adapter: ListaRecetasAdapter? = null

    val data = mutableListOf<Receta>()

    // Se inicializa doc y obtiene los deportes.
    init {

    }

    //Función para buscar recetas en la base de datos y mostrarlas
    fun cargarRecetas(baseGuardado: SQLiteDatabase) {

        //Limpiamos el array para evitar duplicados
        data.clear()

        //Comprobamos que la tablas recetas existe
        val cursor: Cursor = baseGuardado.rawQuery("Select name from sqlite_master where type = 'table' and name like 'recetas' ", null)

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
                    val listaCategorias : ArrayList<Int> = ArrayList()

                    for ( i in 0 until jsonCategorias!!.length()){
                        listaCategorias.add(jsonCategorias[i].toString().toInt());
                    }

                    //Y también con ingredientes
                    val json2 = JSONObject(cursorReceta.getString(5))
                    val jsonIngredientes = json2.optJSONArray("listaIngredientes")
                    val listaIngredientes : ArrayList<String> = ArrayList()

                    for ( i in 0 until jsonIngredientes!!.length()){
                        listaIngredientes.add(jsonIngredientes[i].toString());
                    }

                    //Transformamos los arrays de categoría
                    val json3 = JSONObject(cursorReceta.getString(3))
                    val jsonHoras = json3.optJSONArray("listaHoras")
                    val listaHoras : ArrayList<Int> = ArrayList()

                    for ( i in 0 until jsonHoras!!.length()){
                        listaHoras.add(jsonHoras[i].toString().toInt());
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

                    //Y lo agregamos a la lista para el recyclerview
                    data.add(receta)
                }
            }
        }
    }
}
