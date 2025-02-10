package neo.spider.admin.flow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import spider.neo.solution.flowadmin.dto.bulkhead.BulkheadDto;
import spider.neo.solution.flowadmin.dto.bulkhead.BulkheadSearchDto;
import spider.neo.solution.flowadmin.dto.redisPub.UpdateConfigDto;
import spider.neo.solution.flowadmin.mapper.ApplicationMapper;
import spider.neo.solution.flowadmin.mapper.BulkheadMapper;

import java.util.List;

@Service
public class BulkheadService {

    private static final int TYPE = 0;

    private final BulkheadMapper bulkheadMapper;
    private final ApplicationMapper applicationMapper;
    private final ObjectMapper objectMapper;
    private final MessagePublisher publisher;

    public BulkheadService(BulkheadMapper bulkheadMapper, ObjectMapper objectMapper, MessagePublisher publisher, ApplicationMapper applicationMapper) {
        this.bulkheadMapper = bulkheadMapper;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
        this.applicationMapper = applicationMapper;
    }

    public List<BulkheadSearchDto> findByApplication(long id){
        return bulkheadMapper.findByApplication(id);
    }

    public boolean create(BulkheadDto newBulkhead){
        int result = bulkheadMapper.create(newBulkhead);
        if (result > 0) {
            applicationMapper.updateModified_date(newBulkhead.getApplication_id());
            UpdateConfigDto updateConfigDto = new UpdateConfigDto();
            updateConfigDto.setId(newBulkhead.getId());
            updateConfigDto.setType(TYPE);
            updateConfigDto.setDoing(0); // create, update
            updateConfigDto.setName(newBulkhead.getUrl());
            try{
                String json = objectMapper.writeValueAsString(updateConfigDto);
                String name = applicationMapper.findById(newBulkhead.getApplication_id()).getApplication_name();
                publisher.publish(name, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean delete(long id, String application_name){
        BulkheadDto bh = bulkheadMapper.findById(id);
        int result = bulkheadMapper.delete(id);
        if (result > 0) {
            applicationMapper.updateModified_date(bh.getApplication_id());
            UpdateConfigDto updateConfigDto = new UpdateConfigDto();
            updateConfigDto.setId(id);
            updateConfigDto.setType(TYPE);
            updateConfigDto.setDoing(1); // delete
            updateConfigDto.setName(bh.getUrl());
            try {
                String json = objectMapper.writeValueAsString(updateConfigDto);
                publisher.publish(application_name, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("전송 실패");
            }

        } else {
            return false;
        }

        return true;
    }

    public boolean update(BulkheadDto dto){
        int result = bulkheadMapper.update(dto);
        if (result > 0) {
            applicationMapper.updateModified_date(dto.getApplication_id());
            UpdateConfigDto updateConfigDto = new UpdateConfigDto();
            updateConfigDto.setId(dto.getId());
            updateConfigDto.setType(TYPE);
            updateConfigDto.setDoing(0);
            updateConfigDto.setName(dto.getUrl());
            try {
                String json = objectMapper.writeValueAsString(updateConfigDto);
                String name = applicationMapper.findById(dto.getApplication_id()).getApplication_name();
                publisher.publish(name, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return false;
        }
        return true;
    }

    public BulkheadDto findById(long id){
        return bulkheadMapper.findById(id);
    }
}
