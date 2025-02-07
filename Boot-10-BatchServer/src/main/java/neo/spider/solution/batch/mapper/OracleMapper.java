package neo.spider.solution.batch.mapper;

import java.util.List;

import neo.spider.solution.batch.dto.FwkErrorHisDto;

public interface OracleMapper {
	List<FwkErrorHisDto> getRecords(Long startLine);

	long getCount();
}
