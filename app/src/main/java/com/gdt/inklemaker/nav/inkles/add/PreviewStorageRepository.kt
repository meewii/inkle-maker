package com.gdt.inklemaker.nav.inkles.add

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.view.View
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to write and read from Internal/External storage to store the Preview image bitmap of the Inkle
 */
@Singleton
class PreviewStorageRepository @Inject constructor(private val context: Context) {

  private var directoryName = "images"
  private var fileExtension = ".png"

  // Checks if a volume containing external storage is available for read and write.
  private val isExternalStorageWritable: Boolean
    get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

  /**
   * Creates a bitmap from the supplied view.
   *
   * @param view The view to get the bitmap.
   * @param width The width for the bitmap.
   * @param height The height for the bitmap.
   *
   * @return The bitmap from the supplied View.
   */
  fun createBitmapFromView(view: View, width: Float, height: Float): BitmapOptional {
    if (width > 0 && height > 0) {
      view.measure(
        View.MeasureSpec.makeMeasureSpec(width.toInt(), View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height.toInt(), View.MeasureSpec.EXACTLY)
      )
    }

    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.background?.draw(canvas)
    view.draw(canvas)

    return BitmapOptional(bitmap)
  }

  /**
   * Save given Bitmap under given name in the external storage if available, or in internal storage otherwise
   */
  fun write(name: String, bitmap: Bitmap): Status {
    var status: Status

    val fileName = "$name$fileExtension"

    try {
      val file = File(createFile(), fileName)
      val out = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

      status = Status.SuccessWriting(file.path)

      out.flush()
      out.close()
    } catch (e: Exception) {
      Timber.e(e, "Error while saving bitmap in storage")
      val message = when (e) {
        is FileNotFoundException -> "File not found"
        is IOException -> "I/O error occurs"
        is NullPointerException -> "File not created"
        else -> "Unknown error"
      }
      status = Status.Error(message, e)
    }

    return status
  }

  private fun createFile(): File? {
    return if (isExternalStorageWritable) {
      Timber.d("Writing in External storage")
      context.getExternalFilesDir(DIRECTORY_PICTURES)
    } else {
      Timber.d("Writing in Internal storage")
      context.getDir(directoryName, Context.MODE_PRIVATE)
    }
  }

  /**
   * Find the Bitmap stored under given path or return Status.Error if it doesn't exist
   */
  fun read(path: String?): Status {
    var status: Status

    if (path == null) return Status.Error("Cannot create bitmap from null path")

    try {
      File(path).apply {
        val inputStream = FileInputStream(this)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        status = if (bitmap != null) Status.SuccessReading(bitmap)
        else Status.Error("The bitmap couldn't be found with given path '$path'")

        inputStream.close()
      }
    } catch (e: Exception) {
      val message = when (e) {
        is FileNotFoundException -> "File not found"
        is IOException -> "I/O error occurs"
        is NullPointerException -> "File not created"
        else -> "Unknown error"
      }
      status = Status.Error(message, e)
    }
    return status
  }

  data class BitmapOptional(val value: Bitmap?)

  sealed class Status {
    data class SuccessWriting(val path: String) : Status()
    data class SuccessReading(val bitmap: Bitmap) : Status()
    data class Error(val message: String? = null, val throwable: Throwable? = null) : Status()
  }
}


