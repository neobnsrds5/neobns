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
            warnings.add("ForceVirtualPrimaryKeyPlugin: The 'primaryKeyColumns' property must be specified.");
            return false;
        }
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        // 설정에서 지정한 가상 기본 키 컬럼을 가져옴
        String primaryKeyColumns = properties.getProperty("primaryKeyColumns");
        List<String> virtualPrimaryKeys = Arrays.asList(primaryKeyColumns.split(","));

        // 기존 기본 키 컬럼 무시
        List<IntrospectedColumn> newPrimaryKeyColumns = new ArrayList<>();
        for (String columnName : virtualPrimaryKeys) {
            IntrospectedColumn column = introspectedTable.getColumn(columnName.trim()).orElse(null);
            if (column != null) {
                newPrimaryKeyColumns.add(column);
            }
        }

        // 가상 기본 키 설정
        if (!newPrimaryKeyColumns.isEmpty()) {
            introspectedTable.getPrimaryKeyColumns().clear();
            introspectedTable.getPrimaryKeyColumns().addAll(newPrimaryKeyColumns);
            System.out.println("Virtual primary keys applied: " + virtualPrimaryKeys);
        } else {
            System.out.println("No valid virtual primary keys found for table: " + introspectedTable.getFullyQualifiedTable());
        }
    }
}
