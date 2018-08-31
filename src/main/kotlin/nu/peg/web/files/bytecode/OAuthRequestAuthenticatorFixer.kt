package nu.peg.web.files.bytecode

import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.pool.TypePool

object OAuthRequestAuthenticatorFixer {
    init {
        val classLoader = OAuthRequestAuthenticatorFixer::class.java.classLoader
        val classLocator = ClassFileLocator.ForClassLoader.of(classLoader)
        val pool = TypePool.Default.of(classLoader)

        val authenticatorDesc = pool.describe("org.keycloak.adapters.OAuthRequestAuthenticator").resolve()

        ByteBuddy().rebase<Any>(authenticatorDesc, classLocator)
                .method(ElementMatchers.nameMatches("^(?:getRequestUrl|stripOauthParametersFromRedirect)$"))
                .intercept(FixedValue.value("https://files.peg.nu/sso/login"))
                .make().load(classLoader, ClassLoadingStrategy.Default.INJECTION)
    }

    fun ensureExecution() {
        print("Fixed OAuthRequestAuthenticator")
    }
}