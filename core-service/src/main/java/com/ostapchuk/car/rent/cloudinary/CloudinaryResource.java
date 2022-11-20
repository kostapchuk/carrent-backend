package com.ostapchuk.car.rent.cloudinary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CloudinaryResource(
        @JsonProperty("public_id") String publicId
) {
}
