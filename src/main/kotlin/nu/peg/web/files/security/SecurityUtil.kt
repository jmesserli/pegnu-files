package nu.peg.web.files.security

import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtil {
    object Role {
        const val SEE_HIDDEN = "PegNu-Files.SEE-HIDDEN"
        const val MAKE_DIRECTORY = "PegNu-Files.MAKE-DIRECTORY"
        const val DELETE = "PegNu-Files.DELETE"
        const val UPLOAD = "PegNu-Files.UPLOAD"
        const val ADMIN = "PegNu-Files.ADMIN"
    }

    fun isLoggedIn(): Boolean {
        val auth = SecurityContextHolder.getContext().authentication

        return auth != null && auth.isAuthenticated && !auth.authorities.any { it.authority == "ROLE_ANONYMOUS" }
    }

    fun hasRole(role: String): Boolean {
        if (!isLoggedIn())
            return false

        return SecurityContextHolder.getContext().authentication.authorities.any { it.authority == role }
    }

    fun canSeeHidden() = hasRole(Role.SEE_HIDDEN)
    fun canUpload() = hasRole(Role.UPLOAD)
    fun canMakeDirectory() = hasRole(Role.MAKE_DIRECTORY)
    fun canDelete() = hasRole(Role.DELETE)
    fun isAdmin() = hasRole(Role.ADMIN)
}