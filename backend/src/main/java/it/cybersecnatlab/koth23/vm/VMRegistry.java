package it.cybersecnatlab.koth23.vm;

import java.util.Map;

public class VMRegistry {
    private Object data;
    private final String name;

    public VMRegistry(String name) {
        this.data = null;
        this.name = name;
    }

    public void setData(Object data) throws VMException {
        if (data == null || data instanceof Integer || data instanceof Float || data instanceof Boolean || data instanceof String || data instanceof Map<?, ?>) {
            this.data = data;
            return;
        }

        throw new VMException("Cannot store in registry type " + data.getClass().getName());
    }

    public Integer getInteger() throws VMException {
        if (data == null) {
            return null;
        }
        if (data instanceof Integer) {
            return (Integer) data;
        }
        if (data instanceof Float) {
            return ((Float) data).intValue();
        }
        if (data instanceof Boolean) {
            return ((Boolean) data) ? 1 : 0;
        }
        throw new VMException("Cannot get Integer from registry " + this.name);
    }

    public Float getFloat() throws VMException {
        if (data == null) {
            return null;
        }
        if (data instanceof Integer) {
            return ((Integer) data).floatValue();
        }
        if (data instanceof Float) {
            return ((Float) data);
        }
        if (data instanceof Boolean) {
            return ((Boolean) data) ? 1f : 0f;
        }
        throw new VMException("Cannot get Float from registry " + this.name);
    }

    public Boolean getBoolean() throws VMException {
        if (data == null) {
            return null;
        }
        if (data instanceof Integer) {
            return ((Integer) data) != 0;
        }
        if (data instanceof Float) {
            return ((Float) data) != 0f;
        }
        if (data instanceof Boolean) {
            return ((Boolean) data);
        }
        throw new VMException("Cannot get Boolean from registry " + this.name);
    }

    public String getString() throws VMException {
        if (data == null) {
            return null;
        }
        if (data instanceof String) {
            return (String) data;
        } else {
            return data.toString();
        }
    }

    public Object getObject() {
        return this.data;
    }
}
