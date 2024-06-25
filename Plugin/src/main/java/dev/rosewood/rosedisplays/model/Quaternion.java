package dev.rosewood.rosedisplays.model;

public record Quaternion(float x,
                         float y,
                         float z,
                         float w) {

    public static String toString(Quaternion vector3) {
        return String.format("%.2f %.2f %.2f %.2f", vector3.x, vector3.y, vector3.z, vector3.w);
    }

    public static Quaternion parseQuaternion(String string) {
        String[] parts = string.split(" ");
        return new Quaternion(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
    }

}
