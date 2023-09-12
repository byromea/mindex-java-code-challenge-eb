package com.mindex.challenge.data;

public class ReportingStructure {

    /*
    I'd maybe think about just having numberOfReports be a transient attribute on Employee object instead of a
    new Class, unless there would be somme other usage for this information in this format down the road.
     */
    private Employee employee;
    private int numberOfReports;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }
}
