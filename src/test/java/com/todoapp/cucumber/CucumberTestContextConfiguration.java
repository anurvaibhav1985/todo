package com.todoapp.cucumber;

import com.todoapp.TodoApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = TodoApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
