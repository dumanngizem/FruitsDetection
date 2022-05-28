package com.example.cekipal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.cekipal.databinding.ActivityMainBinding
import java.io.File

lateinit var bitmap : Bitmap

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    var currentPath: String? = null
    private lateinit var photoUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()
        initializeEvents()
    }

    private fun initializeViews(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detectBtn.visibility = View.GONE
    }

    private fun initializeEvents(){
        binding.cameraBtn.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                cameraFun()
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),2)
            }
        }

        binding.galleryBtn.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED)
            {
                galleryFun()
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }
        }

        binding.detectBtn.setOnClickListener {
            val intent = Intent(this, DetectionScreen::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (gr in grantResults)
        {
            if (gr != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "İzinlerin tümü verilmedi.", Toast.LENGTH_LONG).show()
                return
            }
        }

        if(requestCode == 1) galleryFun() else cameraFun()

    }

    private fun cameraFun()
    {
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {
            createImageFile()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intent,0)
        }
    }

    private fun galleryFun()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,2)
    }

    private fun createImageFile()
    {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("photo", ".jpg" ,storageDir)
        currentPath = imageFile.absolutePath
        photoUri = FileProvider.getUriForFile(this, packageName, imageFile)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == 0)
        {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = 1
            bmOptions.inPurgeable = true
            bitmap = BitmapFactory.decodeFile(currentPath, bmOptions)
            bitmap = rotateBitmap()!!
            binding.photo.setImageBitmap(bitmap)

            binding.detectBtn.visibility = View.VISIBLE
            binding.camGalLinearLayout.visibility = View.GONE

        }
        else if(resultCode == Activity.RESULT_OK && requestCode == 2)
        {
            photoUri = data?.data!!
            binding.photo.setImageURI(photoUri)
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,photoUri)

            binding.detectBtn.visibility = View.VISIBLE
            binding.camGalLinearLayout.visibility = View.GONE
        }
    }

    private fun rotateBitmap() : Bitmap?{
        val orientation: Int = ExifInterface(currentPath!!).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        var rotatedBitmap: Bitmap? = null
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
            else -> rotatedBitmap = bitmap
        }
        return rotatedBitmap
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }


}

