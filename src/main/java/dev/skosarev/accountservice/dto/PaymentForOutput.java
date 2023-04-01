package dev.skosarev.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "lastname", "period", "salary"})
public class PaymentForOutput {
    @JsonProperty
    private String name;
    @JsonProperty
    private String lastname;
    @JsonProperty
    private String period;
    @JsonProperty
    private String salary;

    public PaymentForOutput(String name, String lastname, String period, String salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "PaymentForOutput{" +
                "name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", period='" + period + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }
}
