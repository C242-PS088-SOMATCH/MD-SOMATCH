package com.example.somatchapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

fun compressImage(context: Context, imageUri: Uri, maxSizeInMB: Int = 1): File? {
    // Maximum size in byte
    val maxSizeInBytes = maxSizeInMB * 1024 * 1024

    val inputStream = context.contentResolver.openInputStream(imageUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    // Compress the bitmap
    var compressedFile: File? = null
    var quality = 100
    do {
        val outputStream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        outputStream.close()

        if (byteArray.size <= maxSizeInBytes || quality <= 10) {
            // Save compressed bitmap to file
            compressedFile = File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
            FileOutputStream(compressedFile).use { fos ->
                fos.write(byteArray)
                fos.flush()
            }
            break
        }
        quality -= 10
    } while (quality > 0)

    return compressedFile
}