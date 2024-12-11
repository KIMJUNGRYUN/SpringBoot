package mysite.expense.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Entity
@Table(name = "tbl_expenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String expenseId;
    private String name;
    private String description;
    private Long amount;
    private Date date;
    @ManyToOne
    private User user; //유저 1명이 여러 비용사용
}
