package dev.polv.polcinematics.client.renders;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BrowserView {

    private String url;
    private String customCSS = "body { background-color: rgba(0, 0, 0, 0); margin: 0px auto; overflow: hidden; }";

    public BrowserView(String url) {
        this(url, "");
    }

    public BrowserView(String url, String customCSS) {
        this.url = url;
        if (!customCSS.isEmpty()) {
            this.customCSS = customCSS;
        }
    }

}
