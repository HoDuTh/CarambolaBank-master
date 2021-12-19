package com.thuan.carambola.controller;

import com.thuan.carambola.repositoryprimary.PhanManhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportKHController extends ReportController {

    @Autowired
    public ReportKHController(PhanManhRepository phanManhRepository) {
        super(phanManhRepository);
    }
}