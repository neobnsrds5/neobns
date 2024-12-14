package com.spider.demo.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MathUtilsTest {

    @Test
    void testFactorial() {
        System.out.println("[TEST] 팩토리얼 계산 기능 테스트");
        assertEquals(120, MathUtils.factorial(5));
        assertEquals(1, MathUtils.factorial(0));
        assertThrows(IllegalArgumentException.class, () -> MathUtils.factorial(-1));
    }

    @Test
    void testIsPrime() {
        System.out.println("[TEST] 소수 여부 확인 기능 테스트");
        assertTrue(MathUtils.isPrime(7));
        assertFalse(MathUtils.isPrime(10));
        assertFalse(MathUtils.isPrime(1));
    }

    @Test
    void testGCD() {
        System.out.println("[TEST] 최대공약수 계산 기능 테스트");
        assertEquals(6, MathUtils.gcd(54, 24));
        assertEquals(1, MathUtils.gcd(17, 13));
    }

    @Test
    void testLCM() {
        System.out.println("[TEST] 최소공배수 계산 기능 테스트");
        assertEquals(72, MathUtils.lcm(24, 18));
        assertEquals(0, MathUtils.lcm(0, 18));
    }

    @Test
    void testSquareRoot() {
        System.out.println("[TEST] 제곱근 계산 기능 테스트");
        assertEquals(5.0, MathUtils.squareRoot(25), 0.0001);
        assertEquals(0.0, MathUtils.squareRoot(0), 0.0001);
        assertThrows(IllegalArgumentException.class, () -> MathUtils.squareRoot(-4));
    }

    @Test
    void testRandomInt() {
        System.out.println("[TEST] 랜덤 정수 생성 기능 테스트");
        int min = 1, max = 10;
        for (int i = 0; i < 100; i++) {
            int random = MathUtils.randomInt(min, max);
            assertTrue(random >= min && random <= max);
        }
        assertThrows(IllegalArgumentException.class, () -> MathUtils.randomInt(10, 1));
    }

    @Test
    void testFibonacci() {
        System.out.println("[TEST] 피보나치 수열 계산 기능 테스트");
        assertEquals(0, MathUtils.fibonacci(0));
        assertEquals(1, MathUtils.fibonacci(1));
        assertEquals(21, MathUtils.fibonacci(8));
        assertThrows(IllegalArgumentException.class, () -> MathUtils.fibonacci(-1));
    }
}
