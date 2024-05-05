package ru.nsu.bolotov.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgramStateDto {
    @JsonProperty(value = "program_id", required = true)
    private String programId;

    @JsonProperty(value = "bspline_parameters", required = true)
    private BSplineStateDto bSplineStateDto;

    @JsonProperty(value = "application_parameters", required = true)
    private ApplicationParametersDto applicationParametersDto;

    @JsonProperty(value = "rotation_matrix", required = true)
    @JsonDeserialize(as = double[][].class)
    private double[][] rotationMatrix;

    @JsonProperty(value = "translate_matrix", required = true)
    @JsonDeserialize(as = double[][].class)
    private double[][] translateMatrix;
}
