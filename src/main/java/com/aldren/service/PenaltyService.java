package com.aldren.service;

import com.aldren.repository.BorrowedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PenaltyService {

    private BorrowedRepository borrowedRepository;

    public PenaltyService(@Autowired BorrowedRepository borrowedRepository) {
        this.borrowedRepository = borrowedRepository;
    }

}
