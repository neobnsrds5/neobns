package code_generator_plugin.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForceVirtualPrimaryKeyPlugin extends PluginAdapter {

	@Override
    public boolean validate(List<String> warnings) {
        // 플러그인이 유효한지 확인
        String primaryKeyColumns = properties.getProperty("primaryKeyColumns");
        if (primaryKeyColumns == null || primaryKeyColumns.trim().isEmpty()) {
            warnings.add("ForceVirtualPrimaryKeyPlugin: The 'primaryKeyColumn' property must be specified.");
            return false;
        }
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        // 설정에서 지정한 가상 기본 키 컬럼을 가져옴
        List<String> newPrimaryKeyColumns = Arrays.asList(properties.getProperty("primaryKeyColumns").split(","));
        
        if(introspectedTable.hasPrimaryKeyColumns()) { // pk를 가지고 있는 경우 pk가 없는 상태로 만들기
        	List<IntrospectedColumn> curPrimaryKeyColumns = new ArrayList<>();
        	curPrimaryKeyColumns.addAll(introspectedTable.getPrimaryKeyColumns());
        	introspectedTable.getPrimaryKeyColumns().clear();
        	
        	for(IntrospectedColumn curPKColumn : curPrimaryKeyColumns) {
        		introspectedTable.addColumn(curPKColumn);
        	}
        }
        // pk 추가하기
    	for(String pkColumnName : newPrimaryKeyColumns) {
        	introspectedTable.addPrimaryKeyColumn(pkColumnName);
        }
    }
    
}
