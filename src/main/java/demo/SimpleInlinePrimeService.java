package demo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simple prime calculator which does no caching but calculates a list of primes
 * based on checking roots (up to sqrt of upper bound) for each request.
 */
public class SimpleInlinePrimeService implements PrimeService {
    @Override
    public PrimeList getPrimes(int lower, int upper) {
        checkGetConstraints(lower, upper);

        long startNanos = System.nanoTime();
        List<Integer> primes = IntStream.range(lower, upper)
                .filter(SimpleInlinePrimeService::isPrime)
                .boxed()
                .collect(Collectors.toList());

        PrimeList pl = new PrimeList(primes);
        pl.setEllapsedNanos(System.nanoTime() - startNanos);

        return pl;
    }

    @Override
    public NumPrimes getNumPrimes(int lower, int upper) {
        return new NumPrimes(getPrimes(lower, upper));
    }

    private static boolean isPrime(int num) {
        if (num < 2)
            return false;
        else if (num <=3 )
            return true;

        int upperRoot = (int)Math.ceil(Math.sqrt(num));
        for (int factor = 2; factor <= upperRoot; factor++) {
            if (num % factor == 0)
                return false;
        }
        return true;
    }

    private static void checkGetConstraints(int lower, int upper) {
        if (lower < 0 || upper < 0)
            throw new IllegalArgumentException("Cannot request primes in negative range");
        if (lower > upper)
            throw new IllegalArgumentException(("Lower bound of range cannot be greater than upper bound"));
    }
}
