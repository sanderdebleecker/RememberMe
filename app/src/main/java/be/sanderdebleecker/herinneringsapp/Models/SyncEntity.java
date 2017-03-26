package be.sanderdebleecker.herinneringsapp.Models;

public class SyncEntity {
    //* Type of entity : 0 Memory, 1 Album, 2 Session, 3 User, 4 Timeline, 5 Trust
    public enum Types {
        Memory,Album,Session,User,Timeline,Trust
    }
    public String uuid;
    public Types type;
    public SyncEntity() {
    }
    public SyncEntity(String uuid,Types entityType){
        this.uuid=uuid;
        this.type=entityType;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public void setUuid(byte[] uuid) { this.uuid = getUuidFromByteArray(uuid); }
    public Types getType() {
        return type;
    }
    public void setType(Types type) {
        this.type = type;
    }
    public void setType(int type) {
        this.type = Types.values()[type];
    }

    public static String getUuidFromByteArray(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for(int i=0; i<bytes.length; i++) {
            buffer.append(String.format("%02x", bytes[i]));
        }
        return buffer.toString();
    }
}
