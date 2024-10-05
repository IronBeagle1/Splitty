package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ParticipantTest {
    /**
     * Test for constructor
     */
    @Test
    public void checkConstructor() {
        var p = new Participant("name");
        assertEquals("name", p.getName());
    }

    /**
     * Test for ID constructor
     */
    @Test
    public void checkIdConstructor() {
        var p = new Participant(123L, "name");
        assertEquals("name", p.getName());
        assertEquals(123L, p.getId());
    }

    /**
     * Test for name getter and setter
     */
    @Test
    public void setterGetterNameTest(){
        var p = new Participant("name");
        assertEquals("name", p.getName());
        p.setName("new name");
        assertEquals("new name", p.getName());
    }

    /**
     * Test for email getter and setter
     */
    @Test
    public void setterGetterEmailTest(){
        var p = new Participant("name");
        assertNull(p.getEmail());
        p.setEmail("email");
        assertEquals("email", p.getEmail());
    }

    /**
     * Test for bank details getter and setter
     */
    @Test
    public void setterGetterBankTest(){
        String name = "L. Egal Name";
        String iban = "NL02ABNA0123456789";
        String bic = "ABNANL2AXXX";
        var p = new Participant("Name");

        p.setLegalName(name);
        p.setIban(iban);
        p.setBic(bic);

        assertEquals(name, p.getLegalName());
        assertEquals(iban, p.getIban());
        assertEquals(bic, p.getBic());
    }

    /**
     * Test for bank details checker with all data
     */
    @Test
    public void availableBankTest(){
        String name = "L. Egal Name";
        String iban = "NL02ABNA0123456789";
        String bic = "ABNANL2AXXX";
        var p = new Participant("Name");

        p.setLegalName(name);
        p.setIban(iban);
        p.setBic(bic);

        assertTrue(p.hasBankAccount());
    }

    /**
     * Test for bank details checker with data missing
     */
    @Test
    public void unavailableBankTest(){
        String name = "";
        String iban = "NL02ABNA0123456789";
        String bic = "ABNANL2AXXX";
        var p = new Participant("Name");

        p.setLegalName(name);
        p.setIban(iban);
        p.setBic(bic);

        assertFalse(p.hasBankAccount());
    }
    /**
     * Equality checker for equal expenses
     */
    @Test
    public void equalsHashCode() {
        var a = new Participant("name");
        var b = new Participant("name");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
    /**
     * Equality checker for unequal expenses
     */
    @Test
    public void notEqualsHashCode() {
        var a = new Participant("name");
        var b = new Participant("new name");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }
    /**
     * Tests for toString
     */
    @Test
    public void hasToString() {
        var actual = new Participant("name").toString();
        assertTrue(actual.contains("id"));
    }
}
