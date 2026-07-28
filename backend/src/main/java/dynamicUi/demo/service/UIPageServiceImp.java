package dynamicUi.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import dynamicUi.demo.constant.PageStatus;
import dynamicUi.demo.demo.PageUpdateMessage;
import dynamicUi.demo.dto.UIPageRequestDTO;
import dynamicUi.demo.dto.UIPageResponseDTO;
import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.mapper.UIPageMapper;
import dynamicUi.demo.mapper.UIRouteMapper;
import dynamicUi.demo.repoistory.UIRouteRepository;
import dynamicUi.demo.service.inter.UIPageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;



import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import dynamicUi.demo.repoistory.UIPageRepository;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
@RequiredArgsConstructor
public class UIPageServiceImp implements UIPageService {

    private final UIPageRepository uiPageRepository;
    private final UIRouteRepository uiRouteRepository;
    private final UIPageJsonRepository uiPageJsonRepository;
    private final SimpMessagingTemplate messagingTemplate;

    //mapper
    private final UIPageMapper uiPageMapper;
    private final UIRouteMapper uiRouteMapper;
    private final JsonMapper jsonMapper;

    @Override
    @Transactional
    public UIPageResponseDTO createPage(UIPageRequestDTO uiPageReq) {

        ///Unique validation
        if (uiPageRepository.existsByPageCode(uiPageReq.getPageCode())) {
            throw new RuntimeException("Page code already exists");
        }

        if (uiRouteRepository.existsByPath(uiPageReq.getRoute().getPath())) {
            throw new RuntimeException("Route Path already exists");
        }

        UIPage uiPage = uiPageMapper.toEntity(uiPageReq);
        UIPage uiPageSaved = uiPageRepository.save(uiPage);

        UIRoute uiRoute = uiRouteMapper.toEntity(uiPageReq.getRoute());
        uiRoute.setPage(uiPageSaved);
        UIRoute uiRouteSaved = uiRouteRepository.save(uiRoute);

        createPageJson(uiPageSaved, uiRouteSaved);

        publishUpdate(uiPageSaved, "CREATED");

        return uiPageMapper.toResponse(uiPageSaved, uiRouteSaved);
    }

    @Override
    public UIPageResponseDTO getPageByCode(String pageCode) {

        UIPage page = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() ->
                        new RuntimeException("Page not found with code: " + pageCode));

        UIRoute route = getRouteForPageOrThrow(pageCode);
        return uiPageMapper.toResponse(page, route);
    }

    @Override
    public List<UIPage> getAllPages() {
        return uiPageRepository.findAll();
    }

    @Override
    @Transactional
    public UIPageResponseDTO updatePage(String pageCode, UIPageRequestDTO request) {

        UIPage page = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page not found"));
        UIRoute route = getRouteForPageOrThrow(pageCode);

        if (!route.getPath().equals(request.getRoute().getPath())
                && uiRouteRepository.existsByPath(request.getRoute().getPath())) {
            throw new RuntimeException("Route already exists");
        }

        // Update page and nested route
        uiRouteMapper.updateEntity(request.getRoute(), route);
        uiPageMapper.updateEntity(request, page);

        UIPage savedPage = uiPageRepository.save(page);
        UIRoute savedRoute = uiRouteRepository.save(route);

        syncPageJson(savedPage, savedRoute);

        return uiPageMapper.toResponse(savedPage, savedRoute);
    }

    @Override
    public void deletePage(String pageCode) {
        UIPage page = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page not found with code: " + pageCode));
        page.setStatus(PageStatus.DELETED);
        uiPageRepository.save(page);
        syncPageJson(page, getRouteForPageOrThrow(pageCode));
    }

    @Override
    public List<UIPage> getAllPages1() {
        return uiPageRepository.findAll();
    }

    @Override
    @Transactional
    public UIPage updatePageStatus(
            String pageCode,
            PageStatus status) {

        UIPage page =
                uiPageRepository.findByPageCode(pageCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Page not found"));

        page.setStatus(status);

        UIPage saved = uiPageRepository.save(page);

        syncPageJson(saved, getRouteForPageOrThrow(pageCode));

        return saved;
    }


    /// Helper Functions
    private UIRoute getRouteForPageOrThrow(String pageCode) {
        return uiRouteRepository.findByPage_PageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Route not found for page: " + pageCode));
    }

    private ObjectNode buildPageNode(UIPage page, UIRoute route) {

        ObjectNode pageNode = jsonMapper.createObjectNode();

        pageNode.put("id", page.getId());
        pageNode.put("pageCode", page.getPageCode());
        pageNode.put("pageName", page.getPageName());
        pageNode.put("description", page.getDescription());

        pageNode.put("route", route.getPath());

        pageNode.put("module", page.getModuleCode());
        pageNode.put("category", page.getCategoryCode());
        pageNode.put("layout", page.getLayoutCode());
        pageNode.put("version", page.getVersion());

        pageNode.put("icon", route.getIcon());

        pageNode.put("status", page.getStatus().name());

        ObjectNode security = pageNode.putObject("security");
        security.put("requireAuthentication", page.getRequireAuthentication());
        security.put("permission", page.getPermissionCode());

        ObjectNode navigation = pageNode.putObject("navigation");
        navigation.put("showInMenu", route.getShowInMenu());
        navigation.put("parentMenu", route.getParentMenu());
        navigation.put("menuOrder", route.getMenuOrder());
        navigation.put("breadcrumb", route.getBreadcrumb());

        return pageNode;
    }

    private void syncPageJson(UIPage page, UIRoute route) {

        UIPageJson pageJson = uiPageJsonRepository
                .findByUiPage_PageCode(page.getPageCode())
                .orElseThrow();

        pageJson.setJsonSchema(buildPageJson(page, route));

        uiPageJsonRepository.save(pageJson);
    }

    private String buildPageJson(UIPage page, UIRoute route) {

        ObjectNode root = jsonMapper.createObjectNode();

        root.set("page", buildPageNode(page, route));

        root.set("components", jsonMapper.createArrayNode());

        try {
            return jsonMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createPageJson(UIPage page, UIRoute route) {

        UIPageJson json = new UIPageJson();

        json.setUiPage(page);

        json.setJsonSchema(buildPageJson(page, route));

        uiPageJsonRepository.save(json);
    }

    private void publishUpdate(
            UIPage page,
            String action) {

        messagingTemplate.convertAndSend(
                "/topic/page-updates",
                PageUpdateMessage.builder()
                        .pageCode(page.getPageCode())
                        .pageName(page.getPageName())
                        .action(action)
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }
}
