package dynamicUi.demo.service;

import java.util.List;

import dynamicUi.demo.service.inter.UIPageService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import dynamicUi.demo.repoistory.UIPageRepository;

@Service
public class UIPageServiceImp implements UIPageService {

    private final UIPageRepository uiPageRepository;
    private final UIPageJsonRepository uiPageJsonRepository;

    public UIPageServiceImp(UIPageRepository uiPageRepository, UIPageJsonRepository uiPageJsonRepository) {
        this.uiPageRepository = uiPageRepository;
        this.uiPageJsonRepository = uiPageJsonRepository;
    }
    @Override
    public UIPage createPage(UIPage uiPage) {

        if (uiPageRepository.existsByPageCode(uiPage.getPageCode())) {
            throw new RuntimeException("Page code already exists");
        }

        // STEP 1 - SAVE PAGE

        UIPage uiPageSaved = uiPageRepository.save(uiPage);

        // STEP 2 - CREATE JSON ROOT

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.createObjectNode();

        // STEP 3 - PAGE OBJECT

        ObjectNode pageNode = objectMapper.createObjectNode();

        pageNode.put(
                "pageCode",
                uiPageSaved.getPageCode()
        );

        pageNode.put(
                "pageName",
                uiPageSaved.getPageName()
        );

        // IMPORTANT LINE YOU MISSED

        rootNode.set("page", pageNode);

        // STEP 4 - EMPTY COMPONENT ARRAY

        ArrayNode componentsArray =
                objectMapper.createArrayNode();

        rootNode.set(
                "components",
                componentsArray
        );

        // STEP 5 - SAVE JSON

        UIPageJson uiPageJson = new UIPageJson();

        uiPageJson.setUiPage(uiPageSaved);

        uiPageJson.setJsonSchema(
                rootNode.toPrettyString()
        );

        uiPageJsonRepository.save(uiPageJson);

        return uiPageSaved;
    }

    @Override
    public UIPage getPageByCode(String pageCode) {

        UIPage page = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() ->
                        new RuntimeException("Page not found with code: " + pageCode));
        System.out.println(page);
        return page;
    }

    @Override
    public List<UIPage> getAllPages() {
        return uiPageRepository.findByIsActiveTrue();
    }

    @Override
    public UIPage updatePage(String pageCode, UIPage uiPage) {
        UIPage existingPage = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page not found with code: " + pageCode));

        if (uiPage.getPageName() != null) {
            existingPage.setPageName(uiPage.getPageName());
        }
        if (uiPage.getDescription() != null) {
            existingPage.setDescription(uiPage.getDescription());
        }
        if (uiPage.getActive() != null) {
            existingPage.setActive(uiPage.getActive());
        }

        return uiPageRepository.save(existingPage);
    }

    @Override
    public void deletePage(String pageCode) {
        UIPage page = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page not found with code: " + pageCode));
        page.setActive(false);
        uiPageRepository.save(page);
    }

    @Override
    public List<UIPage> getAllPages1() {
        return uiPageRepository.findAll();
    }

    @Override
    public UIPage updatePageStatus(
            String pageCode,
            boolean status) {

        UIPage page =
                uiPageRepository.findByPageCode(pageCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Page not found"));

        page.setActive(status);

        return uiPageRepository.save(page);
    }
}
