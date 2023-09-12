package com.mindex.challenge.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Compensation {

    private String compensationId;
    private Employee employee;
    private LocalDate effectiveDate;

    @JsonDeserialize(using = SalaryDeserializer.class)
    private BigDecimal salary;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getCompensationId() {
        return compensationId;
    }

    public void setCompensationId(String compensationId) {
        this.compensationId = compensationId;
    }

    public static class SalaryDeserializer extends NumberDeserializers.BigDecimalDeserializer {

        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String cleanString = p.getValueAsString().replaceAll("[^0-9.]", "");
            return new BigDecimal(cleanString).setScale(2, RoundingMode.HALF_EVEN);
        }
    }
}
