# 실행 방법

1. eclipse/STS에 spider-codegen-plugin 플러그인 프로젝트 import
2. run as -> eclipse application
3. 새로 열린 eclipse/STS 창의 window -> show view -> others -> code generator -> browser view
4. browser view에 데이터베이스 정보 입력 후 show tables 버튼 클릭
5. generate code 버튼 클릭 시 eclipse/STS 폴더 내 target path에 적은 폴더명으로  
   조회된 모든 table에 대해 mapper.xml, Mapper, DAO, Service, Controller 생성
     - mapper.xml, Mapper, DAO: Mybatis Generator에 의해 단순 CRUD 생성, primary key 없는 경우 insert 구문만 생성
     - Service, Controller: JavaPoet 라이브러리 이용, Mybatis Generator에 생성되는 단순 CRUD에 맞춰 메소드 생성
