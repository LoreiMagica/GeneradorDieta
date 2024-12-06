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
    val adapter get() = _adapter

    val data = mutableListOf<Receta>()

    // Se inicializa doc y obtiene los deportes.
    init {
        _adapter = ListaRecetasAdapter(data)

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
                "select numero, nombre, categorias, preparacion, ingredientes, calorias, url from recetas",
                null
            )

            //Procesamos los datos obtenidos
            if (cursorReceta.count > 0) {
                while (cursorReceta.moveToNext()) {

                    //Transformamos los arrays de categoría
                    val json1 = JSONObject(cursorReceta.getString(2))
                    val jsonCategorias = json1.optJSONArray("listaCategorias")
                    val listaCategorias : ArrayList<String> = ArrayList()

                    for ( i in 0 until jsonCategorias!!.length()){
                        listaCategorias.add(jsonCategorias[i].toString());
                    }

                    //Y también con ingredientes
                    val json2 = JSONObject(cursorReceta.getString(4))
                    val jsonIngredientes = json2.optJSONArray("listaIngredientes")
                    val listaIngredientes : ArrayList<String> = ArrayList()

                    for ( i in 0 until jsonIngredientes!!.length()){
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

                    //Y lo agregamos a la lista para el recyclerview
                    data.add(receta)
                }
            }
        }
    }
}
