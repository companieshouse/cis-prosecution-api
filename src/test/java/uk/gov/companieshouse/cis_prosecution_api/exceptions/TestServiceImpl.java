package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    public TestServiceImpl() {
    }

    @Override
    public String getString() {
        return "Test string";
    }
}
