package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import javafx.scene.control.PasswordField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ManagementOverviewPasswordCtrlTest {
    private Translation translation;
    private ServerUtils server;
    private TestManagementOverviewPasswordCtrl sut;
    private MainCtrl mainCtrl;
    @BeforeEach
    public void setUp(){
        translation = mock(Translation.class);
        server = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);
        sut = new TestManagementOverviewPasswordCtrl(server, mainCtrl, translation);
    }
    @Test
    void logInCheckValid() {
        sut.textBoxText = "password";
        when(server.checkPassword("password")).thenReturn(true);
        sut.logInCheck();
        //should only contain a binding to empty
        verify(translation).bindLabel(null, "Empty");
        assertTrue(sut.textBoxText.isEmpty());
    }

    @Test
    void logInCheckNull() {
        sut.textBoxText = null;
        when(server.checkPassword(anyString())).thenReturn(true);
        sut.logInCheck();
        verify(translation).bindLabel(null, "MOPCtrl.Log.In.Feedback");
        assertTrue(sut.textBoxText.isEmpty());
    }

    @Test
    void logInCheckInvalidPassword() {
        sut.textBoxText = "invalid";
        when(server.checkPassword("invalid")).thenReturn(false);
        sut.logInCheck();
        verify(translation).bindLabel(null, "MOPCtrl.Log.In.Feedback");
        assertTrue(sut.textBoxText.isEmpty());
    }

    @Test
    void logInCheckEmptyPassword() {
        sut.textBoxText = "";
        when(server.checkPassword(anyString())).thenReturn(true);
        assertEquals(0, mockingDetails(translation).getInvocations().size());
        sut.logInCheck();
        verify(translation).bindLabel(null, "MOPCtrl.Log.In.Feedback");
        assertTrue(sut.textBoxText.isEmpty());
    }

    @Test
    void goBackToMainScreen() {
        sut.goBackToMain();
        assertEquals(1, mockingDetails(mainCtrl).getInvocations().size());
    }

    @Test
    void intializeTest(){
        sut.initialize(null, null);
        assertEquals(3, mockingDetails(translation).getInvocations().size());
    }


    private class TestManagementOverviewPasswordCtrl extends ManagementOverviewPasswordCtrl {
        public String textBoxText;
        public TestManagementOverviewPasswordCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation) {
            super(server, mainCtrl, translation);
        }

        @Override
        public String getPasswordFieldText(PasswordField passwordField){
            return textBoxText;
        }

        @Override
        public void clearPasswordField(){
            textBoxText = "";
        }
    }
}