package site.alex_xu.dev.game.party_physics.game.engine.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

public class Package {
    private static final byte TYPE_INTEGER = 0;
    private static final byte TYPE_FRACTION = 1;
    private static final byte TYPE_STRING = 2;
    private static final byte TYPE_BOOLEAN = 3;

    private static final String[] TYPE_NAMES = {"Integer", "Fraction", "String", "Boolean"};
    private final PackageTypes type;
    TreeMap<Integer, Object> data = new TreeMap<>();
    TreeMap<Integer, Byte> types = new TreeMap<>();

    public Package(PackageTypes type) {
        this.type = type;
    }

    Package(DataInputStream stream) throws IOException {

            type = PackageTypes.values()[stream.readByte()];

            int size = stream.readByte();
            for (int i = 0; i < size; i++) {
                int hash = stream.readInt();
                byte type = stream.readByte();
                types.put(hash, type);
                if (type == TYPE_INTEGER) {
                    data.put(hash, stream.readInt());
                } else if (type == TYPE_FRACTION) {
                    data.put(hash, stream.readDouble());
                } else if (type == TYPE_STRING) {
                    int length = stream.readInt();
                    StringBuilder s = new StringBuilder();
                    for (int j = 0; j < length; j++) {
                        s.append(stream.readChar());
                    }
                    data.put(hash, s.toString());
                }
            }

    }

    void writeStream(DataOutputStream stream) {
        try {
            // Package type
            stream.writeByte(getType().ordinal());

            // Data set size
            stream.writeByte(data.size());

            // Data set (hash,type,data)
            for (Integer hash : data.keySet()) {
                byte type = types.get(hash);
                stream.writeInt(hash);
                stream.writeByte(type);
                Object value = data.get(hash);
                if (type == TYPE_INTEGER) {
                    stream.writeInt((int) value);
                } else if (type == TYPE_FRACTION) {
                    stream.writeDouble((double) value);
                } else if (type == TYPE_STRING) {
                    String s = (String) value;
                    stream.writeInt(s.length());
                    stream.writeChars(s);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PackageTypes getType() {
        return type;
    }

    public void setInteger(String key, int value) {
        int hash = key.hashCode();
        data.put(hash, value);
        types.put(hash, TYPE_INTEGER);
    }

    public void setFraction(String key, double value) {
        int hash = key.hashCode();
        data.put(hash, value);
        types.put(hash, TYPE_FRACTION);
    }

    public void setString(String key, String value) {
        int hash = key.hashCode();
        data.put(hash, value);
        types.put(hash, TYPE_STRING);
    }

    public void setBoolean(String key, boolean value) {
        int hash = key.hashCode();
        data.put(hash, value);
        types.put(hash, TYPE_BOOLEAN);
    }

    public int getInteger(String key) {
        int hash = key.hashCode();
        if (data.containsKey(hash)) {
            if (types.get(hash) == TYPE_INTEGER) {
                return (int) data.get(hash);
            } else {
                throw new RuntimeException("Type for key " + key + " was " + TYPE_NAMES[types.get(hash)]);
            }
        } else {
            throw new RuntimeException("Key " + key + " doesn't exist in the package!");
        }
    }

    public double getFraction(String key) {
        int hash = key.hashCode();
        if (data.containsKey(hash)) {
            if (types.get(hash) == TYPE_FRACTION) {
                return (double) data.get(hash);
            } else {
                throw new RuntimeException("Type for key " + key + " was " + TYPE_NAMES[types.get(hash)]);
            }
        } else {
            throw new RuntimeException("Key " + key + " doesn't exist in the package!");
        }
    }

    public String getString(String key) {
        int hash = key.hashCode();
        if (data.containsKey(hash)) {
            if (types.get(hash) == TYPE_STRING) {
                return (String) data.get(hash);
            } else {
                throw new RuntimeException("Type for key " + key + " was " + TYPE_NAMES[types.get(hash)]);
            }
        } else {
            throw new RuntimeException("Key " + key + " doesn't exist in the package!");
        }
    }

    public boolean getBoolean(String key) {
        int hash = key.hashCode();
        if (data.containsKey(hash)) {
            if (types.get(hash) == TYPE_BOOLEAN) {
                return (boolean) data.get(hash);
            } else {
                throw new RuntimeException("Type for key " + key + " was " + TYPE_NAMES[types.get(hash)]);
            }
        } else {
            throw new RuntimeException("Key " + key + " doesn't exist in the package!");
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Integer hash : data.keySet()) {
            result.append(String.format("%16d", hash)).append(": ").append(data.get(hash)).append(",\n");
        }
        return type + ": {\n" + result.substring(0, result.length() - 2) + "\n}";
    }
}