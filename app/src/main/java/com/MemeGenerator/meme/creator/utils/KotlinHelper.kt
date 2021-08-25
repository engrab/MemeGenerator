package com.MemeGenerator.meme.creator.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.MemeGenerator.meme.creator.models.InternalStoragePhoto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.function.BiConsumer
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class KotlinHelper(private val context: Context) {

    companion object {
        suspend fun loadPhotosFromInternalStorage(filesDir: File): List<InternalStoragePhoto> {
            return withContext(Dispatchers.IO) {
                val files = filesDir.listFiles()
                files?.filter {
                    it.canRead() && it.isFile && it.name.endsWith(".jpg")
                }?.map {
                    val bytes = it.readBytes()
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    InternalStoragePhoto(it.name, bmp)
                }?.asReversed() ?: listOf()
            }
        }
    }

    fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            context.openFileOutput("$filename.jpg", AppCompatActivity.MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    @JvmOverloads
    fun <R> getContinuation(
        onFinished: BiConsumer<R?, Throwable?>,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ): Continuation<R> {
        return object : Continuation<R> {
            override val context: CoroutineContext
                get() = dispatcher

            override fun resumeWith(result: Result<R>) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onFinished.accept(result.getOrNull(), result.exceptionOrNull())
                }
            }
        }
    }
}