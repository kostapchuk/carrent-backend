package com.ostapchuk.car.rent.cloudinary;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UploadRequest(
        @JsonProperty("public_id") String publicId
) {
}
