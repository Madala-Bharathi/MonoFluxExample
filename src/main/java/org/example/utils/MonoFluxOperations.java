package org.example.utils;

import reactor.core.publisher.Mono;

public class MonoFluxOperations {
    // just example
//    What it does: Creates a Mono that emits exactly one value (already available at assembly time) and then completes.
//    When to use: Returning a constant/known value, stubbing data in tests, or adapting an immediate value to a reactive type.
//    Value is captured eagerly at assembly (not lazy).
//    For laziness, prefer Mono.fromSupplier or Mono.defer.
    public Mono<String> greeting(){
        return Mono.just("Hello Reactive!").log();
    }
}
