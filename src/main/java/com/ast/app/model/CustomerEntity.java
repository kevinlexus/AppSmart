package com.ast.app.model;

import com.ast.app.dto.Product;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class CustomerEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products = new ArrayList<>();

    @Column(name = "TITLE", length = 255)
    private String title;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "IS_DELETED")
    private Boolean isDeleted;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "MODIFIED_AT")
    private Date modifiedAt;

    public void addProductEntity(ProductEntity productEntity) {
        productEntity.setCustomer(this);
        products.add(productEntity);
    }

}
