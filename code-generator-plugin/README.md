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


---


# MyBatis Generator 가이드

## 1. MyBatis Generator란?

MyBatis Generator(MBG)는 **MyBatis**를 사용하는 애플리케이션에서 데이터베이스 테이블을 기반으로 DAO(Data Access Object), 매퍼 XML 파일, 모델 클래스를 자동으로 생성해주는 도구입니다. 이를 통해 데이터베이스 CRUD 작업을 간소화하고 반복 작업을 줄일 수 있습니다.


## 2. 주요 특징

- **자동화된 코드 생성**:
  - 테이블에 맞는 기본적인 CRUD 메서드 생성 (`insert`, `update`, `delete`, `select`).
  - DTO 및 Mapper 인터페이스 자동 생성.

- **커스터마이징 가능**:
  - 플러그인을 통해 추가 기능을 정의할 수 있음.
  - 생성된 코드의 포맷 및 동작 수정 가능.

- **DBMS 지원**:
  - 다양한 데이터베이스(DBMS)와 호환 가능 (MySQL, PostgreSQL, Oracle 등).


## 3. MyBatis Generator의 기본 구성 요소

1. **Java Model Generator**:
   - 데이터베이스 테이블의 컬럼을 기반으로 DTO(Data Transfer Object) 클래스를 생성.
   - 예: `User`, `Product` 등의 클래스 생성.

2. **SQL Map Generator**:
   - 매퍼 XML 파일(`mapper.xml`)을 생성.
   - CRUD 쿼리 및 결과 매핑을 정의.

3. **Java Client Generator**:
   - 매퍼 인터페이스를 생성하여 애플리케이션 코드에서 데이터베이스와 상호작용 가능.
   - 예: `UserMapper` 인터페이스.


## 4. 생성되는 코드

### 1) DTO 클래스 (Model)

```java
public class User {
    private Integer id;
    private String name;
    private String email;

    // Getters and Setters
}
```

### 2) Mapper 인터페이스

```java
public interface UserMapper {
    int insert(User record);

    int deleteByPrimaryKey(Integer id);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(User record);
}
```

### 3) 매퍼 XML

```xml
<mapper namespace="com.example.mapper.UserMapper">
    <insert id="insert" parameterType="com.example.model.User">
        INSERT INTO user (name, email)
        VALUES (#{name}, #{email})
    </insert>

    <select id="selectByPrimaryKey" resultType="com.example.model.User">
        SELECT id, name, email FROM user WHERE id = #{id}
    </select>
</mapper>
```


## 5. MyBatis Generator 커스터마이징

### 1) 플러그인 작성
플러그인을 작성하여 기본 CRUD 외의 동작 추가 가능.

#### 페이징 기능 추가 (예: `findByPage`)

```java
public class FindByPagePlugin extends PluginAdapter {

    @Override
    public boolean sqlMapDocumentGenerated(XmlElement document, IntrospectedTable introspectedTable) {
        XmlElement findByPage = new XmlElement("select");
        findByPage.addAttribute(new Attribute("id", "findByPage"));
        findByPage.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));

        findByPage.addElement(new TextElement(
                "SELECT * FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime() +
                " LIMIT #{size} OFFSET #{page} * #{size}"));

        document.addElement(findByPage);
        return true;
    }
}
```

### 2) 커스텀 플러그인 적용
`generatorConfig.xml`에 플러그인 등록:

```xml
<plugin type="com.example.plugins.FindByPagePlugin" />
```


## 6. 장점과 한계

### 장점
- 데이터베이스 중심 애플리케이션 개발 속도 향상.
- 표준화된 코드 생성으로 일관성 유지.
- 다양한 DBMS 지원.

### 한계
- 복잡한 쿼리는 수동 작성 필요.
- 기본 제공 메서드 외 기능은 커스터마이징 요구.


## 7. 결론
MyBatis Generator는 반복적인 데이터베이스 작업을 자동화하고 생산성을 높이는 강력한 도구입니다. 기본 제공 기능 외에도 커스터마이징을 통해 프로젝트 요구 사항에 맞는 추가 동작을 구현할 수 있습니다.


---


# BrowserFunction 클래스 설명

## 1. BrowserFunction 개요

`BrowserFunction` 클래스는 Eclipse의 SWT(Standard Widget Toolkit)에서 제공하는 기능으로, Java와 JavaScript 간의 양방향 통신을 가능하게 합니다. 이 클래스는 SWT의 `Browser` 위젯과 함께 사용되며, 브라우저에서 실행되는 JavaScript 함수와 Java 메서드를 연결하는 역할을 합니다.

 

## 2. 주요 특징

- **JavaScript → Java 호출**: JavaScript에서 Java 메서드를 호출할 수 있습니다. 이때 전달된 데이터는 Java 메서드에서 처리됩니다.
- **Java → JavaScript 호출**: Java는 `Browser` 위젯의 `execute` 메서드를 사용해 JavaScript 코드를 실행할 수 있습니다.
- **동적 연결**: 특정 JavaScript 함수 이름을 Java 메서드와 연결하여 사용할 수 있습니다.

 

## 3. BrowserFunction 사용 방법

### 1) BrowserFunction 클래스 정의

```java
private class ExampleFunction extends BrowserFunction {
    ExampleFunction(Browser browser, String name) {
        super(browser, name); // Browser 인스턴스와 JavaScript 함수 이름 지정
    }

    @Override
    public Object function(Object[] arguments) {
        System.out.println("JavaScript 호출됨: " + arguments[0]);
        return "Java로부터의 응답"; // JavaScript에 반환
    }
}
```

### 2) BrowserFunction 등록

```java
Browser browser = new Browser(parent, SWT.EDGE);
new ExampleFunction(browser, "exampleFunction");
```

### 3) JavaScript에서 호출

```javascript
// JavaScript 코드
var result = exampleFunction("Hello from JavaScript");
console.log("Java로부터의 응답: " + result);
```

 

## 4. BrowserFunction과 JavaScript 통신 흐름

1. JavaScript에서 Java 메서드를 호출하며 데이터를 전달합니다.
2. Java는 데이터를 처리하고 결과를 반환합니다.
3. Java가 `Browser`의 `execute` 메서드를 통해 JavaScript 코드를 실행할 수 있습니다.

 

## 5. 장점

- Java와 JavaScript 간 직접 통신 가능
- 브라우저 기반 UI를 활용하면서 복잡한 로직은 Java에서 처리 가능
- 데이터 처리 및 비동기 통신을 Java에서 구현하여 성능과 안정성 보장

 

## 6. 실전 예시: BrowserView 코드에서 활용

### 1) ListTables 클래스
JavaScript 함수 `invokeListTables`와 연결하여 데이터베이스 테이블 목록을 반환합니다.

### 2) GenerateCode 클래스
JavaScript 함수 `invokeGenerateCode`와 연결하여 사용자가 입력한 데이터를 기반으로 코드를 생성합니다.

```java
new ListTables(browser, "invokeListTables");
new GenerateCode(browser, "invokeGenerateCode");
```

### JavaScript 호출 예시

```javascript
var result = invokeListTables("jdbc:mysql://localhost:3306/mydb", "user", "password");
console.log(result);

generateCode("jdbc:mysql://localhost:3306/mydb", "user", "password", "output/path");
```

