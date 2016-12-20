package be.sanderdebleecker.herinneringsapp.Models;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String q1,q2,a1,a2;



    //GETSET
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getQ1() {
        return q1;
    }
    public void setQ1(String q1) {
        this.q1 = q1;
    }

    public String getQ2() {
        return q2;
    }
    public void setQ2(String q2) {
        this.q2 = q2;
    }

    public String getA1() {
        return a1;
    }
    public void setA1(String a1) {
        this.a1 = a1;
    }

    public String getA2() {
        return a2;
    }
    public void setA2(String a2) {
        this.a2 = a2;
    }
}
