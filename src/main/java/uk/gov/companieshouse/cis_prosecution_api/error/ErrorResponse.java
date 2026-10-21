package uk.gov.companieshouse.cis_prosecution_api.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse implements Serializable {
    private String message;
    private List<ApiError> apiErrors;
    
    public ErrorResponse() {
        // Default constructor for serialization
    }
    
    public ErrorResponse(String message, List<ApiError> apiErrors) {
        this.message = message;
        this.apiErrors = apiErrors;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<ApiError> getApiErrors() {
        return apiErrors;
    }
    
    public void setApiErrors(List<ApiError> apiErrors) {
        this.apiErrors = apiErrors;
    }

    public Map<String, Object> toDebugMap() {
        final Map<String, Object> debugMap = new HashMap<>();

        debugMap.put("ErrorResponse", getPropertiesDebugMap());

        return debugMap;
    }

    private Map<String, Object> getPropertiesDebugMap() {
        final Map<String, Object> debugMap = new HashMap<>();

        debugMap.put("message", message);
        debugMap.put("errors", apiErrors != null ? apiErrors.stream().map(ApiError::toDebugMap).toList() : null);

        return debugMap;
    }
}
