package demo;

/**
 * Provides a service to generate primes
 */
public interface PrimeService {
    /**
     * Generates all primes in the specified range
     * @param lower lower bound value for list (inclusive)
     * @param upper upper bound value for list (exclusive)
     * @return an object representation of the list of primes with some additional information
     */
    PrimeList getPrimes(int lower, int upper);

    /**
     * Counts all primes in the specified range
     * @param lower lower bound value for list (inclusive)
     * @param upper upper bound value for list (exclusive)
     * @return an object representation of the number of primes with some additional information
     */
    NumPrimes getNumPrimes(int lower, int upper);
}
