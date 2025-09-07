package org.example.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@SpringBootTest
class MonoFluxOperationsTest {

    @Autowired
    MonoFluxOperations monoFluxOperations;

    @Test
    void justExampleTest(){
        StepVerifier.create(monoFluxOperations.greeting())
                .expectNext("Hello Reactive!")
                .verifyComplete();
    }

    @Test
    void nanoTimeNowTest(){
        Mono<Long> longMono = monoFluxOperations.nanoTimeNow();
        StepVerifier.create(longMono.zipWith(longMono, (a,b) -> !a.equals(b)))// a == b in case of Mono.just()
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void rangeExampleTest(){
        StepVerifier.create(monoFluxOperations.rangeExample())
                .expectNext(5,6,7)
                .verifyComplete();
    }

    @Test
    void rangeExampleCountTest(){
        StepVerifier.create(monoFluxOperations.rangeExample())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void rangeExampleRequestTest(){
        StepVerifier.create(monoFluxOperations.rangeExample(), 1)
                .expectNext(5)
                .thenRequest(2)
                .expectNext(6,7)
                .verifyComplete();
    }

    @Test
    void wordLengthTest(){
        StepVerifier.create(monoFluxOperations.wordLength(Flux.just("Mango","Iceapple","Sapota", "Pineapple")))
                .expectNext(5,8,6,9)
                .verifyComplete();
    }

    @Test
    void flatMapExampleTest(){
        StepVerifier.create(monoFluxOperations.flatMapExample(Flux.just("A", "B","C","D")).sort()) // order may change for delay
                .expectNext("A-task", "B-task", "C-task","D-task")
                .verifyComplete();
    }

    @Test
    void concatExampleTest(){
        StepVerifier.create(monoFluxOperations.concatMapExample(Flux.just("A","B","C")))  //sort doesn't vary
                .expectNext("A-task", "B-task", "C-task")
                .verifyComplete();
    }

    @Test
    void filterExampleTest(){
        StepVerifier.create(monoFluxOperations.filterExample())
                .expectNext(2,4,6,8,10)
                .verifyComplete();
    }

    @Test
    void bufferExampleTest(){
        StepVerifier.create(monoFluxOperations.bufferExample())
                .expectNext(List.of(1,2,3), List.of(4,5,6), List.of(7,8,9), List.of(10))
                .verifyComplete();
    }

    @Test
    void zipWithExampleTest(){
        StepVerifier.create(monoFluxOperations.fullNameExample())
                .expectNext("Ada Lovelace", "Alan Turing")
                .verifyComplete();
    }

    @Test
    void fallbackTest(){
        StepVerifier.withVirtualTime(monoFluxOperations::fallback)
                .thenAwait(Duration.ofSeconds(2))
                .expectNext("fallback")
                .verifyComplete();
    }

    @Test
    void distinctExampleTest(){
        StepVerifier.create(monoFluxOperations.distinctExample())
                .expectNext(1,2,3)
                .verifyComplete();
    }

    @Test
    void takeExampleTest(){
        StepVerifier.create(monoFluxOperations.takeExample())
                .expectNext(1,2,3)
                .verifyComplete();
    }

    @Test
    void skipExample(){
        StepVerifier.create(monoFluxOperations.skipExample())
                .expectNext(4,5,6,7,8,9,10)
                .verifyComplete();
    }

    @Test
    void skipStringExampleTest(){
        StepVerifier.create(monoFluxOperations.skipStringExample())
                .expectNext("Cranberry","Dates")
                .verifyComplete();
    }

    @Test
    void takeWhileExampleTest(){
        StepVerifier.create(monoFluxOperations.takeWhileExample())
                .expectNext(1,2,3,4) //fails at first element if the condition is false
                .verifyComplete();
    }

    @Test
    void windowExampleTest(){
        StepVerifier.create(monoFluxOperations.windowExample())
                .expectNext(List.of(1,2,3),List.of(4,5,6),List.of(7,8,9), List.of(10))
                .verifyComplete();
    }

    @Test
    void groupByTest(){
        StepVerifier.create(monoFluxOperations.groupBy())
                .expectNext("even[2, 4, 6]" ,"odd[1, 3, 5]")
                .verifyComplete();
    }

    @Test
    void mergeWithExampleTest(){
        StepVerifier.withVirtualTime(monoFluxOperations::mergeWithExample)
                .thenAwait(Duration.ofMillis(50))
                .expectNextCount(4)  //order may vary
                .verifyComplete();
    }

    @Test
    void concatWithExampleTest(){
        StepVerifier.withVirtualTime(monoFluxOperations::concatWithExample)
                .thenAwait(Duration.ofMillis(50))
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    void combineLatestExampleTest(){
        StepVerifier.create(monoFluxOperations.combineLatestExample().log())
                .expectNext("A1", "B1","C1", "C2", "C3")
                .verifyComplete();
    }

    @Test
    void startWithExampleTest(){
        StepVerifier.create(monoFluxOperations.startWithExample())
                .expectNext(0,1,2,3)
                .verifyComplete();
    }

    @Test
    void startLateTest(){
        StepVerifier.create(monoFluxOperations.startLate().log())
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Delay by 1s")
                .verifyComplete();
    }

    @Test
    void subscribeOnTest(){
        StepVerifier.create(monoFluxOperations.subscribeOn())
                .expectNext(2, 4, 6, 8, 10)
                .verifyComplete();
    }

    @Test
    void publishOnTest(){
        StepVerifier.create(monoFluxOperations.publishOn())
                .expectNext(3, 5, 7, 9, 11)
                .verifyComplete();
    }

    @Test
    void safeValueTest(){
        StepVerifier.create(monoFluxOperations.safeValue())
                .expectNext("Default")
                .verifyComplete();
    }

    @Test
    void dynamicSafeValueTest(){
        StepVerifier.create(monoFluxOperations.dynamicSafeValue())
                .expectNext("Resume")
                .verifyComplete();
    }
}