package com.example.librarty_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "janretb")
@Data
public class Genre {
    
    @Id
    private String janreId; // janre_id (String型で '001' などの形式に対応)
    
    private String janreName; // janre_name
}