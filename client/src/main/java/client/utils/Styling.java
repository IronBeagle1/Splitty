package client.utils;

import javafx.scene.Node;

public class Styling {

    /**
     * How to use:
     * Go in main.css and make the style you want e.g:
     * .backgroundLight{ -fx-background-color: #FFFFFF; }
     * Now lets say I have a button that I made (myButton) and I want to apply the backgroundLight styling to it. Using the Styling class:
     * Styling.applyStyling(myButton, backgroundLight)
     * The button should now have the css property -fx-background-color: #FFFFFF
     * Now lets say I want to alternate between backgroundLight and another css class e.g. backgroundDark.
     * Simply doing Styling.applyStyling(myButton, backgroundDark) will not work since myButton already has the class backgroundLight and that one
     * takes priority.
     * In this case use Styling.changeStyling(myButton, backgroundLight, backgroundDark)
     * which replaces the backgroundLight styling with backgroundDark.
     */

    /**
     * Applies a styling to a node
     * Be careful as the new styling may be overridden by the old styling. In this case use changeStyling
     * @param node the node to apply the styling to
     * @param style the style to apply
     */
    public void applyStyling(Node node, String style) {
        if (node != null) {
            if (!node.getStyleClass().contains(style)) {
                node.getStyleClass().add(style);
            }
        }
    }

    /**
     * Changes the styling of a node
     * @param node the node to change the styling of
     * @param oldStyle the old style to remove
     * @param newStyle the new style to add
     */
    public void changeStyling(Node node, String oldStyle, String newStyle) {
        if (node != null) {
            node.getStyleClass().remove(oldStyle);
            applyStyling(node, newStyle);
        }
    }

    /**
     * Removes a styling from a node
     * @param node the node to remove the styling from
     * @param style the style to remove
     */
    public void removeStyling(Node node, String style) {
        if (node != null) {
            node.getStyleClass().remove(style);
        }
    }

}
