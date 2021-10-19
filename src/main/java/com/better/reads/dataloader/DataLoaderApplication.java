package com.better.reads.dataloader;

import com.better.reads.dataloader.author.Author;
import com.better.reads.dataloader.author.AuthorRepository;
import com.better.reads.dataloader.book.Book;
import com.better.reads.dataloader.book.BookRepository;
import com.better.reads.dataloader.connection.DataStaxAstraProperties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class DataLoaderApplication {
    Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    BookRepository bookRepository;

    @Value("${datadump-location.author}")
    private String authorDumpLocation;

    @Value("${datadump-location.work}")
    private String workDumpLocation;

    public static void main(String[] args) {
        SpringApplication.run(DataLoaderApplication.class, args);
    }


    @PostConstruct
    public void setup() {
//        initAuthor();
//initBook();
    }

    private void initAuthor() {

        Path authorPath = Paths.get(authorDumpLocation);
        try (Stream<String> lines = Files.lines(authorPath)) {
            LOGGER.info("******authors persisting process begin *********");
            lines.forEach(line -> {
                String data = line.substring(line.indexOf("{"));
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    author.setPersonalName(jsonObject.optString("personal_name"));
                    author.setId(jsonObject.optString("key").replace("/authors/", ""));
                    LOGGER.info(author.toString());
                    authorRepository.save(author);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("****** finished authors persisting process *********");
    }

    private void initBook() {

        Path bookPath = Paths.get(workDumpLocation);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try (Stream<String> lines = Files.lines(bookPath)) {
            LOGGER.info("******Books persisting process begin *********");
            lines.forEach(line -> {
                String data = line.substring(line.indexOf("{"));
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    Book book = new Book();
                    book.setId(jsonObject.getString("key").replace("/works/", ""));
                    book.setName(jsonObject.optString("title"));
                    JSONObject descObj = jsonObject.optJSONObject("description");
                    if (descObj != null) {
                        book.setBookDescription(descObj.optString("value"));
                    }

                    JSONObject publishedObj = jsonObject.getJSONObject("created");
                    if (publishedObj != null) {
                        book.setPublishedDated(LocalDate.parse(publishedObj.optString("value"), dateTimeFormatter));
                    }

                    JSONArray coversArray = jsonObject.optJSONArray("covers");
                    if (coversArray != null) {
                        List<String> coverList = new ArrayList<>();
                        for (int i = 0; i < coversArray.length(); i++) {
                            coverList.add(coversArray.getString(i));
                        }
                        book.setCoverIds(coverList);
                    }

                    JSONArray authors = jsonObject.optJSONArray("authors");
                    if (authors != null) {
                        List<String> authorsList = new ArrayList<>();
                        for (int i = 0; i < authors.length(); i++) {
                            authorsList.add(authors.getJSONObject(i).getJSONObject("author")
                                    .getString("key").replace("/authors/", ""));
                        }
                        book.setAuthorIds(authorsList);
                        List<String> authorName = authorsList.stream().map(s -> authorRepository.findById(s))
                                .map(author -> {
                                    if (author.isEmpty()) return "Unknown author";
                                    return author.get().getName();
                                }).collect(Collectors.toList());
                        book.setAuthorNames(authorName);
                    }

                    LOGGER.info(book.toString());
                    bookRepository.save(book);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("****** finished Books persisting process *********");
    }


    /**
     * This is necessary to have the Spring Boot app use the Astra secure bundle
     * to connect to the database
     */
    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }
}
