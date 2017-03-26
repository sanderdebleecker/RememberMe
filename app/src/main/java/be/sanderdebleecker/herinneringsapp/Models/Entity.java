package be.sanderdebleecker.herinneringsapp.Models;

/**
 * Created by Sander De Bleecker on 24/03/2017.
 */

/**
 * Base class with identifier
 */
public abstract class Entity {
    private String uuid;

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
