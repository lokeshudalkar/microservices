package com.jobportal.jobservice.JobPostController;


import com.jobportal.jobservice.JobPostRepository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private  final JobPostRepository jobPostRepository;

    @GetMapping
    public ResponseEntity<?> getAllJobPost(){
        return ResponseEntity.ok(jobPostRepository.findAll());
    }

}
