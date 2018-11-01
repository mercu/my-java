package com.mercu.bricklink.repository;

import com.mercu.bricklink.model.my.MyItem;
import org.springframework.data.repository.CrudRepository;

public interface MyItemRepository extends CrudRepository<MyItem, String> {

}
