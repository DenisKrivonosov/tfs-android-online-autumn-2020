package ru.krivonosovdenis.fintechapp.dataclasses.postcommentsdataclasses.getpostcommentsdataclasses

import com.google.gson.annotations.SerializedName

data class Sticker(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("sticker_id")
    val stickerId: Int,
    val images: List<Image>,
    @SerializedName("images_with_background")
    val imagesWithBackground: List<ImagesWithBackground>
)
