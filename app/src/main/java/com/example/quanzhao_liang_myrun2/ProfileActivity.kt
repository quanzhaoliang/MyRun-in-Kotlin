package com.example.quanzhao_liang_myrun2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var snapButton: Button
    private lateinit var saveButton: Button
    private lateinit var imageView: ImageView
    private val tempImgName = "temp_img.jpg"
    private val perImgName = "per_img.jpg"
    private lateinit var tempImgUri: Uri
    private lateinit var perImgUri: Uri
    private lateinit var launchCam: ActivityResultLauncher<Intent>
    private lateinit var launchGallery: ActivityResultLauncher<Intent>
    private lateinit var myViewModel: MyViewModel
    private lateinit var sharePref: SharedPreferences
    private lateinit var fnText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneNum: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton
    private lateinit var classText: TextView
    private lateinit var majorText: TextView
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        snapButton = findViewById(R.id.btn_photo)
        saveButton = findViewById(R.id.save_btn)
        cancelButton = findViewById(R.id.cancel_btn)
        imageView = findViewById(R.id.image)
        sharePref = getSharedPreferences("shareP", Context.MODE_PRIVATE)
        fnText = findViewById(R.id.et_fn)
        emailText = findViewById(R.id.et_email)
        phoneNum = findViewById(R.id.et_phone)
        radioGroup = findViewById(R.id.radioGrp)
        radioMale = findViewById(R.id.radioM)
        radioFemale = findViewById(R.id.radioF)
        classText = findViewById(R.id.et_class)
        majorText = findViewById(R.id.et_major)

        //Reload the Save Image
        val perImg = File(getExternalFilesDir(null), perImgName)
        perImgUri = FileProvider.getUriForFile(this, "com.qz.MyRun2", perImg)
        if (perImg.exists()){
            val bitmap = Util.getBitmap(this, perImgUri)
            imageView.setImageBitmap(bitmap)
        }
        //Reload the String Fields and the Select Options
        fnText.setText(sharePref.getString("firstName", ""), TextView.BufferType.EDITABLE)
        emailText.setText(sharePref.getString("lastName", ""), TextView.BufferType.EDITABLE)
        phoneNum.setText(sharePref.getString("phoneNumber", ""), TextView.BufferType.EDITABLE)
        val selectedOptionId = sharePref.getInt("selectedOptionId", -1)
        if (selectedOptionId != -1){
            radioGroup.check(selectedOptionId)
        }
        classText.setText(sharePref.getString("class", ""), TextView.BufferType.EDITABLE)
        majorText.setText(sharePref.getString("major", ""), TextView.BufferType.EDITABLE)


        /**
        Description: Code from cameraDemo in CMPT 362
        Author: Xingdong Yang
        https://canvas.sfu.ca/courses/80625/pages/schedule
         */
        snapButton.setOnClickListener(){
            Util.checkPermissions(this)
            showTwoButtonDialog()
        }
        //Setup the Image Capture
        var tempImg = File(getExternalFilesDir(null), tempImgName)
        tempImgUri = FileProvider.getUriForFile(this, "com.qz.MyRun2", tempImg)

        launchCam = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: androidx.activity.result.ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val bitmap = Util.getBitmap(this, tempImgUri)
                myViewModel.userImage.value = bitmap

            }
        }
        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.userImage.observe(this, {
            imageView.setImageBitmap(it)
        })


        //Handle user pick photo from gallery
        launchGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: androidx.activity.result.ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val selectedUri = result.data?.data!!
                imageView.setImageURI(selectedUri)
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(selectedUri, projection, null, null, null)
                if (cursor != null) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    val filePath = cursor.getString(columnIndex)
                    val imgFile = File(filePath)
                    imgFile.copyTo(tempImg, overwrite = true)
                    cursor.close()
                }
            }
        }



        saveButton.setOnClickListener(){
            //Update the Permanent Image to the image just capture
            if (tempImg.exists()){
                tempImg.copyTo(perImg, overwrite = true)
            }
            //Create the sharePref to save the data as String
            val editor = sharePref.edit()

            val perUriString = perImgUri.toString()
            val fn = fnText.text.toString()
            val email = emailText.text.toString()
            val phone = phoneNum.text.toString()
            editor.clear()
            editor.putString("imgUri", perUriString)
            editor.putString("firstName", fn)
            editor.putString("lastName", email)
            editor.putString("phoneNumber", phone)

            val selectedId = radioGroup.checkedRadioButtonId
            editor.putInt("selectedOptionId", selectedId)

            val className = classText.text.toString()
            val major = majorText.text.toString()
            editor.putString("class", className)
            editor.putString("major", major)
            editor.apply()
            finish()
        }

        cancelButton.setOnClickListener(){
            finish()
        }
    }

    private fun showTwoButtonDialog() {
        val customView = layoutInflater.inflate(R.layout.dialog_buttons, null)
        val openCameraButton = customView.findViewById<Button>(R.id.openCameraButton)
        val selectFromGalleryButton = customView.findViewById<Button>(R.id.selectFromGalleryButton)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Pick Profile Picture")
            .setView(customView)
            .create()

        openCameraButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
            launchCam.launch(intent)
            alertDialog.dismiss()
        }

        selectFromGalleryButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            launchGallery.launch(intent)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}