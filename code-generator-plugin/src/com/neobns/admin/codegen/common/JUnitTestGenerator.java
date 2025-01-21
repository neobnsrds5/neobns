package com.neobns.admin.codegen.common;

import com.squareup.javapoet.*;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;

// JavaPoet 라이브러리: Java 코드를 프로그래밍 방식으로 생성
public class JUnitTestGenerator {

	public static void generateJunitTest(String className, String packageName, String targetPath) throws IOException {
		// Create a test method for the `add` method
        MethodSpec testAdd = MethodSpec.methodBuilder(className + "JUnitTest")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Test.class)
                .addCode("""
                        // Add test logic
                        
                        // Assert
                        """)
                .build();

        // Create the test class
        TypeSpec testClass = TypeSpec.classBuilder(className + "Test")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(testAdd)
                .build();

        // Write the test class to a file
        JavaFile javaFile = JavaFile.builder(packageName, testClass)
                .build();

        // Specify the output directory
        javaFile.writeTo(Paths.get(targetPath+"/src/test/java"));
        System.out.println("JUnit test class generated successfully!");
	}
}
