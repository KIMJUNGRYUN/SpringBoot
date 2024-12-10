package mysite.expense.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(unique = true)
    private String userId; // 유저아이디 유니크 (중복 x)

    private String name;

    @Column(unique = true)
    private String email; // 이메일 유니크 (중복 x)

    private String password;
}
