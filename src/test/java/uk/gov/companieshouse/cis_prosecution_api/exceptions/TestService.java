package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;

public interface TestService {

    String getString() throws MethodArgumentNotValidException;
}
