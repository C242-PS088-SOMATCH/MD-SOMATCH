package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ScannerUtils {

    fun compressImage(context: Context, uri: Uri, maxFileSize: Long = 1_000_000): Uri? {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            var compressQuality = 100
            var streamLength: Int
            val compressedFile = File(context.cacheDir, "compressed_image.jpg")
            do {
                val outputStream = FileOutputStream(compressedFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
                outputStream.flush()
                outputStream.close()

                streamLength = compressedFile.length().toInt()
                compressQuality -= 5
            } while (streamLength > maxFileSize && compressQuality > 5)

            Log.d("ScannerUtils", "Image compressed to size: $streamLength bytes")
            return Uri.fromFile(compressedFile)
        } catch (e: Exception) {
            Log.e("ScannerUtils", "Image compression failed", e)
        }
        return null
    }
}