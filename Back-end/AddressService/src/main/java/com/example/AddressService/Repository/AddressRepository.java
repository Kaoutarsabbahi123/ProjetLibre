package com.example.AddressService.Repository;

import com.example.AddressService.Entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByFkIdcontact(Long fkIdcontact);
}
