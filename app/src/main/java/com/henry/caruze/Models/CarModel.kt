data class CarData(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val details: String = "",
    val category: String = "",
    val sellerName: String = "",
    val sellerPhone: String = "",
    val imageUrl: String = "", // Keep consistent naming
    val userId: String = ""
) {
    // Convert to navigation parameters
    fun toNavParams(): CarNavParams {
        return CarNavParams(
            name = this.name,
            price = this.price,
            details = this.details,
            category = this.category,
            sellerName = this.sellerName,
            sellerPhone = this.sellerPhone,
            imageUri = this.imageUrl
        )
    }

    // Create from navigation parameters
    companion object {
        fun fromNavParams(navParams: CarNavParams, id: String = "", userId: String = ""): CarData {
            return CarData(
                id = id,
                name = navParams.name,
                price = navParams.price,
                details = navParams.details,
                category = navParams.category,
                sellerName = navParams.sellerName,
                sellerPhone = navParams.sellerPhone,
                imageUrl = navParams.imageUri,
                userId = userId
            )
        }
    }
}