package com.fnd.order.ms.repository;

import com.fnd.order.ms.entity.OrderTable;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderTable, Long> {
}
