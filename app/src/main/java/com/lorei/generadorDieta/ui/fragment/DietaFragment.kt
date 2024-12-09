package com.lorei.generadorDieta.ui.fragment

import DietaViewModel
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.DietaLayoutBinding
import com.lorei.generadorDieta.model.Receta
import java.util.*


class DietaFragment : Fragment() {
    private lateinit var binding: DietaLayoutBinding
    private lateinit var viewModel: DietaViewModel
    val PERMISSION_CODE = 101


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DietaLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DietaViewModel::class.java]

        primeraVez()

        val basePdf = requireActivity().openOrCreateDatabase(
            "baseGuardado.db",
            Context.MODE_PRIVATE,
            null
        )
        //Hacemos la consulta a la BBDD
        val cursor: Cursor = basePdf.rawQuery(
            "Select name from sqlite_master where type = 'table' and name like 'dietaActual' ",
            null
        )
        if (cursor.count > 0) {
            //Accedemos a la tabla dietaActual en la base de datos
            val cursorReceta: Cursor = basePdf.rawQuery(
                "select numero, desayuno, mediamanana, comida, acompanamientoComida, postre, merienda, cena, acompanamientoCena from dietaActual where numero=1",
                null
            )

            //Procesamos los datos obtenidos
            if (cursorReceta.count > 0) {
                while (cursorReceta.moveToNext()) {

                    //Obtenemos y convertimos los datos de gson
                    val jsonD =
                        cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("desayuno"))
                    //Comprobamos que los datos obtenidos sean un array y no esté vacío
                    if (jsonD != "desayuno") {
                        binding.noDietaFondo.visibility = View.GONE
                        binding.noDieta.visibility = View.GONE
                    }
                }
            }
            cursorReceta.close()
            cursor.close()
            basePdf.close()
        }else {
            binding.noDieta.visibility = View.VISIBLE
            binding.noDietaFondo.visibility =View.VISIBLE
        }



        //Seteamos los números en el calendario
        //Obtenemos la fecha de hoy
        val cal = Calendar.getInstance()
        //Sacamos el día de la fecha de hoy
        var dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        //Lo seteamos a hoy
        binding.diaLunes.text = dayOfMonth.toString()

        //Y repetimos con cada día
        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaMartes.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaMiercoles.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaJueves.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaViernes.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaSabado.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaDomingo.text = (dayOfMonth).toString()


        //Le damos función a los botones de días
        //Creamos un array de días en español
        val diasSemana = listOf("", getString(R.string.dia_lunes), getString(R.string.dia_martes), getString(R.string.dia_miercoles), getString(R.string.dia_jueves), getString(R.string.dia_viernes), getString(R.string.dia_sabado), getString(R.string.dia_domingo))

        //Llevamos el día a hoy
        val calHoy = Calendar.getInstance()
        calHoy.add(Calendar.DAY_OF_YEAR, -1)

        //Seteamos el texto del día de hoy, y el color a activo
        binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
        val colorPrimary = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)

        binding.fondoLunes.setBackgroundColor(colorPrimary)
        //Cargamos la base de datos para obtener las recetas
        val baseGuardado = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)
        obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), baseGuardado)

        //Y repetimos con cada día
        binding.diaLunes.setOnClickListener {

            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), baseGuardado)

            binding.fondoLunes.setBackgroundColor(colorPrimary)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaMartes.setOnClickListener {
            val calHoy = Calendar.getInstance()
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), baseGuardado)

            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(colorPrimary)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaMiercoles.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 1)
            binding.diaHoy.text = diasSemana[ calHoy.get(Calendar.DAY_OF_WEEK)]
            obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), baseGuardado)

            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(colorPrimary)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaJueves.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 2)
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), baseGuardado)

            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(colorPrimary)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaViernes.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 3)
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), baseGuardado)

            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(colorPrimary)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaSabado.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 4)
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), baseGuardado)

            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(colorPrimary)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaDomingo.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 5)
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), baseGuardado)

            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(colorPrimary)
        }

        //Damos función al botón de generar dieta
        binding.btGenerarDieta.setOnClickListener {
            //Comprobamos la versión de android para saber si pedir permisos o no
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                //Observamos el liveData para mostrar toasts desde el viewmodel
                viewModel.mostrarToast.observe(viewLifecycleOwner) { messageResId ->
                    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
                }

                //Lo mismo, para copiar la lista de la compra
                viewModel.copiarCompra.observe(viewLifecycleOwner) { compra ->
                    val clipboard =
                        requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("compra", compra)
                    clipboard.setPrimaryClip(clip)
                }

                //Obtenemos la dieta y el primer día de la semana para crear el pdf
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                val dieta = sharedPref!!.getInt("dieta", 0)
                val savedPrimerDía = sharedPref.getInt("primerSemana", 0)


                //Y entonces actualizamos la dieta de hoy
                viewModel.generarDieta(baseGuardado, requireContext(), diasSemana, dieta, savedPrimerDía)

                viewModel.actualizarDieta.observe(viewLifecycleOwner) { compra ->
                    val calHoy = Calendar.getInstance()
                    calHoy.add(Calendar.DAY_OF_YEAR, -1)
                    val base = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)

                    obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), base)
                }

            } else {
                //chequeamos los permisos
                if (checkPermissions()) {
                    viewModel.mostrarToast.observe(viewLifecycleOwner) { messageResId ->
                        Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
                    }
                    viewModel.copiarCompra.observe(viewLifecycleOwner) { compra ->
                        val clipboard =
                            requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("compra", compra)
                        clipboard.setPrimaryClip(clip)
                    }
                    //Cargamos la base de datos para obtener las recetas
                    val baseGuardado = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)

                    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                    val dieta = sharedPref!!.getInt("dieta", 0)
                    val sharedDia = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
                    val savedPrimerDía = sharedDia.getInt("primerSemana", 0)

                    //Llamamos al método para obtener estas
                    viewModel.generarDieta(baseGuardado, requireContext(), diasSemana, dieta, savedPrimerDía)

                    viewModel.actualizarDieta.observe(viewLifecycleOwner) { compra ->
                        val calHoy = Calendar.getInstance()
                        calHoy.add(Calendar.DAY_OF_YEAR, -1)
                        val base = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)

                        obtenerDietaActual(calHoy.get(Calendar.DAY_OF_WEEK), base)
                    }
                } else {
                    //Pedimos los permisos
                    requestPermission()
                }
            }
        }

        //Función para volver a generar la actual dieta
        binding.btPdf.setOnClickListener {
            val basePdf = requireActivity().openOrCreateDatabase(
                "baseGuardado.db",
                Context.MODE_PRIVATE,
                null
            )
                //Hacemos la consulta a la BBDD
            val cursor: Cursor = basePdf.rawQuery(
                "Select name from sqlite_master where type = 'table' and name like 'dietaActual' ",
                null
            )
            if (cursor.count > 0) {
                //Accedemos a la tabla dietaActual en la base de datos
                val cursorReceta: Cursor = basePdf.rawQuery(
                    "select numero, desayuno, mediamanana, comida, acompanamientoComida, postre, merienda, cena, acompanamientoCena from dietaActual where numero=1",
                    null
                )

                //Procesamos los datos obtenidos
                if (cursorReceta.count > 0) {
                    while (cursorReceta.moveToNext()) {

                        //Obtenemos y convertimos los datos de gson
                        val jsonD =
                            cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("desayuno"))
                        //Comprobamos que los datos obtenidos sean un array y no esté vacío
                        if (jsonD != "desayuno") {
                            //LiveData, para copiar la lista de la compra
                            viewModel.copiarCompra.observe(viewLifecycleOwner) { compra ->
                                val clipboard =
                                    requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("compra", compra)
                                clipboard.setPrimaryClip(clip)
                            }
                            val basePdf = requireActivity().openOrCreateDatabase(
                                "baseGuardado.db",
                                Context.MODE_PRIVATE,
                                null
                            )
                            val sharedDia = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
                            val savedPrimerDía = sharedDia.getInt("primerSemana", 0)

                            viewModel.volverAGenerarPdf(requireContext(), diasSemana, basePdf, savedPrimerDía)
                        }
                    }
                }
                cursorReceta.close()
                cursor.close()
                basePdf.close()
            }else {
                Toast.makeText(requireContext(), getString(R.string.pdf_volver_generar), Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para el clic prolongado
        binding.btPdf.setOnLongClickListener {
            Toast.makeText(requireContext(), R.string.explica_botonPdf, Toast.LENGTH_SHORT).show()
            true // Devuelve true para indicar que el evento fue manejado
        }

        //Este botón te lleva al menú de editar preferencias de la dieta
        binding.btEditarDieta.setOnClickListener{
            findNavController().navigate(R.id.nav_editar_generar_dieta)
        }

        // Listener para el clic prolongado
        binding.btEditarDieta.setOnLongClickListener {
            Toast.makeText(requireContext(), R.string.explica_btEditarMenu, Toast.LENGTH_SHORT).show()
            true // Devuelve true para indicar que el evento fue manejado
        }

        return binding.root
    }
    /**
     * Método para obtener un color dinámico desde un atributo del tema.
     */
    private fun getColorFromAttr(attr: Int): Int {
        val typedValue = TypedValue()
        context!!.theme.resolveAttribute(attr, typedValue, true)
        return if (typedValue.resourceId != 0) {
            ContextCompat.getColor(context!!, typedValue.resourceId)
        } else {
            typedValue.data
        }
    }

    /**
     * Método para comprobar los permisos del móvil
     */
    fun checkPermissions(): Boolean {

        // on below line we are creating a variable for both of our permissions.

        // on below line we are creating a variable for
        // writing to external storage permission
        var writeStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            WRITE_EXTERNAL_STORAGE
        )

        // on below line we are creating a variable
        // for reading external storage permission
        var readStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        )

        // on below line we are returning true if both the
        // permissions are granted and returning false
        // if permissions are not granted.
        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Método para solicitar permisos del móvil.
     */
    fun requestPermission() {

        // on below line we are requesting read and write to
        // storage permission for our application.
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), PERMISSION_CODE
        )
    }

    // on below line we are calling
    // on request permission result.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // on below line we are checking if the
        // request code is equal to permission code.
        if (requestCode == PERMISSION_CODE) {

            // on below line we are checking if result size is > 0
            if (grantResults.size > 0) {

                // on below line we are checking
                // if both the permissions are granted.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {

                    // if permissions are granted we are displaying a toast message.
                    Toast.makeText(requireActivity(), "Permission Granted..", Toast.LENGTH_SHORT).show()

                } else {

                    // if permissions are not granted we are
                    // displaying a toast message as permission denied.
                    Toast.makeText(requireActivity(), "Permission Denied..", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Método para setear la dieta actual.
     */
    private  fun obtenerDietaActual(dia : Int, baseGuardado : SQLiteDatabase) {

        //Primero comprimimos los botones que estarían comprimidos en el inicio
        val params1 = binding.btAccomidaHoy.layoutParams
        params1.width = ViewGroup.LayoutParams.MATCH_PARENT
        params1.height = 0

        val params2 = binding.btAccenaHoy.layoutParams
        params2.width = ViewGroup.LayoutParams.MATCH_PARENT
        params2.height = 0

        val params3 = binding.btPostrecenaHoy.layoutParams
        params3.width = ViewGroup.LayoutParams.MATCH_PARENT
        params3.height = 0

        val params4 = binding.btPostrecomidaHoy.layoutParams
        params4.width = ViewGroup.LayoutParams.MATCH_PARENT
        params4.height = 0
        binding.btAccomidaHoy.layoutParams = params1
        binding.btAccenaHoy.layoutParams = params2
        binding.btPostrecenaHoy.layoutParams = params3
        binding.btPostrecomidaHoy.layoutParams = params4



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
                        val arrayDesayuno = gson.fromJson(jsonD, Array<Receta>::class.java).toList()
                        if(arrayDesayuno[dia].nombre != "") {

                            //Y seteamos los datos en el botón
                            binding.btdesayunoHoy.setText(arrayDesayuno[dia].nombre)
                            //Y le damos acción para ir a los detalles de la receta
                            binding.btdesayunoHoy.setOnClickListener {
                                //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                val bundle = Bundle()
                                bundle.putInt("idReceta", arrayDesayuno[dia].id!!.toInt())
                                //Y con esto pasamos a la siguiente pantalla
                                findNavController().navigate(R.id.nav_detalle_receta, bundle)
                            }
                        }
                    }

                    //Y repetimos
                    val jsonMedia = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("mediamanana"))
                    if (jsonMedia != "mediamanana") {
                        val arrayMediamanana = gson.fromJson(jsonMedia, Array<Receta>::class.java).toList()
                        if(arrayMediamanana[dia].nombre != "") {
                            binding.btmediamananaHoy.setText(arrayMediamanana[dia].nombre)
                            binding.btmediamananaHoy.setOnClickListener {
                                //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                val bundle = Bundle()
                                bundle.putInt("idReceta", arrayMediamanana[dia].id!!.toInt())
                                //Y con esto pasamos a la siguiente pantalla
                                findNavController().navigate(R.id.nav_detalle_receta, bundle)
                            }
                        }
                    }
                    val jsonMedio = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("comida"))
                    if (jsonMedio != "comida") {
                        val arrayMediodia = gson.fromJson(jsonMedio, Array<Receta>::class.java).toList()

                        if(arrayMediodia[dia].nombre != "") {
                            binding.btcomidaHoy.setText(arrayMediodia[dia].nombre)
                            binding.btcomidaHoy.setOnClickListener {
                                //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                val bundle = Bundle()
                                bundle.putInt("idReceta", arrayMediodia[dia].id!!.toInt())
                                //Y con esto pasamos a la siguiente pantalla
                                findNavController().navigate(R.id.nav_detalle_receta, bundle)
                            }
                        }
                    }
                    val jsonAcMedio = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("acompanamientoComida"))
                    if(jsonAcMedio != "acompanamientoComida") {
                        val arrayAcMediodia = gson.fromJson(jsonAcMedio, Array<Receta>::class.java).toList()
                        if(arrayAcMediodia[dia].nombre != "") {
                            val params = binding.btAccomidaHoy.layoutParams
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            binding.btAccomidaHoy.layoutParams = params
                            binding.btAccomidaHoy.setText(arrayAcMediodia[dia].nombre)
                            binding.btAccomidaHoy.setOnClickListener {
                                //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                val bundle = Bundle()
                                bundle.putInt("idReceta", arrayAcMediodia[dia].id!!.toInt())
                                //Y con esto pasamos a la siguiente pantalla
                                findNavController().navigate(R.id.nav_detalle_receta, bundle)
                            }
                        }
                    }
                    val jsonPo = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("postre"))
                    if (jsonPo != "postre") {
                        val arrayPostre = gson.fromJson(jsonPo, Array<Receta>::class.java).toList()
                        if(arrayPostre[dia].nombre != "") {
                            val params = binding.btPostrecomidaHoy.layoutParams
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            binding.btPostrecomidaHoy.layoutParams = params
                            binding.btPostrecomidaHoy.setText(arrayPostre[dia].nombre)
                            binding.btPostrecomidaHoy.setOnClickListener {
                                //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                val bundle = Bundle()
                                bundle.putInt("idReceta", arrayPostre[dia].id!!.toInt())
                                //Y con esto pasamos a la siguiente pantalla
                                findNavController().navigate(R.id.nav_detalle_receta, bundle)
                            }
                            val params2 = binding.btPostrecomidaHoy.layoutParams
                            params2.width = ViewGroup.LayoutParams.MATCH_PARENT
                            params2.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            binding.btPostrecenaHoy.layoutParams = params2
                            binding.btPostrecenaHoy.setText(arrayPostre[dia].nombre)
                            binding.btPostrecenaHoy.setOnClickListener {
                                //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                val bundle = Bundle()
                                bundle.putInt("idReceta", arrayPostre[dia].id!!.toInt())
                                //Y con esto pasamos a la siguiente pantalla
                                findNavController().navigate(R.id.nav_detalle_receta, bundle)
                            }
                        }
                    }
                    val jsonMer = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("merienda"))
                    if (jsonMer != "merienda") {
                            val arrayMerienda =
                                gson.fromJson(jsonMer, Array<Receta>::class.java).toList()
                            if(arrayMerienda[dia].nombre != "") {

                                binding.btmeriendaHoy.setText(arrayMerienda[dia].nombre)
                                binding.btmeriendaHoy.setOnClickListener {
                                    //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                    val bundle = Bundle()
                                    bundle.putInt("idReceta", arrayMerienda[dia].id!!.toInt())
                                    //Y con esto pasamos a la siguiente pantalla
                                    findNavController().navigate(R.id.nav_detalle_receta, bundle)
                                }
                            }
                        }
                    val jsonCe = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("cena"))
                    if (jsonCe != "cena"&& jsonCe != "") {
                            val arrayCena =
                                gson.fromJson(jsonCe, Array<Receta>::class.java).toList()
                            if(arrayCena[dia].nombre != "") {

                                binding.btcenaHoy.setText(arrayCena[dia].nombre)
                                binding.btcenaHoy.setOnClickListener {
                                    //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                    val bundle = Bundle()
                                    bundle.putInt("idReceta", arrayCena[dia].id!!.toInt())
                                    //Y con esto pasamos a la siguiente pantalla
                                    findNavController().navigate(R.id.nav_detalle_receta, bundle)
                                }
                            }
                        }
                    val jsonAcCe = cursorReceta.getString(cursorReceta.getColumnIndexOrThrow("acompanamientoCena"))
                    if (jsonAcCe != "acompanamientoCena" && jsonAcCe != "") {
                            val arrayAcCena =
                                gson.fromJson(jsonAcCe, Array<Receta>::class.java).toList()
                            if(arrayAcCena[dia].nombre != "") {
                                val params = binding.btAccenaHoy.layoutParams
                                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                                binding.btAccenaHoy.layoutParams = params
                                binding.btAccenaHoy.setText(arrayAcCena[dia].nombre)
                                binding.btAccenaHoy.setOnClickListener {
                                    //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                                    val bundle = Bundle()
                                    bundle.putInt("idReceta", arrayAcCena[dia].id!!.toInt())
                                    //Y con esto pasamos a la siguiente pantalla
                                    findNavController().navigate(R.id.nav_detalle_receta, bundle)
                                }
                            }
                        }
                }
            }

        }
    }

    private fun primeraVez() {
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val primeraVez = sharedPref.getBoolean("primeraVez", true)
        if (primeraVez) {
                val dialogView = LinearLayout(requireActivity())
                dialogView.orientation = LinearLayout.VERTICAL


                // Creamos el CheckBox
                val checkBox = CheckBox(requireContext())
                checkBox.text = getString(R.string.dialog_inicial_acepto)
                dialogView.addView(checkBox)

                // Creamos el ImageButton para cambiar el idioma
                val languageButton = ImageButton(requireContext())
                languageButton.setImageResource(R.drawable.ic_language) // Aquí pones tu ícono
                languageButton.setBackgroundColor(0) // Eliminar el fondo predeterminado del botón
                languageButton.setContentDescription(getString(R.string.opcion_menu_idioma))
                dialogView.addView(languageButton)

                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.dialog_inicial_texto)
                    .setView(dialogView)
                    .setPositiveButton(
                        R.string.ok,
                        null
                    ) // Se define como `null` para personalizar luego
                    .create()
            dialog.setCancelable(false)


            dialog.setOnShowListener {
                    val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    positiveButton.isEnabled = false // Desactiva el botón al inicio

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        positiveButton.isEnabled = isChecked
                    }

                    positiveButton.setOnClickListener {
                        // Acción al aceptar los términos
                        val editor = sharedPref.edit()
                        editor.putBoolean("primeraVez", false)
                        editor.apply()

                        dialog.dismiss()
                    }

                    languageButton.setOnClickListener {
                        showLanguageChangeDialog(dialog, sharedPref)
                    }
                }

                dialog.show()
            }

    }

    // Función para cambiar el idioma
    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources: Resources = requireContext().resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)


        // Retraso para asegurar que el dialog se cierre antes de reiniciar la actividad
        Handler(Looper.getMainLooper()).postDelayed({
            // Reiniciar la actividad para aplicar los cambios
            requireActivity().recreate()
        }, 300)  // El retraso de 300ms es suficiente para evitar el conflicto con el dialog

    }

    // Mostrar diálogo para cambiar el idioma
    private fun showLanguageChangeDialog(dialog: AlertDialog, sharedPreferences: SharedPreferences) {
        val languages = requireContext().resources.getStringArray(R.array.spinner_idiomas)
        val builder = AlertDialog.Builder(requireContext())
        val editor = sharedPreferences.edit()

        builder.setTitle("Selecciona un idioma")
            .setItems(languages) { _, which ->
                when (which) {
                    0 -> {setAppLocale("es")
                        editor.putString("Language", "es")
                    } // Cambiar a español
                    1 -> {setAppLocale("en")
                        editor.putString("Language", "en")
                    } // Cambiar a inglés
                }
                editor.apply()
                // Cerrar el diálogo después de seleccionar el idioma
                dialog.dismiss()
            }
            .show()
    }
}