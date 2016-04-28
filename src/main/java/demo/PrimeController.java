package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ComponentScan
@EnableAutoConfiguration
@RestController
public class PrimeController {
    protected static final String GREETING =
            "<h2>Welcome to Prime Generator.</h2><b>Usage:</b><br>" +
            "http://host:port/primes/lower/upper<br>" +
            "http://host:port/numprimes/lower/upper<br><br>" +
            "where lower and upper are positive integers and lower < upper";

    @Autowired
    private PrimeService primeService;

    @RequestMapping("/")
    public String index() {
        return GREETING;
    }

    @RequestMapping("/primes/{lower}/{upper}")
    public PrimeList primes(@PathVariable int lower, @PathVariable int upper) {
        return primeService.getPrimes(lower, upper);
    }

    @RequestMapping("/numprimes/{lower}/{upper}")
    public NumPrimes numPrimes(@PathVariable int lower, @PathVariable int upper) {
        return primeService.getNumPrimes(lower, upper);
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
