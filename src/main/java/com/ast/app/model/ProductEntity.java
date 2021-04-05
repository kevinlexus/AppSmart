package com.ast.app.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
public class ProductEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CUSTOMER_ID", referencedColumnName="ID")
    private CustomerEntity customer;

    @Column(name = "TITLE", length = 255)
    private String title;

    @Column(name = "DESCRIPTION", length = 1024)
    private String description;

    @Column(name = "PRICE", precision = 10, scale = 2)
    private BigDecimal price;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "IS_DELETED")
    private Boolean isDeleted;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "MODIFIED_AT")
    private Date modifiedAt;

}
