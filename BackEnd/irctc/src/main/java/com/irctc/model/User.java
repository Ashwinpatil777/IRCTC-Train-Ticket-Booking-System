package com.irctc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false, length = 50)
    private String fullname;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @Column(nullable = false, length = 10)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @Column(nullable = false, length = 20)
    private Role role;

	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}
}