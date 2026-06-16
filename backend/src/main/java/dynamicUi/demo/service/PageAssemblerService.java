package dynamicUi.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dynamicUi.demo.dto.PageJsonDTO;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageAssemblerService {

    //DI
    private final UIPageJsonRepository uiPageJsonRepository;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * assemble page json by fetching json of different table
     */
    public PageJsonDTO getPageAssembledByPageCode(String pageCode) {
        //check page is active  :- future task

        PageJsonDTO assembledPageJson = new PageJsonDTO();

        //get parent page json schema from uiPage json
        UIPageJson uiPageJson = uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                .orElseThrow(() ->
                         new RuntimeException("PAGE JSON NOT FOUND:: ID - 1111")
                );

        try{

        //convert it into Object Node(easy to manage (fetch changes and update))
        ObjectNode pageSchemaNode = (ObjectNode) OBJECT_MAPPER.readTree(uiPageJson.getJsonSchema());

        assembledPageJson.setJsonSchema(pageSchemaNode);
        //fetch each callable field

        //update parent page schema

        //return assmebled page schema
        return assembledPageJson;

        } catch (Exception ex) {
            //defult return statment if error occcures;
            return new PageJsonDTO();
        }

    }
}
