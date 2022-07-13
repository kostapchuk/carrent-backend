package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.UploadRequestDto;
import com.ostapchuk.car.rent.service.DocumentWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentWriteController {

    private final DocumentWriteService documentWriteService;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('users:read')")
    public void save(@RequestPart("payload") final UploadRequestDto requestDto,
                     @RequestPart("file") final MultipartFile file) {
        documentWriteService.save(file, requestDto);
    }
}
