package com.lorei.generadorDieta.ui.fragment

import DetalleRecetaViewModel
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.DetalleRecetaLayoutBinding
import java.util.*


class DetalleRecetaFragment : Fragment() {
    private lateinit var binding: DetalleRecetaLayoutBinding
    private lateinit var viewModel: DetalleRecetaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetalleRecetaLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DetalleRecetaViewModel::class.java]

        val arrayNombres = requireActivity().resources.getStringArray(R.array.receta_categoria)

        //Abrimos la base de datos
        val baseGuardado = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)

        //Obtenemos también la id para buscarla en la base de datos
        val id = arguments!!.getInt("idReceta")
        val receta = viewModel.cargarRecetas(baseGuardado, id)
        baseGuardado.close()

        //Obtenemos y seteamos las categorías
        for (cat in receta.categoria)
            addChipToGroup(arrayNombres.getOrElse(cat) { "Error" }, binding.recetaCategorias)


        //Obtenemos y seteamos las categorías
        for (hora in receta.horas)
            addChipToGroup(arrayNombres.getOrElse(hora) { "Error" }, binding.listahoras)

        //También los ingredientes, como una lista html
        var ingredientes = "<ul>"
        for (ing in receta.ingredientes) {
            ingredientes += "<li><b>$ing</b></li>"
        }
        ingredientes += "</ul>"
        binding.recetaIngredientes.text = Html.fromHtml(ingredientes)

        //Y ahora seteamos el resto de valores en sus textviews
        binding.recetaNombre.text = receta.nombre
        binding.recetaPreparacion.text = receta.preparacion
        binding.recetaUrl.text = receta.url
        binding.recetaCalorias.text = receta.calorias.toString() + "kcal"

        //Hacemos que la url sea clickable
        Linkify.addLinks(binding.recetaUrl, Linkify.WEB_URLS)
        binding.recetaUrl.movementMethod = LinkMovementMethod.getInstance()


        binding.btEliminar.setOnClickListener {
            val ostiaBorrar = AlertDialog.Builder(context)
            ostiaBorrar.setTitle("¿Borrar?")
            ostiaBorrar.setMessage("¿Estás seguro/a de borrar esta deliciosa receta de "+receta.nombre + "?")

            ostiaBorrar.setPositiveButton(R.string.si) { dialog, which ->
                val baseGuardado = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)
                baseGuardado.execSQL("delete from recetas where numero='$id'");
                baseGuardado.close();
                findNavController().navigate(R.id.nav_receta)
            }

            ostiaBorrar.setNegativeButton(R.string.no) { dialog, which ->
                Toast.makeText(context,
                    R.string.cancelar, Toast.LENGTH_SHORT).show()
            }
            ostiaBorrar.show()
        }

        binding.btEditar.setOnClickListener {
            //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
            val bundle = Bundle()
            bundle.putInt("idReceta", id)
            //Y con esto pasamos a la siguiente pantalla
            findNavController().navigate(R.id.nav_editar_receta, bundle)
        }

        return binding.root
    }

    //Función para añadir categorías
    private fun addChipToGroup(text: String, chipGroup: ChipGroup) {
        val chip = Chip(activity)
        chip.text = text
        chip.isCloseIconVisible = false // No permite eliminar el chip

        chipGroup.addView(chip)
    }
}