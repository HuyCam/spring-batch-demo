package com.example.springbatchdemo.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("")
public class LoadController {
    @Autowired
    JobLauncher jobLauncher; // jobLauncher is created by spring boot itself

    @Autowired
    Job job;
    @GetMapping(value="/load/{file-name}")
    public BatchStatus load(@PathVariable("file-name") String fileName) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    /*
    YOu can either build jobparameter with Map or use JobParametersBuilder,
    JobParametersBuilder have a method callt toJobParameters that will return JobParamater with it's own map<String,JobParameter>
     */
        //        Map<String, JobParameter> maps = new HashMap<>();
//        maps.put("time", new JobParameter(System.currentTimeMillis()));

        JobParametersBuilder parametersBuilder = new JobParametersBuilder();
        parametersBuilder.addString("pathToFile", fileName + ".csv");
//        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = jobLauncher.run(job, parametersBuilder.toJobParameters());

        System.out.println("JobExecution: " + jobExecution.getStatus());

        while (jobExecution.isRunning()) {
            System.out.println("...");
        }

        return jobExecution.getStatus();
    }
    
}
