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

