package at.randonneurs.network

object ApiConfig {
    // TODO: Deine VM-IP + Port eintragen, z. B. "http://10.211.55.3:5000/"
    const val BASE_URL = "http://10.211.55.3:5000/"

    object Paths {
        const val ACCOUNTS_LOGIN   = "Accounts/Login"
        const val ACCOUNTS_PROFILE = "Accounts/profile"
        const val ACCOUNTS_REGISTER = "Accounts/Register" // <— NEU
    }
}
