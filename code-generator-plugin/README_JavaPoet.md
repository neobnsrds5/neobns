# JavaPoet 라이브러리 설명

## 1. JavaPoet 개요
자바포엣은 **Java 코드 생성**을 위한 라이브러리로, Java 클래스를 프로그래밍 방식으로 생성할 때 사용됩니다. 이 라이브러리는 Google에서 개발되었으며, 복잡한 Java 소스 코드를 동적으로 생성하고 파일로 저장할 수 있는 강력한 도구를 제공합니다.

 

## 2. 주요 특징

- **코드 생성 자동화**: 수동으로 Java 파일을 작성하지 않고, 프로그래밍 방식으로 생성 가능.
- **유연성**: 클래스, 메서드, 필드, 어노테이션 등 Java의 모든 주요 구성 요소를 동적으로 정의할 수 있음.
- **명확한 구조**: 가독성과 유지 보수를 고려한 API 제공.
- **Java 소스 코드 출력**: 생성된 코드는 `.java` 파일로 저장 가능.

 

## 3. JavaPoet 사용 방법

### 1) 클래스 정의
아래는 Java 클래스를 동적으로 생성하는 기본 예제입니다.

```java
import com.squareup.javapoet.*;
import javax.lang.model.element.Modifier;

public class JavaPoetExample {
    public static void main(String[] args) throws Exception {
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(String[].class, "args")
                        .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.example", helloWorld)
                .build();

        javaFile.writeTo(System.out); // 콘솔에 출력하거나
        javaFile.writeTo(new File("src/main/java")); // 파일로 저장
    }
}
```

 

### 2) 생성된 Java 코드
위 코드 실행 시, 아래와 같은 Java 소스 파일이 생성됩니다:

```java
package com.example;

public final class HelloWorld {
  public static void main(String[] args) {
    System.out.println("Hello, JavaPoet!");
  }
}
```

 

## 4. 주요 기능 및 예제

### 1) 필드 정의
Java 클래스에 필드를 추가하는 방법:

```java
FieldSpec field = FieldSpec.builder(String.class, "name", Modifier.PRIVATE)
        .initializer("$S", "JavaPoet")
        .build();
```

### 2) 메서드 정의
Java 클래스에 메서드를 추가하는 방법:

```java
MethodSpec method = MethodSpec.methodBuilder("getName")
        .addModifiers(Modifier.PUBLIC)
        .returns(String.class)
        .addStatement("return this.name")
        .build();
```

### 3) 어노테이션 추가
어노테이션을 동적으로 추가:

```java
AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();
MethodSpec methodWithAnnotation = MethodSpec.methodBuilder("toString")
        .addAnnotation(overrideAnnotation)
        .addModifiers(Modifier.PUBLIC)
        .returns(String.class)
        .addStatement("return $S", "Hello from JavaPoet!")
        .build();
```

 

## 5. JavaPoet와 코드 생성 흐름

1. `TypeSpec`: 클래스를 정의.
2. `MethodSpec`: 메서드를 정의.
3. `FieldSpec`: 필드를 정의.
4. `AnnotationSpec`: 어노테이션을 정의.
5. `JavaFile`: 모든 요소를 묶어서 `.java` 파일로 생성.

 

## 6. 장점

- **자동화**: 동적으로 Java 소스 코드를 생성하여 반복 작업 최소화.
- **구조화된 API**: 클래스를 설계하듯이 코드를 생성할 수 있어 직관적.
- **호환성**: Java 표준을 준수하며 IDE나 빌드 시스템과 쉽게 통합 가능.

 

## 7. 실전 예시: Spring 컨트롤러 생성

JavaPoet을 사용하여 Spring 컨트롤러를 동적으로 생성:

```java
TypeSpec controller = TypeSpec.classBuilder("UserController")
        .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "RestController"))
        .addModifiers(Modifier.PUBLIC)
        .addMethod(MethodSpec.methodBuilder("getUser")
                .addAnnotation(ClassName.get("org.springframework.web.bind.annotation", "GetMapping"))
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return $S", "User Details")
                .build())
        .build();

JavaFile javaFile = JavaFile.builder("com.example.controller", controller).build();
javaFile.writeTo(new File("src/main/java"));
```

 

## 8. 결론

JavaPoet은 코드 생성이 필요한 다양한 프로젝트에서 효율적이고 직관적인 방법을 제공합니다. 특히, 복잡한 클래스나 메서드를 프로그램적으로 정의해야 할 때 강력한 도구입니다. Java와 함께 사용하면서 생산성을 높이는 데 큰 도움이 될 것입니다.
