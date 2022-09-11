package user;

import lombok.Value;

@Value
public class User {
    Integer id;
    String name;
    String email;
    Integer birthYear;
}