package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(Employee employee) {
        LOG.debug("Getting reporting structure for employee [{}]", employee);

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        int numberOfReports = getEmployeeReportCounts(employee);
        reportingStructure.setNumberOfReports(numberOfReports);
        return reportingStructure;
    }

    private int getEmployeeReportCounts(Employee employee) {
        /*
            For the purposes of this exercise, just got all employees to build the map. I'm not too familiar
            with MongoDB.  If this was a relational DB, I'd try to only return the employee's in the heirarchy.  Do
            as much work on the DB server and limit the result set as much as possible to be returned.  If oracle I'd
            write SQL and use "connect by prior" or if MySQL write a SQL CTE (common table expression) to return
            results in a parent/child format.

            Doing a little bit of research looks like you can achieve similar querying mongo using aggregate lookups.
            I'd use the aggregate annotations or create a repository implementation using the mongo template.

            https://dzone.com/articles/manage-hierarchical-data-in-mongodb-with-spring
            https://www.mongodb.com/docs/manual/reference/operator/aggregation/graphLookup/?_ga=2.75265298.1216392104.1694202906-1982505371.1694202906#within-a-single-collection
         */
        Map<String, Set<String>> managerReportsMap = getAllReportsMap(employeeRepository.findAll());
        return getDirectReportCount(employee.getEmployeeId(), managerReportsMap);
    }

    /* Gets the count of any employee's direct reports, then recursively traverses down to count the direct
    reports any direct reports of this employee */
    private int getDirectReportCount(String employeeId, Map<String, Set<String>> managerReportsMap) {
        int reportsCount = 0;
        Set<String> directReports = managerReportsMap.get(employeeId);
        if (directReports != null && !directReports.isEmpty()) {
            //Add the direct reports count
            reportsCount = directReports.size();
            //recursively go get the counts of any direct reports under this employee
            for (String report : directReports) {
                int counts = getDirectReportCount(report, managerReportsMap);
                reportsCount = reportsCount + counts;
            }
        }
        return reportsCount;
    }

    /*
    Create a map to be able to recursively lookup.  Key is employeeId, values are a set of the direct
    report employeeIds
 */
    private Map<String, Set<String>> getAllReportsMap(List<Employee> employees) {
        Map<String, Set<String>> reportsMap = new HashMap<>();
        Set<String> directReports;
        for (Employee employee : employees) {
            if (employee.getDirectReports() != null && !employee.getDirectReports().isEmpty()) {
                directReports = employee.getDirectReports().stream().map(Employee::getEmployeeId).collect(Collectors.toSet());
            } else {
                directReports = new HashSet<>();
            }
            reportsMap.put(employee.getEmployeeId(), directReports);
        }
        return reportsMap;
    }
}
