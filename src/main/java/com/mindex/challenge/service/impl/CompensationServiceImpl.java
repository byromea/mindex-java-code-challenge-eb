package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Compensation create(Compensation compensation) {

        LOG.debug("Creating compensation [{}]", compensation);
        //Not sure if this needed with the MongoDB entity relationship.
        Employee employee = employeeService.read(compensation.getEmployee().getEmployeeId());
        compensation.setEmployee(employee);
        compensation.setCompensationId(UUID.randomUUID().toString());
        return compensationRepository.insert(compensation);
    }

    @Override
    public Compensation findByEmployeeId(String id) {
        LOG.debug("Finding compensation by employee id [{}]", id);
        Employee employee = employeeService.read(id);
        return compensationRepository.findByEmployee_EmployeeId(employee.getEmployeeId());
    }
}
