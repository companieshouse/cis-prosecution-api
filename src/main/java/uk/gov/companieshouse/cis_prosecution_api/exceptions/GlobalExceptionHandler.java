package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import static uk.gov.companieshouse.cis_prosecution_api.CisProsecutionApiApplication.APP_NAMESPACE;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.gov.companieshouse.cis_prosecution_api.error.ApiError;
import uk.gov.companieshouse.cis_prosecution_api.error.ErrorResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static uk.gov.companieshouse.cis_prosecution_api.utils.RequestConstants.X_REQUEST_ID_NAME;

@RestControllerAdvice("uk.gov.companieshouse.cis_prosecution_api.controllers")
public class GlobalExceptionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(APP_NAMESPACE);

    /**
     * System exception thrown when the supplied value for a path parameter cannot be coerced into the correct type.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        
        String context = request.getHeader(X_REQUEST_ID_NAME);
        
        List<ApiError> errors = new ArrayList<>();
        
        String fieldName = exception.getName();
        String message = exception.getMessage();
        String rejectedValue = String.valueOf(exception.getValue());
        
        errors.add(new ApiError(fieldName, message, rejectedValue));
        
        ErrorResponse errorResponse = new ErrorResponse(null, errors);
        
        logInfoContext(context, "MethodArgumentTypeMismatchException", errorResponse.toDebugMap());
        
        return errorResponse;
    }
    
    private void logInfoContext(String context, String message, Map<String, Object> debugMap){
        LOG.infoContext(context, message, debugMap);
    }
}
