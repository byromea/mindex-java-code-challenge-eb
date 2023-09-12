package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;

    private String compensationByEmployeeIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation/employees";
        compensationByEmployeeIdUrl = "http://localhost:" + port + "/compensation/employees/{id}";
    }

    @Test
    public void givenValidEmployee_whenCreatingEmployeeCompensation_thenCreateAndReturnEmployeeCompensation() {
        Compensation testCompensation = getTestCompensation();
        Employee employee = new Employee();
        employee.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        testCompensation.setEmployee(employee);

        // Create checks
        Compensation createCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        //Verify Compensation Created
        verifyCompensation(testCompensation, createCompensation);
    }

    @Test
    public void givenInvalidEmployee_whenAddingNonExistingEmployeeCompensation_thenThrowError() {
        Compensation testCompensation = getTestCompensation();
        Employee employee = new Employee();
        employee.setEmployeeId("UnknownUser");
        testCompensation.setEmployee(employee);
        try {
            // Create checks
            Compensation createCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();
            // fail("Exception should be thrown");
        } catch (Throwable e) {
            assertThat(e.getMessage(), is("Invalid employeeId: " + employee.getEmployeeId()));
            assertThat(e.getCause(), instanceOf(RuntimeException.class));
        }
    }
    @Test
    public void givenInvalidEmployee_whenFindingNonExistingEmployeeCompensation_thenThrowError() {
        Compensation testCompensation = getTestCompensation();
        Employee employee = new Employee();
        employee.setEmployeeId("UnknownUser");
        testCompensation.setEmployee(employee);
        try {
            // Create checks
            Compensation findCompensation = restTemplate.getForEntity(compensationByEmployeeIdUrl, Compensation.class, testCompensation.getEmployee().getEmployeeId()).getBody();
            // fail("Exception should be thrown");
        } catch (Throwable e) {
            assertThat(e.getMessage(), is("Invalid employeeId: " + employee.getEmployeeId()));
            assertThat(e.getCause(), instanceOf(RuntimeException.class));
        }
    }

    @Test
    public void givenValidEmployee_whenCreatingEmployeeCompensationSuccess_thenCanFindEmployeeCompensation() {
        Compensation testCompensation = getTestCompensation();
        Employee employee = new Employee();
        employee.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        testCompensation.setEmployee(employee);

        // Create checks
        Compensation createCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        //Verify Compensation Created
        verifyCompensation(testCompensation, createCompensation);

        Compensation findCompensation = restTemplate.getForEntity(compensationByEmployeeIdUrl, Compensation.class, testCompensation.getEmployee().getEmployeeId()).getBody();
        ;
        //Verify Compensation Created
        verifyCompensation(testCompensation, findCompensation);
    }
    private void verifyCompensation(Compensation expected, Compensation actual) {
        assertNotNull(actual.getCompensationId());
        assertThat(expected.getEffectiveDate().isEqual(actual.getEffectiveDate()), is(true));
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
    }
    private Compensation getTestCompensation() {
        Compensation testCompensation = new Compensation();
        testCompensation.setEffectiveDate(LocalDate.now().minusDays(7));
        testCompensation.setSalary(BigDecimal.valueOf(12500.50).setScale(2, RoundingMode.HALF_EVEN));
        return testCompensation;
    }
}
