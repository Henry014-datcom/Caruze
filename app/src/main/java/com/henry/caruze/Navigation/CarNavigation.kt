data class CarNavParams(
    val name: String = "",
    val price: String = "",
    val details: String = "",
    val category: String = "",
    val sellerName: String = "",
    val sellerPhone: String = "",
    val imageUri: String = ""
) {
    fun toRouteString(): String {
        return listOf(name, price, details, category, sellerName, sellerPhone, imageUri)
            .joinToString("|") { it.encodeToRoute() }
    }

    companion object {
        fun fromRouteString(routeString: String): CarNavParams {
            val parts = routeString.split("|").map { it.decodeFromRoute() }
            return CarNavParams(
                name = parts.getOrElse(0) { "" },
                price = parts.getOrElse(1) { "" },
                details = parts.getOrElse(2) { "" },
                category = parts.getOrElse(3) { "" },
                sellerName = parts.getOrElse(4) { "" },
                sellerPhone = parts.getOrElse(5) { "" },
                imageUri = parts.getOrElse(6) { "" }
            )
        }

        private fun String.encodeToRoute(): String {
            return this.replace("|", "%7C").replace("/", "%2F")
        }

        private fun String.decodeFromRoute(): String {
            return this.replace("%7C", "|").replace("%2F", "/")
        }
    }
}