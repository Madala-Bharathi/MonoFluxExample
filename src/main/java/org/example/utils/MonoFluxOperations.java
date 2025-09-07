package org.example.utils;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Component
public class MonoFluxOperations {
    /*** 1 just
     * What it does: Creates a Mono that emits exactly one value (already available at assembly time) and then completes.
     * When to use: Returning a constant/known value, stubbing data in tests, or adapting an immediate value to a reactive type.
     * Value is captured eagerly at assembly (not lazy).
     * For laziness, prefer Mono.fromSupplier or Mono.defer
     */
    public Mono<String> greeting(){
        return Mono.just("Hello Reactive!").log();
    }

    /*** 2 Mono.fromSupplier
     * What it does: Defers the value computation until subscription time. The supplier is executed per subscriber.
     * When to use: Expensive value creation, time-sensitive values, or when you must re-evaluate per subscription.
     * keynotes: Each subscription calls the supplier again, so each subscriber may get a different value.
     */
    public Mono<Long> nanoTimeNow(){
        return Mono.fromSupplier(System::nanoTime);
    }

    /*** 3 range
     * What it does: Emits a sequence of consecutive int values: start and count.
     * When to use: Generating test data, iteration-like sequences, indexes.
     * keynotes: Finite, synchronous source (unless further operators add async behavior).
     */
    public Flux<Integer> rangeExample(){
        return Flux.range(5,3).log();
    }

    /*** 4 map
     * What it does: Synchronous, one-to-one transformation. Preserves ordering.
     * When to use: Pure data mapping where no async work is needed.
     * keynotes: If the mapping may throw, the error fails the stream.
     */
    public Flux<Integer> wordLength(Flux<String> words){
        return words.map(String::length).log();
    }

    /*** 5 flatMap
     * What it does: Transforms each element into a Publisher and merges them concurrently. Ordering may change.
     * When to use: Async composition (e.g., database/file calls later), or fan-out fan-in scenarios.
     * keynotes: Concurrency can reorder results; cap with flatMap(..., concurrency) or use flatMapSequential/concatMap to keep order.
     */
    public Flux<String> flatMapExample(Flux<String> ids){
        return ids.flatMap(id -> Mono.just(id + "-task").delayElement(Duration.ofMillis(20))); //order may vary
    }

    /*** 6 concatMap
     * What it does: Like flatMap but serializes inner publishers to preserve input order.
     * When to use: You need async inner work and strict ordering.
     * keynotes: Less concurrency vs flatMap, but deterministic order.
     */
     public Flux<String> concatMapExample(Flux<String> ids){
         return ids.concatMap(id -> Mono.just(id + "-task").delayElement(Duration.ofMillis(20))); //order doesn't vary
     }

    /*** 7 filter
     * What it does: Keeps only elements that satisfy a predicate.
     * When to use: Basic selection, validation, or noise filtering.
     * keynotes: Non-matching elements are dropped silently.
     */
    public Flux<Integer> filterExample(){
        return Flux.range(1, 10).filter(i -> i%2 == 0);
    }

    /*** 8 buffer
     * What it does: Accumulates items into List<T> batches (e.g., fixed size).
     * When to use: Batch processing, windowed aggregations, or chunked I/O.
     * keynotes: Final batch may be smaller; use bufferTimeout for time-based batching.
     */
    public Flux<List<Integer>> bufferExample(){
        return Flux.range(1,10).buffer(3);
    }

    /*** 9 zip
     * What it does: Combines elements from multiple sources by index: emits when each source provides its next element.
     * When to use: Pairing related streams (e.g., first/last name, id/value).
     * keynotes: Stops when shortest source completes.
     */
    public Flux<String> fullNameExample(){
        Flux<String> firstName = Flux.just("Ada", "Alan");
        Flux<String> lastName = Flux.just("Lovelace", "Turing");
        return Flux.zip(firstName,lastName,(a,b) -> a+" "+ b);
    }
    /*** 10 fallback, timeout
     * What it does (timeout): If no signal arrives within the specified duration, the source errors with TimeoutException.
     * What it does (onErrorResume): Intercepts an error and switches to a fallback Publisher.
     * When to use: Guard slow/blocked sources and provide graceful fallback.
     * keynotes: Use withVirtualTime in tests to avoid slow sleeps.
     */
//    What it does: If the source doesn’t emit within the given duration, signal a TimeoutException. Often paired with fallback (onErrorResume).
//    When to use: Guard against slow/blocked sources.
//    Notes: You can pass another Publisher to switch to on timeout.
    public Mono<String> fallback(){
        Mono<String> slow = Mono.just("primary").delayElement(Duration.ofMillis(200));
        return slow.timeout(Duration.ofMillis(50))
                .onErrorResume(e -> Mono.just("fallback"));
    }

    /*** 11 distinct
     *What it does: Emits each value only once by tracking “seen” items.
     * When to use: Deduping events (e.g., IDs, keys) when duplicates may appear.
     * Notes:
     * Uses equals/hashCode by default.
     * For high-cardinality or custom keys, use distinct(keyExtractor) or distinctUntilChanged() if you only care about consecutive duplicates.
     */
    public Flux<Integer> distinctExample(){
        return Flux.just(1,1,2,2,2,3,3,3,3).distinct();
    }


    /*** 12 take
     * What it does: Takes the first N elements (or a time window variant) and then cancels upstream.
     * When to use: Guard rails in tests, pagination-like sampling, or cutting infinite streams.
     * Notes: Cancels upstream once the quota is reached → good for conserving work.
     */
    public Flux<Integer> takeExample(){
        return Flux.range(1,10).take(3);
    }

    /*** 13 skip
     * What it does: Skips the first N elements and starts emitting after that.
     * When to use: Offset/skip headers, drop warm-up values, or paging.
     * Notes: Complementary to take.
     */
    public Flux<Integer> skipExample(){
        return Flux.range(1,10).skip(3);
    }

    public Flux<String> skipStringExample(){
        return Flux.just("Apple", "Banana", "Cranberry", "Dates").skip(2);
    }

    /*** 14 takeWhile
     * What it does: Takes items while the predicate is true; stops (and completes) the moment it becomes false.
     * When to use: Truncating on a condition (e.g., thresholds).
     * Notes: One-pass boundary; once false, upstream is cancelled.
     */
    public Flux<Integer> takeWhileExample(){
        return Flux.range(1,20).takeWhile(i -> i<5);
    }

    /*** 15 window
     * What it does: Chunks the stream into windows (each window is a Flux<T>).
     * When to use: Streaming batch processing; unlike buffer (lists), windows let you process each chunk as it emits, not after it closes.
     * Notes: You usually follow with flatMap(w -> w.collectList()) or process each inner Flux.
     */
    public  Flux<List<Integer>> windowExample(){
        return Flux.range(1,10)      //Flux<Integer>
                .window(3)              //Flux<Flux<Integer>
                .flatMap(Flux::collectList);
    }

    /*** 16 groupBy
     * What it does: Splits the stream into multiple keyed substreams (GroupedFlux<K,T>).
     * When to use: Routing by key (e.g., parity, tenant, type) for per-key aggregation or processing.
     * Notes: Each group is its own flux; don’t forget to drain groups (memory).
     */
    public Flux<String> groupBy(){
        return Flux.range(1,6)
                .groupBy(i -> i %2 != 0 ? "odd" : "even")
                .log()
                .flatMap(i -> i.collectList().map(list -> i.key() + list));
    }

    /*** 17 merge/mergeWith
     * What it does: Interleaves multiple publishers as values arrive; ordering across sources is not guaranteed.
     * When to use: Fan-in of independent async sources (e.g., multiple tickers).
     * Notes: Use mergeDelayError to delay errors; use concat if you need strict sequence.
     */
    public Flux<Integer> mergeWithExample(){
        Flux<Integer> a = Flux.range(1, 2).log().delayElements(Duration.ofMillis(20));
        Flux<Integer> b = Flux.range(3, 2).log().delayElements(Duration.ofMillis(20));
        return a.mergeWith(b);
        //Flux.merge(a,b) -> static, can also be used for more than 2 Flux unlike merge with (instance method)
    }

    /*** 18 concat/concatWith
     * What it does: Subscribes to publishers sequentially; the next starts only when the previous completes.
     * When to use: You need predictable ordering across sources.
     * Notes: Contrast with merge (concurrent/interleaved).
     */
    public Flux<Integer> concatWithExample(){
        Flux<Integer> a = Flux.range(1, 2).log().delayElements(Duration.ofMillis(20));
        Flux<Integer> b = Flux.range(3, 2).log().delayElements(Duration.ofMillis(20));
        return a.concatWith(b);
    }

    /*** 19 combineLatest
     * What it does: Combines sources using the latest value from each once all have emitted at least one item;
     * then recomputes whenever any source emits.
     * When to use: Live dashboards, joining streams with different rates.
     * Notes: Stops when any source completes? It continues while at least one emits,
     * but no new combinations can be formed if one never emitted; typically used with finite or “primed” sources.
     */
    public Flux<String> combineLatestExample(){
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        Flux<Integer> flux2 = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(200));
        return Flux.combineLatest(flux1, flux2, (value1, value2) -> value1 + value2); // Combine logic
    }

    /*** 20 startWith
     * What it does: Prepends values or another publisher to the beginning of the sequence.
     * When to use: Emit headers, defaults, or initial state before real data.
     * Notes: Order is strictly: prepended → original.
     */
    public Flux<Integer> startWithExample(){
        return Flux.range(1,3).startWith(0);
    }

    /*** 21 delayElements
     * What it does: Delays each element’s emission by a fixed Duration.
     * The upstream is subscribed immediately, but items are shifted in time downstream.
     * When to use: Throttling UI/event bursts, simulating latency in tests, pacing downstream consumers.
     * Notes: Uses a scheduler (default: Schedulers.parallel()); in tests, prefer withVirtualTime.
     * refer above examples
     */

    /*** 22 delaySubscription
     * What it does: Delays when the subscription starts. Nothing upstream happens until the delay elapses.
     * When to use: Backoff before starting work, staggering subscriptions.
     * Notes: Different from delayElements which shifts each item; this shifts the start only.
     */
    public Flux<String> startLate(){
        return Flux.just("Delay by 1s").delaySubscription(Duration.ofSeconds(1));
    }

    /*** 23 subscribeOn
     * What it does: Decides which scheduler is used when the subscription happens and where upstream work runs.
     * When to use: Offload blocking or CPU-heavy source creation to an appropriate scheduler.
     * Notes: Only the first subscribeOn in the chain takes effect.
     */
    public Flux<Integer> subscribeOn(){
        return Flux.range(1,5)
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> i * 2);
    }

    /*** 24 publishOn
     * What it does: Switches downstream processing to the given scheduler from this point onward.
     * When to use: Change execution context mid-pipeline (e.g., parse on IO, compute on parallel).
     * Notes: Unlike subscribeOn, multiple publishOns can take effect at different points.
     */
    public Flux<Integer> publishOn(){
        return Flux.range(1, 5)
                .map(i -> i * 2)
                .publishOn(Schedulers.parallel())
                .map(i -> i + 1);
    }

    /*** 25 onErrorReturn
     * What it does: Replaces any error with a constant fallback value, then completes.
     * When to use: Safe defaults (e.g., config, feature flag reads).
     * Notes: Use conditionally via overloads (onErrorReturn(predicate, value)).
     */
    public Flux<String> safeValue(){
        return Flux.<String>error(new RuntimeException("error"))
                .onErrorReturn("Default");
    }

    /*** 26 onErrorResume
     * What it does: On error, switch to an alternate Publisher (dynamic fallback based on error).
     * When to use: Fallback paths that require logic (e.g., read-through cache).
     * Notes: You can match by exception type.
     */
    public Mono<String> dynamicSafeValue(){
        return Mono.<String>error(new IllegalStateException("bad request"))
                .onErrorResume(IllegalStateException.class, e -> Mono.just("Resume"));
    }
}
