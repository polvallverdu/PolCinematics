package engineer.pol.cinematic;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.Cinematic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientCinematicManager {

    private Cinematic loadedCinematic = null;

    public ClientCinematicManager() {

    }

    public void loadCinematic(JsonObject json) {

    }

}

