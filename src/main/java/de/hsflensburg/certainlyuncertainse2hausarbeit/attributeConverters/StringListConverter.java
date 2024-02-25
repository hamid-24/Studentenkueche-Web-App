package de.hsflensburg.certainlyuncertainse2hausarbeit.attributeConverters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

// this class is used for both the ingredient and category list in the recipe entity.
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    // before inserting into the database,
    // convert a list of Strings to a single String that can be stored as a varchar in the database.
    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return stringList != null ? String.join(";", stringList) : "";
    }

    // to treat categories and ingredients as lists of Strings,
    // convert a varchar to a List of Strings after querying the database.
    @Override
    public List<String> convertToEntityAttribute(String string) {
        return string != null ? Arrays.asList(string.split(";")) : emptyList();
    }
}