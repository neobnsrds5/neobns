# 스프링 PlantUML 프로젝트 일반 jar 형식으로 재빌드

### BOOT-INF/classes/ 하위의 클래스 파일을 루트 디렉토리로 이동
``` mv BOOT-INF/classes/* . ```

### 불필요한 디렉토리 삭제
``` rm -rf BOOT-INF META-INF ```

### JAR 파일 재생성
``` jar cf ../plantUMLServer-fixed.jar * ```

### JAR 파일 내용 확인
``` jar tf ../plantUMLServer-fixed.jar | grep TraceUmlService ```


#####응답 예시 : ```com/neo/plantUMLServer/service/TraceUmlService.class```
