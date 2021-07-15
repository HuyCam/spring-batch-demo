package com.example.springbatchdemo.config;

import com.example.springbatchdemo.model.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;


@Configuration
@EnableBatchProcessing // make sure add this so Spring boot knows to access jobLauncher and other resources.
public class SpringBatchConfig {
    /*
    To create a job we need a job factory
     */
    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   StepBuilderFactory stepBuilderFactory,
                   ItemReader<User> itemReader,
                   ItemProcessor<User, User> itemProcessor ,// input is User, out put is User
                   ItemWriter<User> itemWriter
    ) {
        /*
        A step has a reader, processor and writer
         */
        Step step = stepBuilderFactory.get("ETL-file-load")
                .<User, User>chunk(100)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();

        return jobBuilderFactory.get("ETL-Load") // you can give it any name, here picked ETL-Load
                .incrementer(new RunIdIncrementer()) // every time batch run, this will get incremented
                .start(step) // you can you either flow or start, flow when you have many steps
                .build(); // return a job
        /*
        assume you have multiple step you do this:
        .flow(step)
        .next(step)...
         */
    }
    /*
    @Value annotation get the setting environment pass to the parameter
     */
    @Bean
    @StepScope
//    public FlatFileItemReader<User> itemReader(@Value("${input}") Resource resource)
    public FlatFileItemReader<User> itemReader(@Value("#{jobParameters['pathToFile']}") String pathToFile)
    {
        // .resource(new ClassPathResource("sample-data.csv"))
        FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
//        flatFileItemReader.setResource(resource);
        flatFileItemReader.setResource(new ClassPathResource(pathToFile));
        flatFileItemReader.setName("CSV-Reader");

        flatFileItemReader.setLinesToSkip(1); // skip first line
        flatFileItemReader.setLineMapper(lineMapper()); // map this data to POJO

        return flatFileItemReader;
    }

    @Bean
    public LineMapper<User> lineMapper() {
        DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        // config tokenizer
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[] {"id", "name", "dept", "salary"});

        // Bean wrapper to help set each field above to a POJO (id, name, dept, salary);
        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

//    @Bean
//    public ItemWriter<User> itemWriter() {
//
//        return new ItemWriter<User>() {
//            @Override
//            public void write(List<? extends User> users) throws Exception {
//                System.out.println("In config class ItemWriter is running");
//                for (User user: users) {
//                    System.out.println("Writing item: " + user.toString());
//                }
//            }
//        };
//    }
//
//    @Bean
//    public PassThroughItemProcessor<User> itemProcessor() {return new PassThroughItemProcessor<>();}
}
