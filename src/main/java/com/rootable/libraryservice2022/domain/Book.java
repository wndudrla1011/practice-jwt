package com.rootable.libraryservice2022.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Book extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;
    private String writer;
    private Integer price;
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Posts posts;

    @Builder
    public Book(String title, String writer, Integer price, Integer stock, Status status) {
        this.title = title;
        this.writer = writer;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public void update(Integer price, Integer stock, Status status) {
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

}
