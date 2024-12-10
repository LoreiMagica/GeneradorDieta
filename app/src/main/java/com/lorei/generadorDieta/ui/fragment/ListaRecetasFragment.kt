package com.lorei.generadorDieta.ui.fragment

import ListaRecetasViewModel
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.ListarecetasLayoutBinding
import com.lorei.generadorDieta.ui.adapter.ListaRecetasAdapter
import java.util.*


class ListaRecetasFragment : Fragment() {
    private lateinit var binding: ListarecetasLayoutBinding
    private lateinit var viewModel: ListaRecetasViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListarecetasLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ListaRecetasViewModel::class.java]
        viewModel.cargarAdapter(requireContext())

        // Comprobamos si es la primera vez que el usuario entra a esta ventana para ofrecerle un tutorial
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val inicialAgregar = sharedPref.getBoolean("inicialReceta", false)

        if(!inicialAgregar){
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_agregar_recetas)
                .setMessage(R.string.dialog_inicial_receta)
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
            val editor = sharedPref.edit()
            editor.putBoolean("inicialReceta", true)
            editor.apply()
        }
        //Cargamos la base de datos para obtener las recetas
        val baseGuardado = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)

        //Llamamos al método para obtener estas
        viewModel.cargarRecetas(baseGuardado)
        baseGuardado.close()

        // Se inicializa el listado de enlaces
        binding.listaRecetas.apply {
            adapter = viewModel.adapter
            layoutManager = GridLayoutManager(requireContext(), GridLayoutManager.VERTICAL)
        }

        //Seteamos la función del botón agregar receta
        binding.agregarReceta.setOnClickListener { findNavController().navigate(R.id.nav_agregar_receta) }

        // Listener para el clic prolongado
        binding.agregarReceta.setOnLongClickListener {
            Toast.makeText(requireContext(), R.string.explica_btAgregar, Toast.LENGTH_SHORT).show()
            true // Devuelve true para indicar que el evento fue manejado
        }

        //Y la de los detalles de la receta
        viewModel.adapter!!.setOnClickListener(object : ListaRecetasAdapter.OnClickListener {
            override fun onItemClick(id: Int) {

                //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                val bundle = Bundle()
                bundle.putInt("idReceta", id)
                //Y con esto pasamos a la siguiente pantalla
                findNavController().navigate(R.id.nav_detalle_receta, bundle)
            }
        } )

        //Le damos funcióna los filtros de nombre y categoría
        binding.buscarCategoria.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.filterCategoria(binding.buscarCategoria.getItemAtPosition(p2).toString(), p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.etReceta.addTextChangedListener {
            viewModel.filterNombre(binding.etReceta.text.toString())
        }
        return binding.root
    }
}