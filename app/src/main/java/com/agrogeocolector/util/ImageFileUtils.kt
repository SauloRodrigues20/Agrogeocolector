package com.agrogeocolector.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utilitário para gerenciar imagens da CameraX.
 * 
 * REGRA DE OURO: NUNCA salvar BLOB no Room!
 * Sempre comprimir e salvar no filesDir, retornando apenas o path.
 * 
 * Otimizações implementadas:
 * - Compressão JPEG 80%
 * - Redimensionamento para max 1920px
 * - Correção de rotação EXIF
 * - Salvamento no diretório interno (privado)
 */
object ImageFileUtils {
    
    private const val MAX_IMAGE_DIMENSION = 1920
    private const val JPEG_QUALITY = 80
    private const val IMAGE_DIRECTORY = "sample_photos"
    
    /**
     * Processa e salva uma foto da câmera.
     * 
     * @param context Contexto da aplicação
     * @param sourceUri URI da foto original (vinda da CameraX)
     * @return Path absoluto do arquivo comprimido, ou null se falhar
     */
    fun saveAndCompressImage(context: Context, sourceUri: Uri): String? {
        return try {
            // 1. Carrega a imagem como Bitmap
            val originalBitmap = loadBitmapFromUri(context, sourceUri) ?: return null
            
            // 2. Corrige a rotação baseada no EXIF
            val rotatedBitmap = fixImageRotation(context, sourceUri, originalBitmap)
            
            // 3. Redimensiona se necessário
            val resizedBitmap = resizeBitmapIfNeeded(rotatedBitmap, MAX_IMAGE_DIMENSION)
            
            // 4. Salva no filesDir com compressão
            val savedFile = saveBitmapToFile(context, resizedBitmap)
            
            // 5. Limpa memória
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            originalBitmap.recycle()
            resizedBitmap.recycle()
            
            savedFile?.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Carrega um Bitmap a partir de uma URI.
     */
    private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Corrige a rotação da imagem baseada nos dados EXIF.
     * Câmeras de celular frequentemente salvam fotos rotacionadas.
     */
    private fun fixImageRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                    else -> return bitmap
                }
                
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } ?: bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }
    
    /**
     * Redimensiona o Bitmap se alguma dimensão exceder o máximo.
     * Mantém a proporção da imagem.
     */
    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        // Se já está dentro do limite, retorna o original
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }
        
        // Calcula as novas dimensões mantendo aspect ratio
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        
        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Salva o Bitmap processado no diretório interno do app.
     * 
     * @return File salvo ou null se falhar
     */
    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): File? {
        return try {
            // Cria o diretório se não existir
            val directory = File(context.filesDir, IMAGE_DIRECTORY)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            
            // Gera um nome único baseado em timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
            val fileName = "SAMPLE_$timestamp.jpg"
            val file = File(directory, fileName)
            
            // Salva com compressão JPEG 80%
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
                outputStream.flush()
            }
            
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Carrega um Bitmap a partir de um path salvo.
     * Útil para exibir a foto depois de salva.
     */
    fun loadBitmapFromPath(path: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Deleta uma foto do sistema de arquivos.
     * Use quando deletar uma amostra do banco de dados.
     */
    fun deleteImageFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Retorna o tamanho total das fotos salvas em MB.
     * Útil para mostrar estatísticas ao usuário.
     */
    fun getTotalImagesSizeMB(context: Context): Double {
        val directory = File(context.filesDir, IMAGE_DIRECTORY)
        if (!directory.exists()) return 0.0
        
        val totalBytes = directory.walkTopDown()
            .filter { it.isFile }
            .sumOf { it.length() }
        
        return totalBytes / (1024.0 * 1024.0)
    }
    
    /**
     * Limpa todas as fotos (útil para testes ou reset do app).
     */
    fun clearAllImages(context: Context): Boolean {
        return try {
            val directory = File(context.filesDir, IMAGE_DIRECTORY)
            if (directory.exists()) {
                directory.deleteRecursively()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
