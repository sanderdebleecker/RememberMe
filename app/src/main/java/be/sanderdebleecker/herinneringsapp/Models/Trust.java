package be.sanderdebleecker.herinneringsapp.Models;

public class Trust {
    public static class Party extends Entity {
        private String name;
        public Party(String identifier) {
            this.setUuid(identifier);
        }
        public Party(String identifier,String name) {
            this.setUuid(identifier);
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    public enum TrustRelations {
        MUTUAL,
        REQUESTED,
        RECEIVED,
    };
    private Party a;
    private Party b;
    private TrustRelations rel;

    public Trust() {

    }
    public Trust(Party a,Party b) {
        this.a = a;
        this.b = b;
    }
    public Trust(Party a,Party b,TrustRelations rel) {
        this.a = a;
        this.b = b;
        this.rel = rel;
    }

    public Party getA() {
        return a;
    }
    public void setA(Party a) {
        this.a = a;
    }
    public Party getB() {
        return b;
    }
    public void setB(Party b) {
        this.b = b;
    }
    public TrustRelations getRel() {
        return rel;
    }
    public void setRel(TrustRelations rel) {
        this.rel = rel;
    }
}
