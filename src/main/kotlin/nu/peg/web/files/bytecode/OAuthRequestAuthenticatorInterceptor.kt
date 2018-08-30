package nu.peg.web.files.bytecode

object OAuthRequestAuthenticatorInterceptor {
    @JvmStatic
    fun getRequestUrl(): String {
        return "https://files.peg.nu/sso/login"
    }
}