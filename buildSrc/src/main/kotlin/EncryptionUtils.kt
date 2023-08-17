/**
 * Created by ABDELMAJID ID ALI on 4/4/21.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


@Suppress("unused")
object EncryptionUtils {

    private val password = "7797F52B25B94EBFBB117731E88ADBA9".toByteArray()

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generateKey(): SecretKey {
//        val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
//        val parameters = IvParameterSpec(password.copyOf(16))
//        keyGenerator.init(parameters) // 192 and 256 bits may not be available
//        return keyGenerator.generateKey()
        return SecretKeySpec(password, "AES")
    }


    fun encryptMsg(message: ByteArray, secretKey: SecretKey = generateKey()): ByteArray {
        val encryptCipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val parameters = IvParameterSpec(password.copyOf(16))
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, parameters)
        return encryptCipher.doFinal(message)
    }

    fun decryptMsg(cipherText: ByteArray, secretKey: SecretKey = generateKey()): ByteArray {
        val decryptCipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val parameters = IvParameterSpec(password.copyOf(16))
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, parameters)
        return decryptCipher.doFinal(cipherText)
    }


    fun encryptString(text: String): String {
        val key = generateKey()
        val encryptMsg = encryptMsg(text.toByteArray(), key)
        // Base64.decode(encryptMsg,Base64.DEFAULT)
        //return String(encryptMsg,Charsets.UTF_8)
        return Base64.getEncoder().encodeToString(encryptMsg)
    }

    fun decryptString(cipherText: String): String {
        val key = generateKey()

        return String(
            decryptMsg(
                // Base64.decode(cipherText.toByteArray(), Base64.DEFAULT), key
                Base64.getDecoder().decode(cipherText.toByteArray()), key
            )
        )
    }

    fun String.encrypt(): String {
        return encryptString(this)
    }

    fun String.decrypt(): String {
        return decryptString(this)
    }

}