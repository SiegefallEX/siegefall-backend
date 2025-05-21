package com.michihides.siegefall_backend.config
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.michihides.siegefall_backend.model.CustomCharacter
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class DefenseAttributConverter : AttributeConverter<List<CustomCharacter?>, String> {

    private val objectMapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<CustomCharacter?>?): String {
        return try {
            if (attribute == null) "[]" else objectMapper.writeValueAsString(attribute)
        } catch (ex: Exception) {
            "[]"
        }
    }

    override fun convertToEntityAttribute(dbData: String?): List<CustomCharacter?> {
        return try {
            if (dbData == null || dbData.isBlank()) emptyList()
            else objectMapper.readValue(dbData, object : TypeReference<List<CustomCharacter?>>() {})
        } catch (ex: Exception) {
            emptyList()
        }
    }
}
