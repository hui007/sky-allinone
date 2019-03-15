package com.sky.allinone.elasticsearch.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.sky.allinone.elasticsearch.entity.Customer;

@NoRepositoryBean
public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {

    public List<Customer> findByFirstName(String firstName);

    public List<Customer> findByLastName(String lastName);

}
