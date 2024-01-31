package dev.polv.polcinematics.client;

public enum EClientModules {

//    MEDIA_PLAYER("fancyvideo-api", "https://github.com/polvallverdu/FancyVideo-API-PolCinematics"),
    BROWSER("mcef", "https://github.com/polvallverdu/mcef-PolCinematics"),
    ;

    public final String name;
    public final String url;

    EClientModules(String name, String url) {
        this.name = name;
        this.url = url;
    }

}
