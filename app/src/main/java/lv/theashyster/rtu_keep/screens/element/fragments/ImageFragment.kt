package lv.theashyster.rtu_keep.screens.element.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.EXTRA_OUTPUT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.fragment_image.*
import lv.theashyster.rtu_keep.repositories.NoteItem
import lv.theashyster.rtu_keep.R
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.IMAGE_NOTE
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageFragment : BaseFragment() {

    private var imageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item?.let {
            imageUri = it.uri?.toUri()
            imageImageView.setImageURI(imageUri)
        }

        imageCameraButton.setOnClickListener { takeImage() }
        imageSaveButton.setOnClickListener { saveImage() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageImageView.setImageURI(imageUri)
        }
    }

    private fun takeImage() {
        val intent = Intent(ACTION_IMAGE_CAPTURE)

        intent.resolveActivity(fragmentActivity.packageManager)?.let {
            val imageFile = try {
                createImageFile()
            } catch (e: IOException) {
                null
            }

            imageFile?.let {
                imageUri = FileProvider.getUriForFile(
                    fragmentActivity,
                    "lv.theashyster.rtu_keep.fileprovider",
                    it
                )

                intent.putExtra(EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val directory = fragmentActivity.getExternalFilesDir(DIRECTORY_PICTURES)

        return File.createTempFile("JPEG_${timestamp}_", ".jpg", directory)
    }

    private fun saveImage() {
        if (imageUri == null) {
            Toast.makeText(fragmentContext, getString(R.string.image_picture_empty_text), LENGTH_SHORT).show()
            return
        }

        val noteItem = NoteItem(
            type = IMAGE_NOTE,
            uri = imageUri.toString(),
            color = randomColor
        )

        save(noteItem)
    }

    companion object {

        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}
