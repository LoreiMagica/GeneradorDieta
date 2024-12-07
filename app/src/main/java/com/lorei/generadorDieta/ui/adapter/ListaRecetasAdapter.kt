package com.lorei.generadorDieta.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.RecetaObjetoBinding
import com.lorei.generadorDieta.model.Receta

/**
 * Adapter del listado de recetas de su respectiva sección
 */
class ListaRecetasAdapter(arrayNombres : Array<out String>,
    private var data: List<Receta> // Listado de enlaces
) : RecyclerView.Adapter<ListaRecetasAdapter.ViewHolder>() {

    // Listener para obtener el enlace seleccionado al hacer click
    private var listener : OnClickListener? = null
    val arrayNombres = arrayNombres


    //Interface y función Onclick para el contenedor
    interface OnClickListener {
        fun onItemClick(id : Int)
    }

    fun setOnClickListener (listener: OnClickListener) {
        this.listener = listener
    }
    /**
     * Clase interna que almacena la vista que tendrá cada elemento de la lista
     */
    inner class ViewHolder(val binding: RecetaObjetoBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Método que crea el contenedor de la vista de cada elemento de la lista
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecetaObjetoBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    /**
     * Método que se ejecuta por cada elemento que haya en la lista y
     * que inicializa los elementos de la lista
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            with(holder.binding) {
                // Se muestra el titulo, descripcción y el resto de textViews
                nombreReceta.text = nombre

                var listaCategoria = ArrayList<String>()
                for (i in categoria) {
                    listaCategoria.add(arrayNombres.getOrElse(i) { "Cat" })
                }
                categoriaReceta.text = listaCategoria.toString()

                // Se establece el método onclik
                objetoReceta.setOnClickListener { listener?.onItemClick(id!!) }
            }
        }
    }

    /**
     * Método que devuelve el total de elementos que contiene la lista
     */
    override fun getItemCount() = data.size

    /**
     * Función para setear en el adapatador la lista filtrada en el ViewModel
     */
    fun filterList(filteredList: List<Receta>) {
        data = filteredList
        notifyDataSetChanged()
    }





}