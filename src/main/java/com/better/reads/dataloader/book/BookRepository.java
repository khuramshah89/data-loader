package com.better.reads.dataloader.book;

import com.better.reads.dataloader.author.Author;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CassandraRepository<Book, String> {
}
