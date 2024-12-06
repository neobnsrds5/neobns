package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AsciiDocCodeGenerator implements BaseCodeGenerator {
    
    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String asciiDoc = generateResourceDocumentation(packageName, resourceName, schema);
        writeToFile(packageDir + resourceName + ".adoc", asciiDoc);
        generateSnippets(packageDir, resourceName.toLowerCase());
    }
    
    private String generateResourceDocumentation(String packageName, String resourceName, Schema schema) {
        StringBuilder doc = new StringBuilder();
        
        // Document Header
        doc.append("""
            = API Documentation
            :toc: left
            :toc-title: Contents
            :doctype: book
            :source-highlighter: highlightjs
            
            == Overview
            This document provides API details for the `/%s` endpoints, including request, response, and usage examples.
            
            == API Details
            
            """.formatted(resourceName.toLowerCase()));

        // Generate endpoint documentation
        generateEndpointDocs(doc, resourceName, "GET", "Find All");
        generateEndpointDocs(doc, resourceName, "GET", "Find By ID");
        generateEndpointDocs(doc, resourceName, "POST", "Create");
        generateEndpointDocs(doc, resourceName, "PUT", "Update");
        generateEndpointDocs(doc, resourceName, "DELETE", "Delete");

        // Examples Section
        doc.append("""
            === Examples
            Examples of how to use the API with sample requests and responses.
            
            * Curl Example: include::{snippets}/%s/curl-request.adoc[]
            * HTTPie Example: include::{snippets}/%s/httpie-request.adoc[]
            
            == References
            For additional details, visit our [API Documentation Portal](#).
            """.formatted(
                resourceName.toLowerCase(),
                resourceName.toLowerCase()
            ));

        return doc.toString();
    }
    
    private void generateEndpointDocs(StringBuilder doc, String resourceName, String method, String operation) {
        String endpoint = resourceName.toLowerCase();
        String snippetPrefix = endpoint + "-" + operation.toLowerCase().replace(" ", "-");
        
        doc.append(String.format("""
            === %s %s
            Description: This endpoint %s %s.
            
            include::{snippets}/%s/curl-request.adoc[]
            include::{snippets}/%s/httpie-request.adoc[]
            include::{snippets}/%s/http-request.adoc[]
            include::{snippets}/%s/http-response.adoc[]
            include::{snippets}/%s/request-body.adoc[]
            include::{snippets}/%s/request-fields.adoc[]
            include::{snippets}/%s/response-body.adoc[]
            
            """,
            operation,
            resourceName,
            operation.toLowerCase(),
            resourceName.toLowerCase(),
            snippetPrefix,
            snippetPrefix,
            snippetPrefix,
            snippetPrefix,
            snippetPrefix,
            snippetPrefix,
            snippetPrefix
        ));
    }
    
    private void generateSnippets(String packageDir, String resourceName) throws IOException {
        // Create snippets directory
        String snippetsDir = packageDir + "snippets/" + resourceName + "/";
        Files.createDirectories(Paths.get(snippetsDir));
        
        // Generate curl request snippet
        generateSnippet(snippetsDir, "curl-request.adoc", """
            [source,bash]
            ----
            curl -X GET 'http://localhost:8080/%s' \\
                 -H 'Content-Type: application/json'
            ----
            """.formatted(resourceName));
            
        // Generate HTTPie request snippet
        generateSnippet(snippetsDir, "httpie-request.adoc", """
            [source,bash]
            ----
            http GET 'http://localhost:8080/%s' \\
                Content-Type:application/json
            ----
            """.formatted(resourceName));
            
        // Generate HTTP request snippet
        generateSnippet(snippetsDir, "http-request.adoc", """
            [source,http,options="nowrap"]
            ----
            GET /%s HTTP/1.1
            Host: localhost:8080
            Content-Type: application/json
            ----
            """.formatted(resourceName));
            
        // Generate HTTP response snippet
        generateSnippet(snippetsDir, "http-response.adoc", """
            [source,http,options="nowrap"]
            ----
            HTTP/1.1 200 OK
            Content-Type: application/json
            
            {
                "status": "success",
                "data": {}
            }
            ----
            """);
            
        // Generate request body snippet
        generateSnippet(snippetsDir, "request-body.adoc", """
            [source,json,options="nowrap"]
            ----
            {
                "id": "123",
                "name": "example"
            }
            ----
            """);
            
        // Generate request fields snippet
        generateSnippet(snippetsDir, "request-fields.adoc", """
            |===
            |Field|Type|Description|Required
            
            |id
            |String
            |The unique identifier
            |Yes
            
            |name
            |String
            |The name of the resource
            |Yes
            |===
            """);
            
        // Generate response body snippet
        generateSnippet(snippetsDir, "response-body.adoc", """
            [source,json,options="nowrap"]
            ----
            {
                "id": "123",
                "name": "example",
                "createdAt": "2024-01-01T12:00:00Z"
            }
            ----
            """);
    }
    
    private void generateSnippet(String snippetsDir, String filename, String content) throws IOException {
        writeToFile(snippetsDir + filename, content);
    }
}