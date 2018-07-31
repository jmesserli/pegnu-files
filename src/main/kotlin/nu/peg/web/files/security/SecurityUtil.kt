package nu.peg.web.files.security

import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtil {
    fun isLoggedIn(): Boolean {
        val auth = SecurityContextHolder.getContext().authentication

        return auth != null && auth.isAuthenticated && !auth.authorities.any { it.authority == "ROLE_ANONYMOUS" }
    }
}