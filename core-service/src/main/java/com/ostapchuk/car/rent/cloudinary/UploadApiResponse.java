package com.ostapchuk.car.rent.cloudinary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UploadApiResponse(
        @JsonProperty("url") String url
) {
}
