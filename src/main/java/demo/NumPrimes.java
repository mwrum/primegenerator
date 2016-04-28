package demo;

import java.util.concurrent.TimeUnit;

/**
 * An object encapsulating a list of primes. We use this so we can encapsulate some additional
 * useful information such as ellapsed time and number of primes calculated.
 */
public class NumPrimes {
    private final PrimeList primeList;

    public NumPrimes(PrimeList primeList) {
        this.primeList = primeList;
    }

    public int getNumPrimes() {
        return primeList.getPrimeList().size();
    }

    public long getEllapsedNanos() {
        return primeList.getEllapsedNanos();
    }

    public long getEllapsedMillis() {
        return TimeUnit.NANOSECONDS.toMillis(primeList.getEllapsedNanos());
    }

    public void setEllapsedNanos(long nanos) {
        primeList.setEllapsedNanos(nanos);
    }
}
