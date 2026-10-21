package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<?> myTest() throws MethodArgumentNotValidException {
        String string = testService.getString();
        return ResponseEntity.ok(string);
    }
}
