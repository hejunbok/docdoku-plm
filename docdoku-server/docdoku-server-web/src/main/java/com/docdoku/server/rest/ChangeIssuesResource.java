/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.docdoku.server.rest;

import com.docdoku.core.change.ChangeIssue;
import com.docdoku.core.document.DocumentIterationKey;
import com.docdoku.core.exceptions.AccessRightException;
import com.docdoku.core.exceptions.EntityConstraintException;
import com.docdoku.core.exceptions.EntityNotFoundException;
import com.docdoku.core.exceptions.UserNotActiveException;
import com.docdoku.core.meta.Tag;
import com.docdoku.core.product.PartIterationKey;
import com.docdoku.core.security.ACL;
import com.docdoku.core.security.UserGroupMapping;
import com.docdoku.core.services.IChangeManagerLocal;
import com.docdoku.server.rest.dto.*;
import com.docdoku.server.rest.dto.change.ChangeIssueDTO;
import com.docdoku.server.rest.dto.change.ChangeItemDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@RequestScoped
@Api(hidden = true, value = "issues", description = "Operations about issues")
@DeclareRoles(UserGroupMapping.REGULAR_USER_ROLE_ID)
@RolesAllowed(UserGroupMapping.REGULAR_USER_ROLE_ID)
public class ChangeIssuesResource {

    @Inject
    private IChangeManagerLocal changeManager;

    private Mapper mapper;

    public ChangeIssuesResource() {

    }

    @PostConstruct
    public void init() {
        mapper = DozerBeanMapperSingletonWrapper.getInstance();
    }

    @GET
    @ApiOperation(value = "Get issues for given parameters",
            response = ChangeIssueDTO.class,
            responseContainer = "List")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIssues(@PathParam("workspaceId") String workspaceId)
            throws EntityNotFoundException, UserNotActiveException {
        List<ChangeIssue> changeIssues = changeManager.getChangeIssues(workspaceId);
        List<ChangeIssueDTO> changeIssueDTOs = new ArrayList<>();
        for (ChangeIssue issue : changeIssues) {
            ChangeIssueDTO changeIssueDTO = mapper.map(issue, ChangeIssueDTO.class);
            changeIssueDTO.setWritable(changeManager.isChangeItemWritable(issue));
            changeIssueDTOs.add(changeIssueDTO);
        }
        return Response.ok(new GenericEntity<List<ChangeIssueDTO>>((List<ChangeIssueDTO>) changeIssueDTOs) {
        }).build();
    }

    @POST
    @ApiOperation(value = "Create issue",
            response = ChangeIssueDTO.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeItemDTO createIssue(@PathParam("workspaceId") String workspaceId,
                                     @ApiParam(required = true, value = "Change issue to create") ChangeIssueDTO changeIssueDTO)
            throws EntityNotFoundException, AccessRightException {
        ChangeIssue changeIssue = changeManager.createChangeIssue(workspaceId,
                changeIssueDTO.getName(),
                changeIssueDTO.getDescription(),
                changeIssueDTO.getInitiator(),
                changeIssueDTO.getPriority(),
                changeIssueDTO.getAssignee(),
                changeIssueDTO.getCategory());
        ChangeIssueDTO ret = mapper.map(changeIssue, ChangeIssueDTO.class);
        ret.setWritable(true);
        return ret;
    }

    @GET
    @ApiOperation(value = "Search issue with given reference",
            response = ChangeIssueDTO.class,
            responseContainer = "List")
    @Path("link")
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeIssueDTO[] searchIssuesToLink(@PathParam("workspaceId") String workspaceId,
                                               @QueryParam("q") String q)
            throws EntityNotFoundException, UserNotActiveException {
        int maxResults = 8;
        List<ChangeIssue> issues = changeManager.getIssuesWithReference(workspaceId, q, maxResults);
        List<ChangeIssueDTO> issueDTOs = new ArrayList<>();
        for (ChangeIssue issue : issues) {
            ChangeIssueDTO changeIssueDTO = mapper.map(issue, ChangeIssueDTO.class);
            changeIssueDTO.setWritable(changeManager.isChangeItemWritable(issue));
            issueDTOs.add(changeIssueDTO);
        }
        return issueDTOs.toArray(new ChangeIssueDTO[issueDTOs.size()]);
    }

    @GET
    @ApiOperation(value = "Get issue",
            response = ChangeIssueDTO.class)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{issueId}")
    public ChangeItemDTO getIssue(@PathParam("workspaceId") String workspaceId,
                                  @PathParam("issueId") int issueId)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException {
        ChangeIssue changeIssue = changeManager.getChangeIssue(workspaceId, issueId);
        ChangeIssueDTO changeIssueDTO = mapper.map(changeIssue, ChangeIssueDTO.class);
        changeIssueDTO.setWritable(changeManager.isChangeItemWritable(changeIssue));
        return changeIssueDTO;
    }

    @PUT
    @ApiOperation(value = "Update issue",
            response = ChangeIssueDTO.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{issueId}")
    public ChangeItemDTO updateIssue(@PathParam("workspaceId") String workspaceId,
                                     @PathParam("issueId") int issueId,
                                     @ApiParam(required = true, value = "Change issue to update") ChangeIssueDTO pChangeIssueDTO)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException {
        ChangeIssue changeIssue = changeManager.updateChangeIssue(issueId,
                workspaceId,
                pChangeIssueDTO.getDescription(),
                pChangeIssueDTO.getPriority(),
                pChangeIssueDTO.getAssignee(),
                pChangeIssueDTO.getCategory());
        ChangeIssueDTO changeIssueDTO = mapper.map(changeIssue, ChangeIssueDTO.class);
        changeIssueDTO.setWritable(changeManager.isChangeItemWritable(changeIssue));
        return changeIssueDTO;
    }

    @DELETE
    @ApiOperation(value = "Delete issue",
            response = ChangeIssueDTO.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{issueId}")
    public Response removeIssue(@PathParam("issueId") int issueId)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException, EntityConstraintException {
        changeManager.deleteChangeIssue(issueId);
        return Response.ok().build();
    }

    @PUT
    @ApiOperation(value = "Update tags attached to an issue",
            response = ChangeIssueDTO.class)
    @Path("{issueId}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeItemDTO saveTags(@PathParam("workspaceId") String workspaceId,
                                  @PathParam("issueId") int issueId,
                                  @ApiParam(required = true, value = "Tag list to add") TagListDTO tagListDTO)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException {

        List<TagDTO> tagDTOs = tagListDTO.getTags();
        String[] tagsLabel = new String[tagDTOs.size()];
        for (int i = 0; i < tagDTOs.size(); i++) {
            tagsLabel[i] = tagDTOs.get(i).getLabel();
        }

        ChangeIssue changeIssue = changeManager.saveChangeIssueTags(workspaceId, issueId, tagsLabel);
        ChangeIssueDTO changeIssueDTO = mapper.map(changeIssue, ChangeIssueDTO.class);
        changeIssueDTO.setWritable(changeManager.isChangeItemWritable(changeIssue));
        return changeIssueDTO;
    }

    @POST
    @ApiOperation(value = "Attached a new tag to an issue",
            response = ChangeIssueDTO.class)
    @Path("{issueId}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeItemDTO addTag(@PathParam("workspaceId") String workspaceId,
                                @PathParam("issueId") int issueId,
                                @ApiParam(required = true, value = "Tag list to add") TagListDTO tagListDTO)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException {
        ChangeIssue changeIssue = changeManager.getChangeIssue(workspaceId, issueId);
        Set<Tag> tags = changeIssue.getTags();
        Set<String> tagLabels = new HashSet<>();

        for (TagDTO tagDTO : tagListDTO.getTags()) {
            tagLabels.add(tagDTO.getLabel());
        }

        for (Tag tag : tags) {
            tagLabels.add(tag.getLabel());
        }

        changeIssue = changeManager.saveChangeIssueTags(workspaceId, issueId, tagLabels.toArray(new String[tagLabels.size()]));
        ChangeIssueDTO changeIssueDTO = mapper.map(changeIssue, ChangeIssueDTO.class);
        changeIssueDTO.setWritable(changeManager.isChangeItemWritable(changeIssue));
        return changeIssueDTO;
    }

    @DELETE
    @ApiOperation(value = "Delete a tag attached to an issue",
            response = ChangeIssueDTO.class)
    @Path("{issueId}/tags/{tagName}")
    public Response removeTags(@PathParam("workspaceId") String workspaceId,
                               @PathParam("issueId") int issueId,
                               @PathParam("tagName") String tagName)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException {
        changeManager.removeChangeIssueTag(workspaceId, issueId, tagName);
        return Response.ok().build();
    }

    @PUT
    @ApiOperation(value = "Attach a document to an issue",
            response = ChangeIssueDTO.class)
    @Path("{issueId}/affected-documents")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeItemDTO saveAffectedDocuments(@PathParam("workspaceId") String workspaceId,
                                               @PathParam("issueId") int issueId,
                                               @ApiParam(required = true, value = "Document list to save as affected") DocumentIterationListDTO documentListDTO)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException {

        List<DocumentIterationDTO> documentIterationDTOs = documentListDTO.getDocuments();
        DocumentIterationKey[] links = createDocumentIterationKeys(documentIterationDTOs);

        ChangeIssue changeIssue = changeManager.saveChangeIssueAffectedDocuments(workspaceId, issueId, links);
        ChangeIssueDTO changeIssueDTO = mapper.map(changeIssue, ChangeIssueDTO.class);
        changeIssueDTO.setWritable(changeManager.isChangeItemWritable(changeIssue));
        return changeIssueDTO;
    }

    @PUT
    @ApiOperation(value = "Attach a part to an issue",
            response = ChangeIssueDTO.class)
    @Path("{issueId}/affected-parts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeItemDTO saveAffectedParts(@PathParam("workspaceId") String workspaceId,
                                           @PathParam("issueId") int issueId,
                                           @ApiParam(required = true, value = "Part list to save as affected") PartIterationListDTO partIterationListDTO)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException {

        List<PartIterationDTO> partIterationDTOs = partIterationListDTO.getParts();
        PartIterationKey[] links = createPartIterationKeys(partIterationDTOs);

        ChangeIssue changeIssue = changeManager.saveChangeIssueAffectedParts(workspaceId, issueId, links);
        ChangeIssueDTO changeIssueDTO = mapper.map(changeIssue, ChangeIssueDTO.class);
        changeIssueDTO.setWritable(changeManager.isChangeItemWritable(changeIssue));
        return changeIssueDTO;
    }

    @PUT
    @ApiOperation(value = "Update ACL of an issue",
            response = Response.class)
    @Path("{issueId}/acl")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateACL(@PathParam("workspaceId") String pWorkspaceId,
                              @PathParam("issueId") int issueId,
                              @ApiParam(required = true, value = "ACL rules to set") ACLDTO acl)
            throws EntityNotFoundException, UserNotActiveException, AccessRightException {
        if (!acl.getGroupEntries().isEmpty() || !acl.getUserEntries().isEmpty()) {

            Map<String, String> userEntries = new HashMap<>();
            Map<String, String> groupEntries = new HashMap<>();

            for (Map.Entry<String, ACL.Permission> entry : acl.getUserEntries().entrySet()) {
                userEntries.put(entry.getKey(), entry.getValue().name());
            }

            for (Map.Entry<String, ACL.Permission> entry : acl.getGroupEntries().entrySet()) {
                groupEntries.put(entry.getKey(), entry.getValue().name());
            }

            changeManager.updateACLForChangeIssue(pWorkspaceId, issueId, userEntries, groupEntries);
        } else {
            changeManager.removeACLFromChangeIssue(pWorkspaceId, issueId);
        }
        return Response.ok().build();
    }


    private DocumentIterationKey[] createDocumentIterationKeys(List<DocumentIterationDTO> dtos) {
        DocumentIterationKey[] data = new DocumentIterationKey[dtos.size()];
        int i = 0;
        for (DocumentIterationDTO dto : dtos) {
            data[i++] = new DocumentIterationKey(dto.getWorkspaceId(), dto.getDocumentMasterId(), dto.getVersion(), dto.getIteration());
        }

        return data;
    }

    private PartIterationKey[] createPartIterationKeys(List<PartIterationDTO> dtos) {
        PartIterationKey[] data = new PartIterationKey[dtos.size()];
        int i = 0;
        for (PartIterationDTO dto : dtos) {
            data[i++] = new PartIterationKey(dto.getWorkspaceId(), dto.getNumber(), dto.getVersion(), dto.getIteration());
        }

        return data;
    }
}