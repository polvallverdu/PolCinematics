package dev.polv.polcinematics.exception;

import dev.polv.polcinematics.client.EClientModules;

public class MissingModuleException extends RuntimeException {

    public final EClientModules missingModule;

    public MissingModuleException(EClientModules missingModule) {
        super("Module " + missingModule.name + " is not installed. Please install it from " + missingModule.url + " and try again.");

        this.missingModule = missingModule;
    }

}
