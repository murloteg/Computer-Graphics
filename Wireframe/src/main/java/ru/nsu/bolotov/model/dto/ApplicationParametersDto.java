package ru.nsu.bolotov.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationParametersDto {
    @JsonProperty(value = "number_of_support_points", required = true)
    private int numberOfSupportPoints;

    @JsonProperty(value = "number_of_bspline_segments", required = true)
    private int numberOfBSplinePartSegments;

    @JsonProperty(value = "number_of_forming_lines", required = true)
    private int numberOfFormingLines;

    @JsonProperty(value = "number_of_circle_smoothing_segments", required = true)
    private int circleSmoothingSegments;

    @JsonProperty(value = "zoom_parameter", required = true)
    private double zoomParameter;
}
