package com.ostapchuk.car.rent.cloudinary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchApiResponse(
        @JsonProperty("resources") List<CloudinaryResource> resources,
        @JsonProperty("next_cursor") String nextCursor
) {
}
