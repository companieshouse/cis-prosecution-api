package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import uk.gov.companieshouse.cis_prosecution_api.error.ApiError;

/**
 * Custom exception indicating a particular resource could not be found.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final ApiError apiError;

    public ResourceNotFoundException(ApiError apiError) {

        super("");
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }
}
