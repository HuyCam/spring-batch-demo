package com.example.springbatchdemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBatchDemoApplication.class)
public class BatchJobConfigurationTest {
    @Autowired
    private Job job;

    @Test
    public void test() {
        assertNotNull(job);
        assertEquals("ETL-Load", job.getName());
    }

}
