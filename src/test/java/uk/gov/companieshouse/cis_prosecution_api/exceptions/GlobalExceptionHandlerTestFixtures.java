package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import org.jspecify.annotations.NullMarked;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;


public final class GlobalExceptionHandlerTestFixtures {

    private GlobalExceptionHandlerTestFixtures() {};

    static class TestDto {
        private String field1;
        private Integer field2;
    }
    static class TestController {
        public void testMethod(Integer param1, TestDto param2) {}
    }

    public static MethodArgumentTypeMismatchException createMethodArgumentTypeMismatchException(String parameterName, String parameterValue, String message) throws NoSuchMethodException {
        Method method = TestController.class.getMethod("testMethod", Integer.class, TestDto.class);
        return new MethodArgumentTypeMismatchException(
            parameterValue,
            Integer.class,
            parameterName,
            new MethodParameter(method, 0),
            new IllegalArgumentException(message));
    }

    static class CustomHttpInputMessage implements HttpInputMessage {

        private final HttpHeaders headers;
        private final InputStream body;

        public CustomHttpInputMessage(String bodyContent) {
            this.headers = new HttpHeaders();
            this.body = new ByteArrayInputStream(bodyContent.getBytes());
        }

        @Override
        @NullMarked
        public InputStream getBody() {
            return body;
        }

        @Override
        @NullMarked
        public HttpHeaders getHeaders() {
            return headers;
        }
    }

    public static HttpMessageNotReadableException createHttpMessageNotReadableException(String message) {
        return new HttpMessageNotReadableException(message, new CustomHttpInputMessage(""));
    }

    public static MethodArgumentNotValidException createMethodArgumentNotValidException() throws NoSuchMethodException {
        // Create a BindingResult with a target object name
        BindingResult bindingResult = new BeanPropertyBindingResult(new TestDto(), "testDto");

        bindingResult.addError(new FieldError("testDto", "field1", null, false, null, null,"field1 cannot be null"));
        bindingResult.addError(new FieldError("testDto", "field2", "11", false, null, null,"field2 cannot be greater than 10"));

        // Create a MethodParameter for the exception constructor
        Method method = TestController.class.getMethod("testMethod", Integer.class, TestDto.class);
        MethodParameter methodParameter = new MethodParameter(method, 1);

        // Return the MethodArgumentNotValidException
        return new MethodArgumentNotValidException(methodParameter, bindingResult);
    }

    public static HttpClientErrorException createHttpClientErrorException() {
        return HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", new HttpHeaders(), new byte[0], null);
    }

    public static HttpServerErrorException createHttpServerErrorException(String statusText) {
        return HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, statusText, new HttpHeaders(), new byte[0], null);
    }

    public static UnknownHttpStatusCodeException createUnknownHttpStatusCodeException(String statusText) {
        return new UnknownHttpStatusCodeException(599, statusText, null, null, null);
    }

    public static RuntimeException createRuntimeException(String message) {
        return new RuntimeException(message);
    }
}

