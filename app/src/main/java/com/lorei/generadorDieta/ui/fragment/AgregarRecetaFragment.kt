package com.lorei.generadorDieta.ui.fragment

import ListaRecetasViewModel
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
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
import com.lorei.generadorDieta.model.Receta
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

        // Creamos un par de arrays para almacenar el valor de los chips de horas comida
        var horasElegidas = mutableListOf<Int>()
        var categoriasElegidas = mutableListOf<Int>()

        val arrayNombres = resources.getStringArray(R.array.receta_categoria)

        val chipListHora : ArrayList<String> = arrayListOf()
        chipListHora.add(arrayNombres.getOrElse(9) { "Comida completa" })
        chipListHora.add(arrayNombres.getOrElse(10) { "Primer plato" })
        chipListHora.add(arrayNombres.getOrElse(11) { "Acompañamiento" })
        chipListHora.add(arrayNombres.getOrElse(13) { "Desayuno" })
        chipListHora.add(arrayNombres.getOrElse(14) { "Mediamañana" })
        chipListHora.add(arrayNombres.getOrElse(12) { "Comida mediodía" })
        chipListHora.add(arrayNombres.getOrElse(15) { "Merienda" })
        chipListHora.add(arrayNombres.getOrElse(16) { "Cena" })
        chipListHora.add(arrayNombres.getOrElse(17) { "Postre" })
        val chipIdsHora : ArrayList<Int> = arrayListOf()
        chipIdsHora.add(9)
        chipIdsHora.add(10)
        chipIdsHora.add(11)
        chipIdsHora.add(13)
        chipIdsHora.add(14)
        chipIdsHora.add(12)
        chipIdsHora.add(15)
        chipIdsHora.add(16)
        chipIdsHora.add(17)

        //Y hacemos lo mismo para las categorías
        val chipListCat : ArrayList<String> = arrayListOf()
        chipListCat.add(arrayNombres.getOrElse(1) { "Carne" })
        chipListCat.add(arrayNombres.getOrElse(2) { "Pescado" })
        chipListCat.add(arrayNombres.getOrElse(3) { "Legumbres" })
        chipListCat.add(arrayNombres.getOrElse(4) { "Cereal" })
        chipListCat.add(arrayNombres.getOrElse(5) { "Huevo" })
        chipListCat.add(arrayNombres.getOrElse(6) { "Verdura" })
        chipListCat.add(arrayNombres.getOrElse(7) { "Fruta" })
        chipListCat.add(arrayNombres.getOrElse(8) { "Lácteo" })
        val chipIdsCat : ArrayList<Int> = arrayListOf()
        chipIdsCat.add(1)
        chipIdsCat.add(2)
        chipIdsCat.add(3)
        chipIdsCat.add(4)
        chipIdsCat.add(5)
        chipIdsCat.add(6)
        chipIdsCat.add(7)
        chipIdsCat.add(8)


        //Le damos función al clickar los chips
        chipListHora.forEachIndexed { index, s ->
            val chip = Chip(requireContext())
            chip.text = s
            chip.id = chipIdsHora[index]
            chip.isCheckable = true

            binding.horaComida.addView(chip)
        }
        //Le damos función al clickar los chips a las categorías
        chipListCat.forEachIndexed { index, s ->
            val chip = Chip(requireContext())
            chip.text = s
            chip.id = chipIdsCat[index]
            chip.isCheckable = true

            binding.listaCategorias.addView(chip)
        }

        //Con esto contamos las horas clickadas
        binding.apply {
            horaComida.setOnCheckedStateChangeListener { group, checkedIds ->
                horasElegidas.clear()
               horasElegidas = checkedIds as ArrayList<Int>
            }
        }

        //Con esto contamos las categorías clickadas
        binding.apply {
            listaCategorias.setOnCheckedStateChangeListener { group, checkedIds ->
                categoriasElegidas.clear()
                categoriasElegidas = checkedIds as ArrayList<Int>
            }
        }

        //Damos función al botón de agregar receta
        binding.btAgregar.setOnClickListener {
            //Comprobamos que al menos haya un nombre y categoría para no crear una receta vacía
            if(binding.etName.text!!.isNotEmpty() && binding.listaCategorias.childCount > 0 && horasElegidas.size > 0){
                val bd = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)
                val cursor: Cursor = bd.rawQuery("Select name from sqlite_master where type = 'table' and name like 'recetas' ", null)
                var existe = false
                //Empezamos a recorrer la respuesta recibida
                if (cursor.count > 0) {
                    //Accedemos a la tabla recetas en la base de datos
                    val cursorReceta: Cursor = bd.rawQuery("select  nombre from recetas", null)

                    //Procesamos los datos obtenidos para comparar con el nombre introducido
                    if (cursorReceta.count > 0) {
                        while (cursorReceta.moveToNext()) {
                            if (cursorReceta.getString(0).lowercase() == binding.etName.text.toString().lowercase()) {
                                existe = true
                            }
                        }
                    }
                    cursorReceta.close()
                }
                cursor.close()

                //Si no existe, se agrega
                if (!existe){
                    //Llamamos a la función y le pasamos la base de datos
                    agregarReceta(requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null), horasElegidas, categoriasElegidas)
                } else {
                    //En caso contrario, se avisa al usuario
                    Toast.makeText(activity, R.string.avisoExiste, Toast.LENGTH_SHORT)
                        .show()
                }

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
    fun agregarReceta(db : SQLiteDatabase, horasElegidas : MutableList<Int>, categoriasElegidas : MutableList<Int>) {
        val baseGuardado = db

        //Creamos la tabla en caso de no existir
        baseGuardado.execSQL("CREATE TABLE if not exists recetas (numero integer primary key AUTOINCREMENT, nombre VARCHAR(200), categorias VARCHAR(200), horasComida VARCHAR(200), preparacion VARCHAR(500), ingredientes VARCHAR(200), calorias tinyint, url VARCHAR(200))")

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

        val json1 = JSONObject()
        json1.put("listaCategorias", JSONArray(categoriasElegidas))
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

        val json3 = JSONObject()
        json3.put("listaHoras", JSONArray(horasElegidas))
        val jsonHoras: String = json3.toString()

        /* Leer
        val json = JSONObject(stringreadfromsqlite)
        val items: ArrayList = json.optJSONArray("uniqueArrays")
        */

        //Guardamos to,dos los valores en la variable, y sumamos 1 al número de registro para añadirlo
        //registro.put("numero", (total+1))
        registro.put("nombre", binding.etName.text.toString())
        registro.put("categorias", jsonCategorias)
        registro.put("horasComida", jsonHoras)
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