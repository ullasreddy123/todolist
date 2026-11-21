package com.example.todoweb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByOwnerEmail(String ownerEmail);
}