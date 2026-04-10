package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.enums.JobStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job-status-options")
public class JobStatusController {

        @GetMapping
        public JobStatus[] getAllStatusValues() {
            return JobStatus.values();
        }

}
