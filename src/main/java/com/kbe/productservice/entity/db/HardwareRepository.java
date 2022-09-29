package com.kbe.productservice.entity.db;

import com.kbe.productservice.entity.Hardware;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HardwareRepository extends CrudRepository<Hardware, Integer> {
}
