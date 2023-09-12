package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeReportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeReportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reporting-structure";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, new HttpEntity<Employee>(readEmployee, headers), Employee.class, readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    /*
        Left alone for this exercise, but thought about adding the /static/employee_database.json file to test resources
        to keep test and live data separate but left alone for this exercise.
    */
    @Test
    public void givenEmployeeId_whenReportingStructureCalledOfTopManager_thenReturnReportingStructureWithAllCounts() {
        Employee managerWith4Reports = employeeRepository.findByEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        ReportingStructure managerReportingStructure = restTemplate.getForEntity(employeeReportingStructureUrl, ReportingStructure.class, managerWith4Reports.getEmployeeId()).getBody();
        assertEquals(managerReportingStructure.getEmployee().getEmployeeId(), managerWith4Reports.getEmployeeId());
        assertEquals(managerReportingStructure.getNumberOfReports(), Integer.valueOf(4).intValue());
    }

    @Test
    public void givenEmployeeId_whenReportingStructureCalledOfMidManager_thenReturnReportingStructureWithAllCounts() {
        Employee midManagerWith2Reports = new Employee();
        midManagerWith2Reports.setEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        ReportingStructure managerReportingStructure = restTemplate.getForEntity(employeeReportingStructureUrl, ReportingStructure.class, midManagerWith2Reports.getEmployeeId()).getBody();
        assertEquals(managerReportingStructure.getEmployee().getEmployeeId(), midManagerWith2Reports.getEmployeeId());
        assertEquals(managerReportingStructure.getNumberOfReports(), Integer.valueOf(2).intValue());
    }

    @Test
    public void givenEmployeeId_whenReportingStructureCalledOfIC_thenReturnReportingStructureWithNoCounts() {
        Employee individualContributor = new Employee();
        individualContributor.setEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        ReportingStructure managerReportingStructure = restTemplate.getForEntity(employeeReportingStructureUrl, ReportingStructure.class, individualContributor.getEmployeeId()).getBody();
        assertEquals(managerReportingStructure.getEmployee().getEmployeeId(), individualContributor.getEmployeeId());
        assertEquals(managerReportingStructure.getNumberOfReports(), 0);
    }

    @Test
    public void givenInvalidEmployee_whenFindingReportingStructureForInvalidEmployeeId_thenThrowError() {

        Employee invalidEmployee = new Employee();
        invalidEmployee.setEmployeeId("UnknownUser");
        try {
            ReportingStructure empployeeReportingStructure = restTemplate.getForEntity(employeeReportingStructureUrl,
                    ReportingStructure.class, invalidEmployee.getEmployeeId()).getBody();
        } catch (Throwable e) {
            assertThat(e.getMessage(), is("Invalid employeeId: " + invalidEmployee.getEmployeeId()));
            assertThat(e.getCause(), instanceOf(RuntimeException.class));
        }
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
