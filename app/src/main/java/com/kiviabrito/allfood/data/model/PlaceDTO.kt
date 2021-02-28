package com.kiviabrito.allfood.data.model

import com.google.gson.annotations.SerializedName
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.safeLet

data class PlaceDTO(
    @SerializedName("place_id")
    var id: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("geometry")
    var geometry: GeometryDTO?,
    @SerializedName("icon")
    var imageURL: String?,
    @SerializedName("rating")
    var rating: Double?,
    @SerializedName("user_ratings_total")
    var ratingTotal: Int?,
    @SerializedName("business_status")
    var businessStatus: String?,
    @SerializedName("price_level")
    var priceLevel: Int?,
    @SerializedName("opening_hours")
    var openHours: OpenHoursDTO?
) {

    companion object {
        const val UNAVAILABLE_HOURS = " · Unavailable hours"
        const val DOT = " · "
        const val TWO_DECIMAL_FORMAT = "%.2f"
        const val MONETARY = "$"
    }

    fun getPriceLevelAndOpenHour(): String {
        return when (businessStatusEnum) {
            BusinessStatusEnum.OPERATIONAL -> {
                safeLet(businessStatusEnum, openHours?.openNow) { statusEnum, isOpen ->
                    if (isOpen) {
                        getPriceLevel() + DOT + statusEnum.openLabel
                    } else {
                        getPriceLevel() + DOT + statusEnum.closeLabel
                    }
                } ?: getPriceLevel() + UNAVAILABLE_HOURS
            }
            BusinessStatusEnum.CLOSED_TEMPORARILY -> {
                businessStatusEnum?.let { statusEnum ->
                    getPriceLevel() + DOT + statusEnum.closeLabel
                } ?: getPriceLevel() + UNAVAILABLE_HOURS
            }
            else -> getPriceLevel() + UNAVAILABLE_HOURS
        }
    }

    private fun getPriceLevel(): String {
        var priceLevel = ""
        for (i in priceLevel + 1) {
            priceLevel += MONETARY
        }
        return priceLevel
    }

    private val businessStatusEnum: BusinessStatusEnum?
        get() = BusinessStatusEnum.fromValue(businessStatus)

    enum class BusinessStatusEnum(val value: String, val openLabel: String, val closeLabel: String) {
        OPERATIONAL("OPERATIONAL", "Open now", "Closed"),
        CLOSED_TEMPORARILY("CLOSED_TEMPORARILY", "Closed temporarily", "Closed temporarily");

        companion object {
            private val map = values().associateBy(BusinessStatusEnum::value)
            fun fromValue(value: String?) = value?.let { map[it] }
        }
    }

    var isFavorite: Boolean = false
    var distanceToUserKm: Double? = null
    fun getDistanceToUserStringKm() = TWO_DECIMAL_FORMAT.format(distanceToUserKm)

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false

        other as PlaceDTO

        if (id != other.id) return false
        if (geometry != other.geometry) return false
        if (name != other.name) return false
        if (imageURL != other.imageURL) return false
        if (rating != other.rating) return false
        if (ratingTotal != other.ratingTotal) return false
        if (businessStatus != other.businessStatus) return false
        if (priceLevel != other.priceLevel) return false
        if (openHours != other.openHours) return false
        if (isFavorite != other.isFavorite) return false
        if (distanceToUserKm != other.distanceToUserKm) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (geometry?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (imageURL?.hashCode() ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + (ratingTotal ?: 0)
        result = 31 * result + (businessStatus?.hashCode() ?: 0)
        result = 31 * result + (priceLevel ?: 0)
        result = 31 * result + (openHours?.hashCode() ?: 0)
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + (distanceToUserKm?.hashCode() ?: 0)
        return result
    }

    fun copy() = PlaceDTO(
        id = id,
        geometry = geometry,
        name = name,
        imageURL = imageURL,
        rating = rating,
        ratingTotal = ratingTotal,
        businessStatus = businessStatus,
        priceLevel = priceLevel,
        openHours = openHours
    )

}
