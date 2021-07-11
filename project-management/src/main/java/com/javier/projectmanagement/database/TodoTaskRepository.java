package com.javier.projectmanagement.database;

import org.springframework.data.repository.CrudRepository;

public interface TodoTaskRepository extends CrudRepository<TodoTask, Integer> {

}
