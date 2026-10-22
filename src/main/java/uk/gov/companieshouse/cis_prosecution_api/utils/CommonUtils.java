package uk.gov.companieshouse.cis_prosecution_api.utils;

public class CommonUtils {

    /**
     * Converts a camelCase field name to snake_case.
     *
     * @param fieldName the camelCase field name
     * @return the snake_case field name
     * Strip array indices (e.g., [0], [1]) from field names for error reporting consistency,
     * so that all elements of an array field are treated as the same logical field.
     */
    public static String toSnakeCase(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return fieldName;
        }
        fieldName = fieldName.replaceAll("\\[\\d+]", "");
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
