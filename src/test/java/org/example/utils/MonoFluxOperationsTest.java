package org.example.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

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


}