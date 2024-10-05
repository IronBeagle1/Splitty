package client.utils;

import client.scenes.SimpleRefreshable;
import javafx.scene.Scene;

public record ScreenInfo(SimpleRefreshable controller, boolean shouldLiveRefresh, Scene scene, String titleBinding) {
}
