package net.odiak.paint

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        private val REQUEST_CODE = 10
    }

    private lateinit var rootView: View
    private lateinit var drawView: DrawView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        rootView = findViewById(android.R.id.content)!!
        drawView = findViewById(R.id.drawView) as DrawView
        val clearButton = findViewById(R.id.clearButton)!!
        val saveButton = findViewById(R.id.saveButton)!!

        clearButton.setOnClickListener {
            drawView.clear()
        }

        saveButton.setOnClickListener {
            if (checkPermission()) {
                saveImage(drawView)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (permissions.zip(grantResults.toTypedArray()).any { it.first == Manifest.permission.WRITE_EXTERNAL_STORAGE && it.second == PackageManager.PERMISSION_GRANTED }) {
                saveImage(drawView)
            } else {
                Snackbar.make(rootView, "No permission to access storage", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
            return false
        }

        if (packageManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(rootView, "No permission to access storage", Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveImage(drawView: DrawView) {
        try {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(path, "${System.currentTimeMillis()}.png")

            path.mkdirs()

            val os = FileOutputStream(file)
            drawView.saveTo(os)
            os.close()

            MediaScannerConnection.scanFile(this,
                    arrayOf(file.toString()),
                    null,
                    { path, uri ->
                        println("@ path: $path, uri: $uri")
                    })
        } catch (e: IOException) {
            Snackbar.make(rootView, "Failed to save", Snackbar.LENGTH_SHORT).show()
            return
        }

        Snackbar.make(rootView, "Saved", Snackbar.LENGTH_SHORT).show()
    }
}
