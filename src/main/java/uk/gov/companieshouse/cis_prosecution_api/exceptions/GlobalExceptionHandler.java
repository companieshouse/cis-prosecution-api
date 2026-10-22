package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.companieshouse.cis_prosecution_api.CisProsecutionApiApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.cis_prosecution_api.utils.CommonUtils.toSnakeCase;
import static uk.gov.companieshouse.cis_prosecution_api.utils.RequestConstants.X_REQUEST_ID_NAME;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import uk.gov.companieshouse.cis_prosecution_api.error.ApiError;
import uk.gov.companieshouse.cis_prosecution_api.error.ErrorResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestControllerAdvice("uk.gov.companieshouse.cis_prosecution_api")
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(APP_NAMESPACE);

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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

    /**
     * System exception thrown when a request body is required but not supplied
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception,
        HttpServletRequest request) {

        HttpMessageNotReadableException safeException =
            Objects.requireNonNull(exception, "HttpMessageNotReadableException must not be null");

        ErrorResponse errorResponse;

        String context = request.getHeader(X_REQUEST_ID_NAME);

        ApiError apiError = mapHttpMessageNotReadableError(safeException);
        errorResponse = new ErrorResponse(null, List.of(apiError));

        logInfoContext(context, "HttpMessageNotReadableException", errorResponse.toDebugMap());
        return errorResponse;
    }

    private ApiError mapHttpMessageNotReadableError(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();

        String fieldName = null;
        String message;
        String rejectedValue = null;

        switch (cause) {
            case InvalidFormatException invalidFormatException -> {
                fieldName = extractFieldName(invalidFormatException.getPath());
                message = invalidFormatException.getMessage();
                rejectedValue = String.valueOf(invalidFormatException.getValue());
            }
            case UnrecognizedPropertyException unrecognizedPropertyException -> {
                fieldName = extractFieldName(unrecognizedPropertyException.getPath());
                message = unrecognizedPropertyException.getMessage();
            }
            case MismatchedInputException mismatchedInputException -> {
                fieldName = extractFieldName(mismatchedInputException.getPath());
                message = mismatchedInputException.getMessage();
            }
            case DatabindException databindException -> {
                fieldName = extractFieldName(databindException.getPath());
                message = databindException.getMessage();
            }
            case StreamReadException streamReadException -> {
                fieldName = streamReadException.processor() != null
                    ? streamReadException.processor().currentName()
                    : null;
                message = streamReadException.getMessage();
            }
            case null, default -> message = exception.getMessage();
        }

        return new ApiError(fieldName, message, rejectedValue);
    }

    private String extractFieldName(List<DatabindException.Reference> path) {
        return (path != null && !path.isEmpty())
            ? toSnakeCase(path.getFirst().getPropertyName())
            : null;
    }

    /**
     * System exception thrown when validation on an argument annotated with @Valid fails.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {

        String context = request.getHeader(X_REQUEST_ID_NAME);

        List<ApiError> errors = new ArrayList<>();

        for (FieldError fieldError : exception.getFieldErrors()) {
            String fieldName = toSnakeCase(fieldError.getField());
            String message = fieldError.getDefaultMessage();
            String rejectedValue = fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : null;
            errors.add(new ApiError(fieldName, message, rejectedValue));
        }

        for (ObjectError objectError: exception.getBindingResult().getGlobalErrors()) {
            String message = objectError.getDefaultMessage();
            errors.add(new ApiError(null, message, null));
        }

        ErrorResponse errorResponse = new ErrorResponse(null, errors);

        logInfoContext(context, "MethodArgumentNotValidException", errorResponse.toDebugMap());

        return errorResponse;
    }

    /**
     * System exception thrown by RestTemplate in the case of HTTP status 4xx
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException exception, HttpServletRequest request) {

        String context = request.getHeader(X_REQUEST_ID_NAME);

        ErrorResponse errorResponse;

        try {
            String responseBody = exception.getResponseBodyAsString();
            errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
        } catch (Exception e) {
            errorResponse = new ErrorResponse(exception.getMessage(), null);
        }

        logInfoContext(context, "HttpClientErrorException", errorResponse.toDebugMap());

        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    /**
     * System exception thrown by RestTemplate in the case of HTTP status 5xx
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Object> handleHttpServerErrorException(HttpServerErrorException exception, HttpServletRequest request) {

        String context = request.getHeader(X_REQUEST_ID_NAME);

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), null);

        logInfoContext(context, "HttpServerErrorException", errorResponse.toDebugMap());

        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    /**
     * System exception thrown by RestTemplate in the case of an unknown HTTP status
     */
    @ExceptionHandler(UnknownHttpStatusCodeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleUnknownHttpStatusCodeException(UnknownHttpStatusCodeException exception, HttpServletRequest request) {

        String context = request.getHeader(X_REQUEST_ID_NAME);

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), null);

        logInfoContext(context, "UnknownHttpStatusCodeException", errorResponse.toDebugMap());

        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    /**
     * System exception thrown when a constraint violation occurs for Path parameters.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request) {

        String context = request.getHeader(X_REQUEST_ID_NAME);

        List<ApiError> errors = exception.getConstraintViolations().stream()
            .map(violation -> {
                String fullPath = violation.getPropertyPath().toString();
                String fieldName = toSnakeCase(fullPath.contains(".") ? fullPath.substring(fullPath.lastIndexOf('.') + 1) : fullPath);

                return new ApiError(
                    fieldName,
                    violation.getMessage(),
                    violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : "null"
                );
            })
            .toList();

        ErrorResponse errorResponse = new ErrorResponse(null, errors);

        logInfoContext(context, "ConstraintViolationException", errorResponse.toDebugMap());

        return errorResponse;
    }

    /**
     * System exception thrown when a resource is not found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {

        String context = request.getHeader(X_REQUEST_ID_NAME);

        List<ApiError> errors = new ArrayList<>();
        errors.add(exception.getApiError());

        ErrorResponse errorResponse = new ErrorResponse(null, errors);

        logInfoContext(context, "ResourceNotFoundException", errorResponse.toDebugMap());

        return errorResponse;
    }

    @ExceptionHandler(XmlParsingException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleXmlParsingException(XmlParsingException exception, HttpServletRequest request) {
        String context = request.getHeader(X_REQUEST_ID_NAME);

        List<ApiError> errors = new ArrayList<>();
        errors.add(exception.getApiError());

        ErrorResponse errorResponse = new ErrorResponse(null, errors);

        logInfoContext(context, "XmlParsingException", errorResponse.toDebugMap());

        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception exception, HttpServletRequest request) {

        String context = request.getHeader(X_REQUEST_ID_NAME);

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), null);

        logInfoContext(context, "Exception", errorResponse.toDebugMap());

        return errorResponse;
    }

    private void logInfoContext(String context, String message, Map<String, Object> debugMap){
        LOG.infoContext(context, message, debugMap);
    }
}
