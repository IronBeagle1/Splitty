package client;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;

public class TestObservableUtils {
    /**
     * Creates an observable string from a string
     * @param string String
     * @return Observable string
     */
    public static ObservableValue<String> stringToObservable(String string){
        return Bindings.createStringBinding(()-> string);
    }
}
