package dev.rosewood.rosedisplays.model;

public record Vector3(float x,
                      float y,
                      float z) {

    public static String toString(Vector3 vector3) {
        return String.format("%.2f %.2f %.2f", vector3.x, vector3.y, vector3.z);
    }

    public static Vector3 parseVector3(String string) {
        String[] parts = string.split(" ");
        return new Vector3(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
    }

}
