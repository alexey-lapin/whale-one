package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.domain.model.Project;
import com.github.alexeylapin.whaleone.domain.model.ProjectCampaign;
import com.github.alexeylapin.whaleone.domain.model.ProjectCampaignItem;
import com.github.alexeylapin.whaleone.domain.model.ProjectItem;
import com.github.alexeylapin.whaleone.domain.model.ProjectSite;
import com.github.alexeylapin.whaleone.domain.model.ProjectSiteItem;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.repo.ProjectCampaignRepository;
import com.github.alexeylapin.whaleone.domain.repo.ProjectRepository;
import com.github.alexeylapin.whaleone.domain.repo.ProjectSiteRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ProjectApi {

    private final ProjectRepository projectRepository;
    private final ProjectSiteRepository projectSiteRepository;
    private final ProjectCampaignRepository projectCampaignRepository;

    @PostMapping("/projects")
    public Project create(@RequestBody Project project,
                          @AuthenticationPrincipal IdUser user) {
        ZonedDateTime now = ZonedDateTime.now();
        UserRef userRef = new UserRef(user.getId(), user.getName());
        project = project.toBuilder()
                .id(0)
                .version(0)
                .createdAt(now)
                .createdBy(userRef)
                .lastUpdatedAt(now)
                .lastUpdatedBy(userRef)
                .build();
        return projectRepository.save(project);
    }

    @PutMapping("/projects/{id}")
    public Project update(@PathVariable long id,
                          @RequestBody Project project,
                          @AuthenticationPrincipal IdUser user) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing project expected");
        Assert.isTrue(project.version() > 0,
                "project.version must be greater than 0 - existing project expected");
        Assert.isTrue(id == project.id(),
                "id must match");
        project = project.toBuilder()
                .id(id)
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .build();
        return projectRepository.save(project);
    }

    @GetMapping("/projects/{id}")
    public Project get(@PathVariable long id) {
        return projectRepository.findById(id).orElseThrow();
    }

    @GetMapping("/projects")
    public PageDto<Project> getAll(@RequestParam int page,
                                   @RequestParam int size,
                                   @RequestParam Optional<String> name,
                                   @RequestParam Optional<String> client,
                                   @RequestParam Optional<String> ownership,
                                   @RequestParam Optional<String> region,
                                   @RequestParam Optional<String> type) {
        Page<Project> aPage = projectRepository.findAll(page, size);
        return new PageDto<>(aPage.getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
    }

    @GetMapping("/projects/items")
    public List<ProjectItem> getAllItems(@RequestParam Optional<String> q) {
        return projectRepository.findAllItems(q.orElse(""));
    }
    
    // sites

    @PostMapping("/projects/{id}/sites")
    public ProjectSite createSite(@PathVariable long id,
                                  @RequestBody ProjectSite site) {
        site = site.toBuilder()
                .id(0)
                .projectId(id)
                .build();
        return projectSiteRepository.save(site);
    }

    @GetMapping("/projects/{id}/sites")
    public List<ProjectSite> getAllSites(@PathVariable long id) {
        return projectSiteRepository.findAll(id).getContent();
    }

    @GetMapping("/projects/{id}/sites/items")
    public List<ProjectSiteItem> getAllSiteItems(@PathVariable long id, @RequestParam Optional<String> q) {
        return projectSiteRepository.findAllItems(id, q.orElse(""));
    }

    @PutMapping("/projects/{id}/sites/{siteId}")
    public ProjectSite updateSite(@PathVariable long id,
                                  @PathVariable long siteId,
                                  @RequestBody ProjectSite site) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing project expected");
        Assert.isTrue(siteId > 0,
                "project must be greater than 0 - existing site expected");
        Assert.isTrue(siteId == site.id(),
                "project must match");
        site = site.toBuilder()
                .id(siteId)
                .projectId(id)
                .build();
        return projectSiteRepository.save(site);
    }

    @DeleteMapping("/projects/{id}/sites/{siteId}")
    public void deleteSite(@PathVariable long id, @PathVariable long siteId) {
        projectSiteRepository.deleteById(siteId);
    }

    // campaigns
    
    @PostMapping("/projects/{id}/campaigns")
    public ProjectCampaign createCampaign(@PathVariable long id,
                                          @RequestBody ProjectCampaign campaign) {
        campaign = campaign.toBuilder()
                .id(0)
                .projectId(id)
                .build();
        return projectCampaignRepository.save(campaign);
    }

    @GetMapping("/projects/{id}/campaigns")
    public List<ProjectCampaign> getAllCampaigns(@PathVariable long id) {
        return projectCampaignRepository.findAll(id).getContent();
    }

    @GetMapping("/projects/{id}/campaigns/items")
    public List<ProjectCampaignItem> getAllCampaignItems(@PathVariable long id, @RequestParam Optional<String> q) {
        return projectCampaignRepository.findAllItems(id, q.orElse(""));
    }

    @PutMapping("/projects/{id}/campaigns/{campaignId}")
    public ProjectCampaign updateCampaign(@PathVariable long id,
                                  @PathVariable long campaignId,
                                  @RequestBody ProjectCampaign campaign) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing project expected");
        Assert.isTrue(campaignId > 0,
                "campaignId must be greater than 0 - existing campaign expected");
        Assert.isTrue(campaignId == campaign.id(),
                "campaignId must match");
        campaign = campaign.toBuilder()
                .id(campaignId)
                .projectId(id)
                .build();
        return projectCampaignRepository.save(campaign);
    }

    @DeleteMapping("/projects/{id}/campaigns/{campaignId}")
    public void deleteCampaign(@PathVariable long id, @PathVariable long campaignId) {
        projectCampaignRepository.deleteById(campaignId);
    }

}
