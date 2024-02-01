package es.oretania.programaapi

import com.google.gson.annotations.SerializedName

data class DefinitionResponse(
    @SerializedName("list") val definitions: List<Definition>
)

data class Definition(
    @SerializedName("definition") val definition: String,
    @SerializedName("author") val author: String,
    @SerializedName("example") val example: String,
    @SerializedName("permalink") val permalink: String
)
