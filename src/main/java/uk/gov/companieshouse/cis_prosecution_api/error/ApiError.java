package uk.gov.companieshouse.cis_prosecution_api.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ApiError implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("field_name")
    private String fieldName;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("rejected_value")
    private String rejectedValue;
    
    public ApiError() {
        // Default constructor for serialization
    }
    
    public ApiError(String fieldName, String message, String rejectedValue) {
        this.fieldName = fieldName;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getRejectedValue() {
        return rejectedValue;
    }
    
    public void setRejectedValue(String rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
}
