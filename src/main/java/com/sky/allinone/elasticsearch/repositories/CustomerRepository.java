package com.sky.allinone.elasticsearch.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.sky.allinone.elasticsearch.entity.Customer;

public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {

    public List<Customer> findByFirstName(String firstName);

    public List<Customer> findByLastName(String lastName);

}
