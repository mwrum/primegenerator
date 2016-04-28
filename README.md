## Synopsis

Demo project for a Spring Boot REST service which generates primes. 

## What/How

A Spring Boot Web REST application generated using https://start.spring.io[SPRING INITIALIZR].

The application will use an implementation of a PrimeService to generate a serve primes to 
REST GET requests. By default the service will generate primes using an implementation of 
sieve of Eratosthenes while also caching the previously calculated primes. The service will 
take some time to initialise on the first request of a large upper bound prime but after 
that should be very quick to return subsequent ranges below the current cached upper bound. 
It does this by sacrificing space by storing the list of primes from 0 to upper bound and 
when a request falls below the upper bound all we do is find the index in the existing list 
for lower and upper using binary search then present a view of the existing list using 
subList. This way no further allocation or calculation is required. 

## Installation

Spring Boot projects can be easily built with the
https://github.com/takari/maven-wrapper[maven wrapper] which is package in the repo. 
You also need JDK 1.8.

[indent=0]
----
	$ ./mvnw clean install
----

If you want to build with the regular `mvn` command, you will need
http://maven.apache.org/run-maven/index.html[Maven v3.0.5 or above].


## Usage

java -jar target/primegenerator-0.0.1-SNAPSHOT.jar
The default port is 8080.
Use your browser or curl to 
http://localhost:8080/

To execute the REST GET requests for primes, use the following examples:
http://localhost:8080/numprimes/0/100
http://localhost:8080/numprimes/200/50000

These will return primes in the range of 0 -> 100 (exclusive)
and 200 (inclusive) -> 50000 (exclusive)

or to see a less verbose response use the version which only returns the number of
primes calculated:
http://localhost:8080/numprimes/0/50000000

Malformed URLs result in 400 or 404 status being returned depending on the type of error.

## Testing an alternate service

See:
src/main/java/demo/AppConfig.java

