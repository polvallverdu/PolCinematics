package dev.polv.polcinematics.exception;

import dev.polv.polcinematics.client.ClientModules;

public class MissingModuleException extends RuntimeException {

    public final ClientModules missingModule;

    public MissingModuleException(ClientModules missingModule) {
        super("Module " + missingModule.name + " is not installed. Please install it from " + missingModule.url + " and try again.");

        this.missingModule = missingModule;
    }

}
