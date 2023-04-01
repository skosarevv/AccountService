package dev.skosarev.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.*;

@JsonPropertyOrder({"employee", "period", "salary"})
public class PaymentDto {

    @Email
    @NotBlank
    @JsonProperty("employee")
    @Pattern(regexp = ".+@acme.com", message = "Email should ends with @acme.com")
    private String email;

    @Pattern(regexp = "\\d{2}-\\d{4}", message = "Wrong date!")
    private String period;

    @NotNull
    @Min(value = 1, message = "Salary must be non negative!")
    private Long salary;

    public String getEmail() {
        return email;
    }

    public String getPeriod() {
        return period;
    }

    public Long getSalary() {
        return salary;
    }
}
