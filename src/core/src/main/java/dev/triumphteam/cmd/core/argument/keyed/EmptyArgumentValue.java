package dev.triumphteam.cmd.core.argument.keyed;

final class EmptyArgumentValue implements ArgumentValue {

    static final EmptyArgumentValue INSTANCE = new EmptyArgumentValue();

    @Override
    public String toString() {
        return "Empty{}";
    }
}