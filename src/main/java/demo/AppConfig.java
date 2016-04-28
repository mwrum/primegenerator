package demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public PrimeService primeService(){
        // Change the PrimeService implementation here to test others.
        return new CachedSievedPrimeService();
        //return new SimpleInlinePrimeService();
    }
}
