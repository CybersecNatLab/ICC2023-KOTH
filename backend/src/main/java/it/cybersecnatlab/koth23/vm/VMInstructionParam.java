package it.cybersecnatlab.koth23.vm;

public final class VMInstructionParam<T> {
    private final T value;

    public VMInstructionParam(T value) {
        this.value = value;
    }

    public Integer getIntegerValue() throws VMException {
        if (value == null) {
            return null;
        }
        if (value instanceof VMRegistry) {
            return ((VMRegistry) value).getInteger();
        }
        if (value instanceof Integer) {
            return (Integer) this.value;
        }
        if (value instanceof Float) {
            return ((Float) this.value).intValue();
        }
        if (value instanceof Boolean) {
            return ((Boolean) this.value) ? 1 : 0;
        }
        throw new VMException("Cannot get Integer param");
    }

    public Float getFloatValue() throws VMException {
        if (value == null) {
            return null;
        }
        if (value instanceof VMRegistry) {
            return ((VMRegistry) value).getFloat();
        }
        if (value instanceof Float) {
            return (Float) this.value;
        }
        if (value instanceof Integer) {
            return ((Integer) this.value).floatValue();
        }
        if (value instanceof Boolean) {
            return ((Boolean) this.value) ? 1f : 0f;
        }
        throw new VMException("Cannot get Float param");
    }

    public Boolean getBooleanValue() throws VMException {
        if (value == null) {
            return null;
        }
        if (value instanceof VMRegistry) {
            return ((VMRegistry) value).getBoolean();
        }
        if (value instanceof Boolean) {
            return (Boolean) this.value;
        }
        if (value instanceof Integer) {
            return (Integer) this.value != 0;
        }
        if (value instanceof Float) {
            return (Float) this.value != 0F;
        }
        throw new VMException("Cannot get Boolean param");
    }

    public String getStringValue() throws VMException {
        if (value == null) {
            return null;
        }
        if (value instanceof VMRegistry) {
            return ((VMRegistry) value).getString();
        }
        if (value instanceof String) {
            return (String) this.value;
        } else {
            return value.toString();
        }
    }
}
