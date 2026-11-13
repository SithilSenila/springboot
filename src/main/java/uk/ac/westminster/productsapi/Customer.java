package uk.uc.westminster.productsapi;

public class Customers{

    private Long id;
    private String name;
    private String email;
    private Addres address;

    public Customers(){

    }
    public Customers(Long id. String name, String email, Address address){
        this.idid = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Addres getAddress() {
        return address;
    }
}