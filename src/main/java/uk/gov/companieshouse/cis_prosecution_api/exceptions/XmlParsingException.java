package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import uk.gov.companieshouse.cis_prosecution_api.error.ApiError;

/**
 * Custom exception indicating an error occurred during XML parsing.
 */
public class XmlParsingException extends RuntimeException {

    private final ApiError apiError;

    public XmlParsingException(ApiError error) {
        super("");
        this.apiError = error;
    }

    public ApiError getApiError() {
        return apiError;
    }

}