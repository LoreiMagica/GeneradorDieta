package com.lorei.generadorDieta.ui.fragment

import DietaViewModel
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.OpcionesCopiaLocalLayoutBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class OpcionesCopiaLocalFragment : Fragment() {
    private lateinit var binding: OpcionesCopiaLocalLayoutBinding
    private lateinit var viewModel: DietaViewModel
    val PERMISSION_CODE = 101
    private val REQUEST_CODE_RESTORE = 200


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OpcionesCopiaLocalLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DietaViewModel::class.java]

        //Damos función al botón de generar dieta
        binding.optionHacerCopia.setOnClickListener {
            //Comprobamos la versión de android para saber si pedir permisos o no
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backupDatabase()
            } else {
                //chequeamos los permisos
                if (checkPermissions()) {
                    backupDatabase()

                } else {
                    //Pedimos los permisos
                    requestPermission()
                }
            }
        }

        binding.optionRestaurarCopia.setOnClickListener {
            //Notificamos al usuario con un dialog
            val ostiaRestaura = AlertDialog.Builder(context)
            ostiaRestaura.setTitle(requireContext().getString(R.string.aviso_restaurar))
            ostiaRestaura.setMessage(R.string.aviso_texto_restaurar)

            ostiaRestaura.setPositiveButton(R.string.si) { dialog, which ->
                val backupDir = File(requireContext().getExternalFilesDir(null), "DatabaseBackups")
                if (!backupDir.exists()) {
                    backupDir.mkdirs() // Crea el directorio si no existe
                }
                // Crea un archivo temporal para generar una URI válida
                val sampleFile = File(backupDir, "dummy.txt")
                if (!sampleFile.exists()) {
                    sampleFile.createNewFile()
                }
                val backupUri = FileProvider.getUriForFile(requireActivity(), "com.lorei.generadorDieta.fileprovider", sampleFile)

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, backupUri) // Establece la carpeta inicial
                }
                startActivityForResult(Intent.createChooser(intent, "Selecciona una copia de seguridad"), REQUEST_CODE_RESTORE)
            }

            ostiaRestaura.setNegativeButton(R.string.no) { dialog, which ->
                Toast.makeText(context,
                    R.string.cancelar, Toast.LENGTH_SHORT).show()
            }
            ostiaRestaura.show()

        }


        return binding.root
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
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // on below line we are creating a variable
        // for reading external storage permission
        var readStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
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
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_CODE
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
                    == PackageManager.PERMISSION_GRANTED
                ) {

                    // if permissions are granted we are displaying a toast message.
                    Toast.makeText(requireActivity(), "Permission Granted..", Toast.LENGTH_SHORT)
                        .show()

                } else {

                    // if permissions are not granted we are
                    // displaying a toast message as permission denied.
                    Toast.makeText(requireActivity(), "Permission Denied..", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

  /*  private fun backupDatabase() {
        try {
            // Ruta de la base de datos original
            val dbPath = requireActivity().getDatabasePath("baseGuardado.db").absolutePath

            // Crear una carpeta para almacenar la copia de seguridad
            val backupDir = File(requireContext().getExternalFilesDir(null), "DatabaseBackups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            val sdf = SimpleDateFormat("dd-M-yyyy hh.mm.ss")
            val currentDate = sdf.format(Date())
            // Crear el archivo de copia de seguridad
            val backupFile = File(backupDir, "backup_DietApp_Calendario_$currentDate.db")

            // Copiar la base de datos al archivo de copia de seguridad
            File(dbPath).copyTo(backupFile, overwrite = true)

            //Notificamos al usuario con un dialog
            val ostiaBackup = AlertDialog.Builder(context)
            ostiaBackup.setTitle(requireContext().getString(R.string.backup_exito_titulo))
            ostiaBackup.setMessage("${R.string.backup_exito}${backupFile.absolutePath}")

            ostiaBackup.setPositiveButton(R.string.ok) { dialog, which ->

            }
            ostiaBackup.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al crear la copia de seguridad", Toast.LENGTH_SHORT).show()
        }
    }

   */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RESTORE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                restoreDatabase(uri)
            }
        }
    }


    private fun backupDatabase() {
        try {
            // Ruta de la base de datos original
            val dbPath = requireActivity().getDatabasePath("baseGuardado.db").absolutePath

            // Obtener la fecha actual para el nombre del archivo
            val sdf = SimpleDateFormat("dd-MM-yyyy_HH.mm.ss", Locale.getDefault())
            val currentDate = sdf.format(Date())
            val backupFileName = "backup_DietApp_Calendario_$currentDate.db"
            var realpath = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Usar MediaStore para guardar en Documentos accesibles para el usuario
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, backupFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/DietApp_Calendario")
                }

                val contentResolver = requireContext().contentResolver
                val uri = requireActivity().contentResolver.insert(
                    MediaStore.Files.getContentUri("external"),
                    contentValues
                )
                uri?.let {
                    contentResolver.openOutputStream(it).use { outputStream ->
                        File(dbPath).inputStream().copyTo(outputStream!!)
                    }
                    requireContext().contentResolver.query(it, arrayOf(MediaStore.Images.Media.DATA), null, null, null)?.use { cursor ->
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        if (cursor.moveToFirst()) {
                            realpath = cursor.getString(columnIndex)
                        }
                    }
                }
            } else {
                // Para versiones anteriores a Android Q
                val backupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "DietApp_Calendario")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }
                val backupFile = File(backupDir, backupFileName)
                realpath = "$backupDir/$backupFileName"

                // Copiar la base de datos al archivo de copia de seguridad
                File(dbPath).copyTo(backupFile, overwrite = true)
            }

            // Notificar al usuario con un AlertDialog
            val alertBackup = AlertDialog.Builder(context)
            alertBackup.setTitle(requireContext().getString(R.string.backup_exito_titulo))
            alertBackup.setMessage("${getString(R.string.backup_exito)} $realpath")

            alertBackup.setPositiveButton(R.string.ok) { dialog, _ -> }

            alertBackup.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al crear la copia de seguridad", Toast.LENGTH_SHORT).show()
        }
    }


    private fun restoreDatabase(uri: Uri) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
        try {
            // Verificar que el archivo tenga la extensión .db y sea una base de datos válida
            if (!isDatabaseFile(uri)) {
                Toast.makeText(requireContext(), "El archivo no es una base de datos .db válida", Toast.LENGTH_SHORT).show()
                return
            }

            if (!isValidDatabaseFile(uri)) {
                Toast.makeText(requireContext(), "El archivo no es una base de datos SQLite válida", Toast.LENGTH_SHORT).show()
                return
            }
            // Ruta de la base de datos original
            val dbPath = requireContext().getDatabasePath("baseGuardado.db")
            if (dbPath.exists()) {
                dbPath.delete()  // Eliminar la base de datos antes de restaurar
            }
            requireContext().deleteDatabase("baseGuardado.db")




            // Abrir el InputStream del archivo seleccionado
            val inputStream = requireContext().contentResolver.openInputStream(uri)

            // Sobrescribir la base de datos existente con el archivo seleccionado
            inputStream?.use { input ->
                dbPath.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Forzar que la base de datos se recargue
            SQLiteDatabase.openOrCreateDatabase(dbPath, null).close()
            Toast.makeText(requireContext(), "Base de datos restaurada con éxito", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al restaurar la base de datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDatabaseFile(uri: Uri): Boolean {
        val fileName = getFileName(uri)
        return fileName?.endsWith(".db") == true
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null

        // Si la URI es un archivo en el almacenamiento local (como external storage o cache)
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            fileName = File(uri.path).name
        } else {
            // Si no, consulta el proveedor de contenido
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                // Verifica si DISPLAY_NAME está disponible
                if (displayNameIndex != -1 && it.moveToFirst()) {
                    fileName = it.getString(displayNameIndex)
                }
            }
            cursor?.close()
        }
        return fileName

    }

    private fun isValidDatabaseFile(uri: Uri): Boolean {
        try {
            // Intentar abrir el archivo como una base de datos SQLite
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempDbFile = File(requireContext().cacheDir, "temp_restore.db")
            inputStream?.use { input ->
                tempDbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Intentar abrir la base de datos en SQLite
            val db = SQLiteDatabase.openOrCreateDatabase(tempDbFile, null)
            db.close()

            // Si no hubo excepción, el archivo es un SQLite válido
            tempDbFile.delete() // Eliminar el archivo temporal
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}