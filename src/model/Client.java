package model;

public class Client {
    private static int idCounter = 1;

    private final int id;
    private String name;
    private String phone;
    private String email;

    public Client(String name, String phone, String email) {
        this.id = idCounter++;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("Client{id=%d, name='%s', phone='%s', email='%s'}", id, name, phone, email);
    }
}
