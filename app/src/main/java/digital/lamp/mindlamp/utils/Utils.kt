package digital.lamp.mindlamp.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import java.io.UnsupportedEncodingException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object Utils {
    fun toBase64(message: String): String? {
        val data: ByteArray
        try {
            data = message.toByteArray(charset("UTF-8"))
            return Base64.encodeToString(data, Base64.DEFAULT)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    fun getApplicationName(context: Context): String {
        return context.applicationInfo.loadLabel(context.packageManager).toString()
    }


    /***
     *
     * @param data
     * @param key
     * @param iv
     * @return
     * encrypt using AES
     */
    fun AESEncrypt(
        data: String,
        key: String,
        iv: String
    ): String? {
        return try {
            val mAlgorithmParameterSpec: AlgorithmParameterSpec =
                IvParameterSpec(iv.toByteArray())
            val mSecretKeySpec =
                SecretKeySpec(key.toByteArray(), "AES")
            val mCipher =
                Cipher.getInstance("AES/CBC/PKCS7Padding")
            mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec)
            Base64.encodeToString(
                mCipher.doFinal(data.toByteArray()),
                Base64.DEFAULT
            )
        } catch (ex: Exception) {
            null
        }
    }

    /***
     *
     * @param data
     * @param key
     * @param iv
     * @return
     *
     * Decrypt AES
     */
    fun AESDecrypt(
        data: String?,
        key: String,
        iv: String
    ): String? {
        val decryptData =
            Base64.decode(data, Base64.DEFAULT)
        return try {
            val mAlgorithmParameterSpec: AlgorithmParameterSpec =
                IvParameterSpec(iv.toByteArray())
            val mSecretKeySpec =
                SecretKeySpec(key.toByteArray(), "AES")
            val mCipher =
                Cipher.getInstance("AES/CBC/PKCS7Padding")
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec)
            String(mCipher.doFinal(decryptData))
        } catch (ex: java.lang.Exception) {
            null
        }
    }

}