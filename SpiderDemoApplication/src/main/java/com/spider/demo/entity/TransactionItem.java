package com.spider.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_item")
public class TransactionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    private Integer price;

    public TransactionItem() {}
    public TransactionItem(String name, LocalDate creationDate, Integer price) {
		this.name = name;
		this.creationDate = creationDate;
		this.price = price;
	}
    
    public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", creationDate=" + creationDate + ", price=" + price
				+ "]";
	}

}
