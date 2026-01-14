package ua.com.goit.clearbreath.analysis.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {

    private const val COST = 12

    fun hashPassword(plainPassword: String): String {
        require(plainPassword.isNotBlank()) { "Password must not be blank" }
        val salt = BCrypt.gensalt(COST)
        return BCrypt.hashpw(plainPassword, salt)
    }

    fun verifyPassword(plainPassword: String, passwordHash: String): Boolean {
        if (plainPassword.isBlank() || passwordHash.isBlank()) return false

        return try {
            BCrypt.checkpw(plainPassword, passwordHash)
        } catch (_: IllegalArgumentException) {
            false
        }
    }
}
