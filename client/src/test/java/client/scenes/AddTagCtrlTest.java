package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import commons.Event;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AddTagCtrlTest {
    private AddTagCtrl tagCtrl;
    private ServerUtils serverUtils;
    private MainCtrl mainCtrl;
    private  Translation translation;

    @BeforeEach
    void setup() {
        serverUtils = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);
        translation = mock(Translation.class);
        tagCtrl = new AddTagCtrl(serverUtils, mainCtrl, translation);
    }

    /**
     * IDs should be set
     */
    @Test
    void fillInput() {
        Tag tag = new Tag();
        tagCtrl.setIds(tag, 15L);
        assertEquals(0, tagCtrl.getTagId());
        assertEquals(15, tagCtrl.getExpenseId());
    }

    /**
     * If tag is being edited, then tagId and expenseId should be set to null.
     * Also will you switch back to the expense screen with filled in input.
     */
    @Test
    void tagIsEdited() {
        Event event = new Event("Holiday", new Date());
        tagCtrl.setTagId(15L);
        tagCtrl.setExpenseId(5L);
        tagCtrl.switchScreens(new Tag(), event);
        assertEquals(null, tagCtrl.getTagId());
        assertEquals(null, tagCtrl.getExpenseId());
        verify(mainCtrl).switchToEditExpense(5L);
    }

    /**
     * If a new tag is being created then switch back to the event overview.
     */
    @Test
    void tagIsCreated() {
        tagCtrl.switchScreens(new Tag(), null);
        verify(mainCtrl).switchScreens(EventScreenCtrl.class);
    }
}