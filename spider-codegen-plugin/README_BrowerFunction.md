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

