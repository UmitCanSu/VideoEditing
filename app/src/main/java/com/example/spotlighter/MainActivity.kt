package com.example.spotlighter

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.createBitmap
import androidx.core.view.drawToBitmap
import com.arthenica.mobileffmpeg.Config
import com.example.spotlighter.databinding.ActivityMainBinding

import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.Buffer
import java.nio.ByteBuffer
import java.security.Key
import java.util.*
import java.util.jar.Manifest
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL

import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.ExecuteCallback
import com.arthenica.mobileffmpeg.FFprobe
import com.arthenica.mobileffmpeg.FFmpeg

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLaouncher:ActivityResultLauncher<String>

    private lateinit var cropIntent: Intent

    var selectedVideo :Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        registerLauncher()

        videCrop()
    }

    fun selectedVideo(view: View){
        if(ContextCompat.checkSelfPermission
                (this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission !! "){
                            permissionLaouncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                }else{
                    permissionLaouncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
        }else{
            val intentTOGallery = Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentTOGallery)
        }

    }
    fun uploadVideo(){

    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val intentForResult = it.data
                if(intentForResult != null){
                    selectedVideo= intentForResult.data
                    selectedVideo.let {
                        showAlert(it!!)
                    }
                }
            }
        }


        permissionLaouncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                val intentTOGallery = Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentTOGallery)
            }else{
                Toast.makeText(this,"Permision needed!",Toast.LENGTH_LONG).show()
            }
        }

    }


    private fun showAlert(videoUri: Uri){
        var medioaControler = MediaController(this)
        val view = LayoutInflater.from(this).inflate(R.layout.alert_video_crop, null)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(view)
        val videoPlayer = view.findViewById<VideoView>(R.id.alert_videoPlayer)

        medioaControler.setAnchorView(videoPlayer)
        videoPlayer.setMediaController(medioaControler)


        videoPlayer.setVideoURI(videoUri)
        videoPlayer.setOnPreparedListener {
            videoPlayer.start()
            videoPlayer.pause()
        }

        val playButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.playButton)
        playButton.setOnClickListener {
            if(videoPlayer.isPlaying){
                playButton.text = ">"
                videoPlayer.pause()

            }else{
                playButton.text = "||"
                videoPlayer.start()
            }
        }


        alertDialog.show()
    }

    fun videCrop(){

        var path = "android.resource://" + packageName + "/"+ R.raw.video1
        var newPath = "android.resource://" + packageName + "/ newVideo.mp4"

        val rc = FFmpeg.execute("ffmeg -i"+ path +"-vf scale=1280:720 -preset slow -crf 18" + newPath)


        if (rc == RETURN_CODE_SUCCESS) {

            Log.e("S->","Command execution completed successfully.")
        }
        else if (rc == RETURN_CODE_CANCEL) {

            Log.e("S->","Command execution cancelled by user")
        }
        else {

            Log.e("S->", String.format("Command execution failed with rc=%d and the output below.", rc));
            Log.e("S->","Hata")
        }









    }





}