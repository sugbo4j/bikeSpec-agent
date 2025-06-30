package nz.co.sugbo4j.bikeSpec_agent.batch;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment; // Import Environment
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import nz.co.sugbo4j.bikeSpec_agent.model.AFullBikeSet;

@Configuration
@PropertySource("classpath:config/retailers.yml")
public class BatchConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

    private final Environment environment; // Inject Environment

    public BatchConfiguration(Environment environment) {
        this.environment = environment;
    }

    // Placeholder for ItemReader
    @Bean
    public ItemReader<String> reader() {
        return new ItemReader<String>() {
            private boolean read = false; // Moved to correct position

            @Override // Correctly applied to the method
            public String read() {
                if (!read) {
                    try {
                        String htmlContent = Jsoup
                                .parse(new ClassPathResource("samples/retailers/99spokes.html").getFile(), "UTF-8")
                                .html();
                        read = true;
                        logger.info("Successfully read samples/retailers/99spokes.html");
                        return htmlContent;
                    } catch (IOException e) {
                        logger.error("Error reading HTML file: {}", e.getMessage());
                        return null;
                    }
                }
                return null;
            }
        };
    }

    // Placeholder for ItemProcessor
    @Bean
    public ItemProcessor<String, AFullBikeSet> processor() {
        return new ItemProcessor<String, AFullBikeSet>() {
            @Override
            public AFullBikeSet process(String htmlContent) {
                logger.info("Processing HTML content...");
                Document doc = Jsoup.parse(htmlContent);

                String bikeNameSelector = environment.getProperty("retailers.99spokes.selectors.bikeName");
                String priceSelector = environment.getProperty("retailers.99spokes.selectors.price");

                String bikeName = doc.select(bikeNameSelector).text();
                String price = doc.select(priceSelector).text();

                logger.info("Extracted Bike Name: {}", bikeName);
                logger.info("Extracted Price: {}", price);

                // For now, AFullBikeSet is empty, so we just log and return a new instance
                return new AFullBikeSet(bikeName, price);
            }
        };
    }

    // Placeholder for ItemWriter
    @Bean
    public ItemWriter<AFullBikeSet> writer() {
        return new ItemWriter<AFullBikeSet>() {
            @Override
            public void write(Chunk<? extends AFullBikeSet> items) {
                // Placeholder for writing logic
                for (AFullBikeSet item : items) {
                    logger.info("Writing: {}", item);
                }
            }
        };
    }

    @Bean
    public Step scrapeBikeSpecStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<String> reader,
            ItemProcessor<String, AFullBikeSet> processor,
            ItemWriter<AFullBikeSet> writer) {
        return new StepBuilder("scrapeBikeSpecStep", jobRepository)
                .<String, AFullBikeSet>chunk(1, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step scrapeBikeSpecStep) {
        return new JobBuilder("scrapeBikeSpecJob", jobRepository)
                .start(scrapeBikeSpecStep)
                .build();
    }
}