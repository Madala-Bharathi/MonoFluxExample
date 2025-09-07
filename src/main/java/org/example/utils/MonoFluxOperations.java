package org.example.utils;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

@Component
public class MonoFluxOperations {
    /***
     * What it does: Creates a Mono that emits exactly one value (already available at assembly time) and then completes.
     * When to use: Returning a constant/known value, stubbing data in tests, or adapting an immediate value to a reactive type.
     * Value is captured eagerly at assembly (not lazy).
     * For laziness, prefer Mono.fromSupplier or Mono.defer
     */
    public Mono<String> greeting(){
        return Mono.just("Hello Reactive!").log();
    }

    /***
     * What it does: Defers the value computation until subscription time. The supplier is executed per subscriber.
     * When to use: Expensive value creation, time-sensitive values, or when you must re-evaluate per subscription.
     * keynotes: Each subscription calls the supplier again, so each subscriber may get a different value.
     */
    public Mono<Long> nanoTimeNow(){
        return Mono.fromSupplier(System::nanoTime);
    }

    /***
     * What it does: Emits a sequence of consecutive int values: start and count.
     * When to use: Generating test data, iteration-like sequences, indexes.
     * keynotes: Finite, synchronous source (unless further operators add async behavior).
     */
    public Flux<Integer> rangeExample(){
        return Flux.range(5,3).log();
    }

    /***
     * What it does: Synchronous, one-to-one transformation. Preserves ordering.
     * When to use: Pure data mapping where no async work is needed.
     * keynotes: If the mapping may throw, the error fails the stream.
     */
    public Flux<Integer> wordLength(Flux<String> words){
        return words.map(String::length).log();
    }

    /***
     * What it does: Transforms each element into a Publisher and merges them concurrently. Ordering may change.
     * When to use: Async composition (e.g., database/file calls later), or fan-out fan-in scenarios.
     * keynotes: Concurrency can reorder results; cap with flatMap(..., concurrency) or use flatMapSequential/concatMap to keep order.
     */
    public Flux<String> flatMapExample(Flux<String> ids){
        return ids.flatMap(id -> Mono.just(id + "-task").delayElement(Duration.ofMillis(20))); //order may vary
    }

    /***
     * What it does: Like flatMap but serializes inner publishers to preserve input order.
     * When to use: You need async inner work and strict ordering.
     * keynotes: Less concurrency vs flatMap, but deterministic order.
     */
     public Flux<String> concatMapExample(Flux<String> ids){
         return ids.concatMap(id -> Mono.just(id + "-task").delayElement(Duration.ofMillis(20))); //order doesn't vary
     }

    /***
     * What it does: Keeps only elements that satisfy a predicate.
     * When to use: Basic selection, validation, or noise filtering.
     * keynotes: Non-matching elements are dropped silently.
     */
    public Flux<Integer> filterExample(){
        return Flux.range(1, 10).filter(i -> i%2 == 0);
    }

    /***
     * What it does: Accumulates items into List<T> batches (e.g., fixed size).
     * When to use: Batch processing, windowed aggregations, or chunked I/O.
     * keynotes: Final batch may be smaller; use bufferTimeout for time-based batching.
     */
    public Flux<List<Integer>> bufferExample(){
        return Flux.range(1,10).buffer(3);
    }

    /***
     * What it does: Combines elements from multiple sources by index: emits when each source provides its next element.
     * When to use: Pairing related streams (e.g., first/last name, id/value).
     * keynotes: Stops when shortest source completes.
     */
    public Flux<String> fullNameExample(){
        Flux<String> firstName = Flux.just("Ada", "Alan");
        Flux<String> lastName = Flux.just("Lovelace", "Turing");
        return Flux.zip(firstName,lastName,(a,b) -> a+" "+ b);
    }
    /***
     * What it does (timeout): If no signal arrives within the specified duration, the source errors with TimeoutException.
     * What it does (onErrorResume): Intercepts an error and switches to a fallback Publisher.
     * When to use: Guard slow/blocked sources and provide graceful fallback.
     * keynotes: Use withVirtualTime in tests to avoid slow sleeps.
     */
    public Mono<String> fallback(){
        Mono<String> slow = Mono.just("primary").delayElement(Duration.ofMillis(200));
        return slow.timeout(Duration.ofMillis(50))
                .onErrorResume(e -> Mono.just("fallback"));
    }

}
