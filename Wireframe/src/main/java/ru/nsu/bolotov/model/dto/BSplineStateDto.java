package ru.nsu.bolotov.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BSplineStateDto {
    @JsonProperty(value = "support_points", required = true)
    @JsonDeserialize(as = ArrayList.class, contentAs = Point2D.Double.class)
    private List<Point2D> supportPoints;

    @JsonProperty(value = "bspline_points", required = true)
    @JsonDeserialize(as = ArrayList.class, contentAs = Point2D.Double.class)
    private List<Point2D> bSplinePoints;
}
