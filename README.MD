## Architecture Overview

### 1. Project Purpose

The `bikeSpec-agent` is a web scraping tool designed to extract bike component specifications from popular online bike stores. The collected data is then sent to the `BikeSpec API` via GraphQL, primarily for consumption by the `BikeSpec Pro` mobile application.

### High-Level Architecture

The application is built as a Spring Boot application, leveraging modern Java (JDK 21) for its core logic. It utilizes JSoup for HTML parsing and a GraphQL client for data transmission. The architecture is designed to be modular, allowing for easy extension to support new retailers and data processing pipelines.

### Module Organization

The project follows a standard Maven project structure, with source code organized into logical packages reflecting their responsibilities.

1.  Config:
    - Retailer Configuration: Centralized YAML profiles define retailer-specific parameters (URLs, HTML/CSS selectors, schedules) enabling quick onboarding without code changes.
    - BatchConfiguration, Configures Spring Batch jobs and steps, defining the reader, processor, and writer
2.  Scraper:
    Abstract base scraper handles common functionality, while retailer-specific implementations focus on unique parsing logic. New retailers require only

    - Configuration entry
    - Single subclass implementation

3.  Batch: Spring Batch components responsible for the scraping job

    - Reader: Scrape the webpage using jsoup and pass the document to retailer-specific scraper
    - Processor: Processes and normalizes the scraped data
    - Writer: Transmits the normalized data to GraphQL API for storage

4.  Model: Defines the data structures using Java Records for immutability. This includes various component types (e.g., `Fork`, `Crank`, `WheelSize`), and the main `AFullBikeSet` aggregate model.

### Dependencies

The project relies on the following key technologies and libraries:

- **Java 21 or higher**: The primary programming language.
- **Maven 3 or higher**: Build automation tool.
- **Spring Boot**: Framework for building stand-alone, production-grade Spring applications.
- **Spring Batch**: Framework for robust batch processing.
- **JSoup**: Java library for working with real-world HTML.
