package demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Serves prime numbers in a speedy manner by implementing a cache from which we
 * extract a relevant sublist. The cache is populated on initial creation with an
 * upper bound but will grow dynamically as higher upper bounds are requested.
 * It will not block simultaneous incoming requests while the cache is expanded,
 * the requests will continue to use the old reference as appropriate and we use
 * CAS to swap in the new cache once done. We sacrifice space for speed with this
 * implementation.
 */
public class CachedSievedPrimeService implements PrimeService {

    public static final int DEFAULT_INITIAL_UPPERBOUND = 10000;
    private static final Log logger = LogFactory.getLog(CachedSievedPrimeService.class);
    private static final String GET_PRIMES_MSG = "Starting to generate primes for rang %d -> %d";
    private static final String NEW_CACHE_MSG = "Creating new cache with upper bound %d. Old upper bound %d";
    private static final String FOUND_PRIMES_MSG = "Found %d primes under %d upper bound took %d millis";
    private static final String POPULATED_PRIME_MSG = "Populated %d primes under %d upper bound took %d millis";

    private final AtomicReference<PrimeCache> primeCache;

    public CachedSievedPrimeService() {
        this(DEFAULT_INITIAL_UPPERBOUND);
    }

    public CachedSievedPrimeService(int upperBound) {
        checkInitialiseConstraints(upperBound);
        primeCache = new AtomicReference<>(new PrimeCache(upperBound));
    }

    @Override
    public NumPrimes getNumPrimes(int lower, int upper) {
        return new NumPrimes(getPrimes(lower, upper));
    }

    @Override
    public PrimeList getPrimes(int lower, int upper) {
        checkGetConstraints(lower, upper);

        // Explanation of what's going on here:
        // If the requested upper bound exceeds our current cache upper bound we create a new cache
        // with a new upper bound. We use CAS to swap in the new cache in case another thread has done
        // so before us. But we still only swap in if the new cache has the highest upper bound so far.
        // Once we've exited the CAS loop we will have a cache with a suitable upper bound from which
        // to extract our requested primes.

        long startTime = System.nanoTime();

        PrimeCache currentCache;
        PrimeCache newCache = null;

        do {
            logger.info(String.format(GET_PRIMES_MSG, lower, upper));
            currentCache = primeCache.get();
            if (upper <= currentCache.upperBound) {
                if (newCache != null)
                    logger.info("Another request updated cache first, we're done");
                break;
            } else if (newCache == null) { // If we've looped already, don't re-create the newCache
                logger.info(String.format(NEW_CACHE_MSG, upper, currentCache.upperBound));
                newCache = new PrimeCache(upper);
            }
        } while (!primeCache.compareAndSet(currentCache, newCache));

        PrimeList primeList = primeCache.get().getPrimes(lower, upper);

        primeList.setEllapsedNanos(System.nanoTime() - startTime);
        logger.info(String.format(FOUND_PRIMES_MSG, primeList.getNumPrimes(), upper, primeList.getEllapsedMillis()));

        return primeList;
    }

    private void checkInitialiseConstraints(int upper) {
        if (upper < 0)
            throw new IllegalArgumentException("Can not initialise service with negative value");
    }

    private void checkGetConstraints(int lower, int upper) {
        if (lower < 0 || upper < 0)
            throw new IllegalArgumentException("Cannot request primes in negative range");
        if (lower > upper)
            throw new IllegalArgumentException(("Lower bound of range cannot be greater than upper bound"));
    }

    private static final class PrimeCache {
        final int upperBound;
        final List<Integer> precalculatedPrimes = new ArrayList<>();

        PrimeCache(int upperBound) {
            this.upperBound = upperBound;
            populatePrecalculatedPrimes(upperBound);
        }

        private void populatePrecalculatedPrimes(int upperBound) {
            long startTime = System.nanoTime();

            BitSet sievedPrimes = new BitSet(upperBound) ;
            IntStream.range(2, upperBound).forEach(i -> sievedPrimes.set(i, true));

            int upperRoot = (int)Math.ceil(Math.sqrt(upperBound));
            IntStream.range(2, upperRoot + 1).forEachOrdered(i -> {
                if (sievedPrimes.get(i)) {
                    int n = i;
                    // Check condition before increment to prevent wrapping
                    while (upperBound - n >= i) {
                        n += i;
                        sievedPrimes.set(n, false);
                    }
                }
            });

            sievedPrimes.stream().forEachOrdered(precalculatedPrimes::add);

            long ellapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            logger.info(String.format(POPULATED_PRIME_MSG, precalculatedPrimes.size(), upperBound, ellapsedMillis));
        }

        PrimeList getPrimes(int lower, int upper) {
            // Since the list is sorted, we find the index of lower and upper bound using binary search
            // and return a subList view of the primes list (for speed reasons). Obviously this list is
            // not thread-safe as we're exposing the internals of our cache list.
            return new PrimeList(precalculatedPrimes.subList(getIndex(lower), getIndex(upper)));
        }

        int getIndex(int lower) {
            int p = Collections.binarySearch(precalculatedPrimes, lower);
            if (p < 0)
                return -(p + 1);
            return p;
        }

    }
}
