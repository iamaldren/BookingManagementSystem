package com.aldren.repository;

import com.aldren.entity.Borrowed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowedRepository extends CrudRepository<Borrowed, String> {

    List<Borrowed> findByUserIdAndReturnedDateNotNull(String userId);

}
