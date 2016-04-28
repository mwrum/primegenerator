package demo;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An object encapsulating a list of primes. We use this so we can encapsulate some additional
 * useful information such as ellapsed time and number of primes calculated.
 */
public class PrimeList {

    private long ellapsedNanos;
    private long ellapsedMillis;
    private final int numPrimes;
    private final List<Integer> primeList;

    public PrimeList(List<Integer> primeList) {
        this.primeList = primeList;
        this.numPrimes = primeList.size();
    }

    public int getNumPrimes() {
        return numPrimes;
    }

    public long getEllapsedNanos() {
        return ellapsedNanos;
    }

    public long getEllapsedMillis() {
        return ellapsedMillis;
    }

    public List<Integer> getPrimeList() {
        return primeList;
    }

    public void setEllapsedNanos(long nanos) {
        ellapsedNanos = nanos;
        ellapsedMillis = TimeUnit.NANOSECONDS.toMillis(ellapsedNanos);
    }
}
