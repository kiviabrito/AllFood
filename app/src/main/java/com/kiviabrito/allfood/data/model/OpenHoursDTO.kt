package com.kiviabrito.allfood.data.model

import com.google.gson.annotations.SerializedName

data class OpenHoursDTO(
    @SerializedName("open_now")
    var openNow : Boolean?
)
