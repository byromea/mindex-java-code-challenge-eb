package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);
    @Autowired
    private CompensationService compensationService;

    /* Could have these methods use ResponseEntity to return the object and other status codes like 404
     if lookup is not found. */
    @PostMapping("/compensation/employees")
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Creating compensation for employee id [{}]", compensation.getEmployee().getEmployeeId());

        return compensationService.create(compensation);
    }

    @GetMapping("/compensation/employees/{id}")
    public Compensation read(@PathVariable String id) {
        LOG.debug("Reading compensation for employee id [{}]", id);

        return compensationService.findByEmployeeId(id);
    }
}
