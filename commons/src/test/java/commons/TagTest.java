package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    private Event e;
    private Expense ex1;
    private Expense ex2;
    private Participant p;
    private Tag t1;
    private Tag t2;

    @BeforeEach
    public void setUp(){
        e = new Event("Title", null);
        p = new Participant("Jon Doe");
        ex1 = new Expense("Ice-Cream", 1000, null, p);
        ex2 = new Expense("Ski-Trip", 200000, null, p);
        t1 = new Tag("Food", "#008000");
        t2 = new Tag("Travel", "#FF0000");
        ex1.setExpenseTag(t1);
        ex2.setExpenseTag(t2);
        e.addExpense(ex1);
        e.addExpense(ex2);
        e.addParticipant(p);
    }
    @Test
    public void constructorTest(){
        Tag t = new Tag("Drinks", "#A020F0");
        assertEquals("Drinks", t.getTagName());
    }
    @Test
    public void setTagNameTest(){
        t1.setTagName("Other");
        assertEquals("Other", t1.getTagName());
        assertEquals("Other", ex1.getExpenseTag().getTagName());
        assertEquals("Travel", ex2.getExpenseTag().getTagName());
    }
    @Test
    public void setColorCodeTest(){
        t1.setColorCode("#FFFFFF");
        assertEquals("#FFFFFF", t1.getColorCode());
    }
    @Test
    public void equalsTest(){
        assertNotEquals(t1, t2);
    }
    @Test
    public void hashCodeTest(){
        assertNotEquals(t1.hashCode(), t2.hashCode());
        assertNotNull(t1.hashCode());
        assertNotNull(t2.hashCode());
    }
    @Test
    public void toStringTest(){
        assertEquals("Tag{id='0', tagName='Food', colorCode='#008000'}", t1.toString());
        assertEquals("Tag{id='0', tagName='Travel', colorCode='#FF0000'}", t2.toString());
    }
}