package user;


public class User {
    private Integer id;
    String name;
    String email;
    Integer birthYear;

    public User() {
        super();
    }
    public User(Integer id, String name, String email, Integer birthYear) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthYear = birthYear;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Integer getBirthYear() {
        return birthYear;
    }
    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }



}