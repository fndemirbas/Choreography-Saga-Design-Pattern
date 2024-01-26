package com.fnd.stock.ms.repository;

import com.fnd.stock.ms.entity.WareHouse;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StockRepository extends CrudRepository<WareHouse, Long> {

	List<WareHouse> findByItem(String item);
}
