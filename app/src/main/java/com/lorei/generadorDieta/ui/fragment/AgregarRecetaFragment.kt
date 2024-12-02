package com.lorei.generadorDieta.ui.fragment

import ListaRecetasViewModel
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.AgregarRecetaLayoutBinding
import org.json.JSONArray
import org.json.JSONObject


class AgregarRecetaFragment : Fragment() {
    private lateinit var binding: AgregarRecetaLayoutBinding
    private lateinit var viewModel: ListaRecetasViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AgregarRecetaLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ListaRecetasViewModel::class.java]

        //Se carga la lista de categorías de comidas
        val categorias = resources.getStringArray(R.array.receta_categoria)

        //Se carga un modelo de lista, y la lista de categorías previamente cargada, en el input text de categorías
        val adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_dropdown_item_1line, categorias)
        binding.etCategoria.setAdapter(adapter)

        //función para agregar una categoría de la lista
        binding.etCategoria.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            if (selectedItem != null) {
                // Agregar el chip solo si no existe ya
                if (!isChipAlreadyAdded(selectedItem, binding.listaCategorias)) {
                    addChipToGroup(selectedItem, binding.listaCategorias)
                } else {

                    //En caso de ya existir, se avisa al usuario
                    Toast.makeText(activity, (getString(R.string.avisoRepe) + " " + selectedItem), Toast.LENGTH_SHORT)
                        .show()
                }
                binding.etCategoria.text.clear() // Limpiar el campo de entrada
            }
    }

        //Hacemos lo mismo con los ingredientes. Al pulsar el botón, comprueba si existe y lo agrega o no
        binding.botonAgregarIngrediente.setOnClickListener {
            binding.etIngredientes.setText(
                binding.etIngredientes.text.toString().substring(0, 1).uppercase() +
                        binding.etIngredientes.text.toString().substring(1).lowercase(),
            )
            val inputText = binding.etIngredientes.text.toString().trim()

            if (inputText.isNotEmpty()) {
                // Agregar el chip solo si no existe ya
                if (!isChipAlreadyAdded(inputText, binding.listaIngredientes)) {
                    addChipToGroup(inputText, binding.listaIngredientes)
                } else {
                    //En caso de ya existir, se avisa al usuario
                    Toast.makeText(activity, (getString(R.string.avisoRepe) + " " + inputText), Toast.LENGTH_SHORT)
                        .show()
                }
                    binding.etIngredientes.text?.clear()
                }
            }

        //Damos función al botón de agregar receta
        binding.btAgregar.setOnClickListener {

            //Comprobamos que al menos haya un nombre y categoría para no crear una receta vacía
            if(binding.etName.text!!.isNotEmpty() && binding.listaCategorias.childCount > 0){
                //Llamamos a la función y le pasamos la base de datos
                agregarReceta(requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null))
            } else {
                //En caso contrario, se avisa al usuario
                Toast.makeText(activity, R.string.avisoMinimo, Toast.LENGTH_SHORT)
                    .show()
            }
            }

        return binding.root
    }

    //Función para comprobar si existe esa categoría
private fun isChipAlreadyAdded(text: String, chipGroup: ChipGroup): Boolean {
    for (i in 0 until chipGroup.childCount) {
        val chip = chipGroup.getChildAt(i) as Chip
        if (chip.text == text) {
            return true
        }
    }
    return false
}
    //Función para añadir categorías
    private fun addChipToGroup(text: String, chipGroup: ChipGroup) {
        val chip = Chip(activity)
        chip.text = text
        chip.isCloseIconVisible = true // Permite eliminar el chip
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip) // Eliminar el chip
        }
        chipGroup.addView(chip)
    }

    //Esta función agrega finalmente la receta
    fun agregarReceta(db : SQLiteDatabase) {
        val baseGuardado = db

        //Creamos la tabla en caso de no existir
        baseGuardado.execSQL("CREATE TABLE if not exists recetas (numero integer primary key AUTOINCREMENT, nombre VARCHAR(200), categorias VARCHAR(200), preparacion VARCHAR(500), ingredientes VARCHAR(200), calorias tinyint, url VARCHAR(200))")

        //CDreamos la variable donde almacenaremos la receta a agregar
        val registro = ContentValues()

        //Obtenemos el total de registros de la tabla
        /*val cursorTotal = baseGuardado.rawQuery("SELECT COUNT(*) FROM recetas", null)
        var total = 0
        if (cursorTotal.count > 0) {
            cursorTotal.moveToFirst()
            total = cursorTotal.getInt(0)

        }


        cursorTotal.close()
         */

        //Almacenamos todas las categorías de los chips para guardarlas juntas
        val listaCategorias = ArrayList<String>()
        for (i in 0 until binding.listaCategorias.childCount) {
            val chip = binding.listaCategorias.getChildAt(i) as Chip
            listaCategorias.add(chip.text.toString())
        }
        val json1 = JSONObject()
        json1.put("listaCategorias", JSONArray(listaCategorias))
        val jsonCategorias: String = json1.toString()

        //Y lo mismo con los ingredientes
        val listaIngredientes = ArrayList<String>()
        for (i in 0 until binding.listaIngredientes.childCount) {
            val chip = binding.listaIngredientes.getChildAt(i) as Chip
            listaIngredientes.add(chip.text.toString())
        }
        val json2 = JSONObject()
        json2.put("listaIngredientes", JSONArray(listaIngredientes))
        val jsonIngredientes: String = json2.toString()

        /* Leer
        val json = JSONObject(stringreadfromsqlite)
        val items: ArrayList = json.optJSONArray("uniqueArrays")
        */

        //Guardamos to,dos los valores en la variable, y sumamos 1 al número de registro para añadirlo
        //registro.put("numero", (total+1))
        registro.put("nombre", binding.etName.text.toString())
        registro.put("categorias", jsonCategorias)
        registro.put("preparacion", binding.etPreparacion.text.toString())
        registro.put("ingredientes", jsonIngredientes)
        registro.put("calorias", binding.etCaloria.text.toString())
        registro.put("url", binding.etUrl.text.toString())

        //Intertamos y cerramos la conexión
        baseGuardado.insert("recetas", null, registro)
        baseGuardado.close()
        findNavController().navigate(R.id.nav_receta)

    }
}