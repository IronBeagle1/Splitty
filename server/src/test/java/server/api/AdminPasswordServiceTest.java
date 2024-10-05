package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AdminPasswordServiceTest {
    AdminPasswordService sut;
    TestRandom random;
    @BeforeEach
    void setup(){
        random = new TestRandom();
        sut = new AdminPasswordService(random);
    }

    @Test
    void randomPasswordTestOneChar(){
        random.addNext(0, 20);
        assertEquals("aaaaaaaaaaaaaaaaaaaa", sut.generatePassword());
    }

    @Test
    void randomPasswordTestDifferentChars(){
        random.addNext(32);
        random.addNext(40, 18);
        random.addNext(29);
        assertEquals("GOOOOOOOOOOOOOOOOOOD", sut.generatePassword());
    }

    @Test
    void verifyPasswordCorrectTest(){
        random.addNext(32);
        random.addNext(40, 18);
        random.addNext(29);

        CommandLineRunner result = sut.initPassword();
        try {
            result.run();
        } catch (Exception e) {
           fail();
        }
        assertTrue(sut.passwordChecker("GOOOOOOOOOOOOOOOOOOD"));
    }

    @Test
    void verifyPasswordIncorrectTest(){
        random.addNext(32);
        random.addNext(40, 18);
        random.addNext(29);

        CommandLineRunner result = sut.initPassword();
        try {
            result.run();
        } catch (Exception e) {
            fail();
        }
        assertFalse(sut.passwordChecker("BAAAAAAAAAAAAAAAAAAD"));
    }

    private static class TestRandom extends Random{
        public List<Integer> nextRandoms = new LinkedList<>();
        @Override
        public int nextInt(int bound) {
            return nextRandoms.removeFirst();
        }

        public void addNext(int next){
            nextRandoms.add(next);
        }

        public void addNext(int next, int times){
            for(int i = 0; i < times; i++){
                nextRandoms.add(next);
            }
        }
    }
}