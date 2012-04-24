/*
 * DocDoku, Professional Open Source
 * Copyright 2006, 2007, 2008, 2009, 2010, 2011, 2012 DocDoku SARL
 *
 * This file is part of DocDoku.
 *
 * DocDoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDoku is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.  
 *  
 * You should have received a copy of the GNU General Public License  
 * along with DocDoku.  If not, see <http://www.gnu.org/licenses/>.  
 */
package com.docdoku.server.rest;

import com.docdoku.core.common.User;
import com.docdoku.core.common.UserGroup;
import com.docdoku.core.common.Workspace;
import com.docdoku.core.document.DocumentIterationKey;
import com.docdoku.core.document.DocumentMaster;
import com.docdoku.core.document.DocumentMasterKey;
import com.docdoku.core.meta.*;
import com.docdoku.core.security.ACL;
import com.docdoku.core.security.ACLUserEntry;
import com.docdoku.core.security.ACLUserGroupEntry;
import com.docdoku.core.security.UserGroupMapping;
import com.docdoku.core.services.ICommandLocal;
import com.docdoku.gwt.explorer.shared.ACLDTO;
import com.docdoku.server.rest.dto.*;
import com.docdoku.server.rest.exceptions.ApplicationException;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

@Stateless
@Path("workspaces/{workspaceId}/documents")
@DeclareRoles(UserGroupMapping.REGULAR_USER_ROLE_ID)
@RolesAllowed(UserGroupMapping.REGULAR_USER_ROLE_ID)
public class DocumentResource {

    @EJB
    private ICommandLocal commandService;
    @Context
    private UriInfo context;
    private Mapper mapper;

    public DocumentResource() {
    }

    @PostConstruct
    public void init() {
        mapper = DozerBeanMapperSingletonWrapper.getInstance();
    }

    /**
     * Retrieves representation of an instance of
     * com.docdoku.server.rest.DocumentResource
     *
     * @return an instance of com.docdoku.core.document.DocumentMaster
     */
    @GET
    @Produces("application/json;charset=UTF-8")
    public DocumentMasterDTO[] getRootDocuments(@PathParam("workspaceId") String workspaceId, @QueryParam("tag") String label, @QueryParam("path") String path) {

        try {

            String pCompletePath = Tools.stripTrailingSlash(workspaceId);
            DocumentMaster[] docM = commandService.findDocumentMastersByFolder(pCompletePath);
            DocumentMasterDTO[] dtos = new DocumentMasterDTO[docM.length];

            for (int i = 0; i < docM.length; i++) {
                dtos[i] = mapper.map(docM[i], DocumentMasterDTO.class);
                dtos[i].setPath(docM[i].getLocation().getCompletePath());
                dtos[i] = Tools.createLightDocumentMasterDTO(dtos[i]);
            }

            return dtos;
        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }

    }

//    @GET
//    @Path()
//    @Produces("application/json;charset=UTF-8")
//    public DocumentMasterDTO[] getIterationChangeEventSubscriptions(@PathParam("workspaceId") String workspaceId) {
//
//        try {
//
//            DocumentMasterKey[] docMKey = commandService.getIterationChangeEventSubscriptions(workspaceId);
//            DocumentMasterDTO[] data = new DocumentMasterDTO[docMKey.length];
//            
//            for (int i = 0; i < docMKey.length; i++) {
//                DocumentMasterDTO dto = new DocumentMasterDTO();
//                dto.setWorkspaceID(docMKey[i].getWorkspaceId());
//                dto.setId(docMKey[i].getId());
//                dto.setReference(docMKey[i].getId());
//                dto.setVersion(docMKey[i].getVersion());
//                data[i] = dto;
//            }       
//
//            return data;
//            
//        } catch (com.docdoku.core.services.ApplicationException ex) {
//            throw new RESTException(ex.toString(), ex.getMessage());
//        }
//
//    }    
//
//    @GET
//    @Path()
//    @Produces("application/json;charset=UTF-8")
//    public DocumentMasterDTO[] getStateChangeEventSubscriptions(@PathParam("workspaceId") String workspaceId) {
//
//        try {
//
//            DocumentMasterKey[] docMKey = commandService.getStateChangeEventSubscriptions(workspaceId);
//            DocumentMasterDTO[] data = new DocumentMasterDTO[docMKey.length];
//            
//            for (int i = 0; i < docMKey.length; i++) {
//                DocumentMasterDTO dto = new DocumentMasterDTO();
//                dto.setWorkspaceID(docMKey[i].getWorkspaceId());
//                dto.setId(docMKey[i].getId());
//                dto.setReference(docMKey[i].getId());
//                dto.setVersion(docMKey[i].getVersion());
//                data[i] = dto;
//            }       
//
//            return data;
//            
//        } catch (com.docdoku.core.services.ApplicationException ex) {
//            throw new RESTException(ex.toString(), ex.getMessage());
//        }
//
//    }    
//    
    @GET
    @Path("checkedout")
    @Produces("application/json;charset=UTF-8")
    public DocumentMasterDTO[] getCheckedOutDocMs(@PathParam("workspaceId") String workspaceId) throws ApplicationException {

        try {
            DocumentMaster[] checkedOutdocMs = commandService.getCheckedOutDocumentMasters(workspaceId);
            DocumentMasterDTO[] checkedOutdocMsDTO = new DocumentMasterDTO[checkedOutdocMs.length];

            for (int i = 0; i < checkedOutdocMs.length; i++) {
                checkedOutdocMsDTO[i] = mapper.map(checkedOutdocMs[i], DocumentMasterDTO.class);
                checkedOutdocMsDTO[i].setPath(checkedOutdocMs[i].getLocation().getCompletePath());
                checkedOutdocMsDTO[i] = Tools.createLightDocumentMasterDTO(checkedOutdocMsDTO[i]);
            }

            return checkedOutdocMsDTO;

        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    @GET
    @Path("{docKey}")
    @Produces("application/json;charset=UTF-8")
    public DocumentMasterDTO getDocM(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {


        int lastDash = docKey.lastIndexOf('-');
        String id = docKey.substring(0, lastDash);
        String version = docKey.substring(lastDash + 1, docKey.length());


        try {
            DocumentMaster docM = commandService.getDocumentMaster(new DocumentMasterKey(workspaceId, id, version));
            DocumentMasterDTO docMsDTO = mapper.map(docM, DocumentMasterDTO.class);
            docMsDTO.setPath(docM.getLocation().getCompletePath());
            docMsDTO.setLifeCycleState(docM.getLifeCycleState());

            return docMsDTO;

        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    /**
     * PUT method for updating or creating an instance of DocumentResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    @Path("{docKey}/checkin")
    public DocumentMasterDTO checkInDocument(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {
        try {

            int lastDash = docKey.lastIndexOf('-');
            String docId = docKey.substring(0, lastDash);
            String docVersion = docKey.substring(lastDash + 1, docKey.length());

            DocumentMaster docM = commandService.checkIn(new DocumentMasterKey(workspaceId, docId, docVersion));

            DocumentMasterDTO docMsDTO = mapper.map(docM, DocumentMasterDTO.class);
            docMsDTO.setPath(docM.getLocation().getCompletePath());

            return docMsDTO;

        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    @PUT
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    @Path("{docKey}/checkout")
    public DocumentMasterDTO checkOutDocument(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {
        try {

            int lastDash = docKey.lastIndexOf('-');
            String docId = docKey.substring(0, lastDash);
            String docVersion = docKey.substring(lastDash + 1, docKey.length());

            DocumentMaster docM = commandService.checkOut(new DocumentMasterKey(workspaceId, docId, docVersion));

            DocumentMasterDTO docMsDTO = mapper.map(docM, DocumentMasterDTO.class);
            docMsDTO.setPath(docM.getLocation().getCompletePath());
            docMsDTO.setLifeCycleState(docM.getLifeCycleState());

            return docMsDTO;

        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    @PUT
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    @Path("{docKey}/undocheckout")
    public DocumentMasterDTO undoCheckOutDocument(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {
        try {

            int lastDash = docKey.lastIndexOf('-');
            String docId = docKey.substring(0, lastDash);
            String docVersion = docKey.substring(lastDash + 1, docKey.length());

            DocumentMaster docM = commandService.undoCheckOut(new DocumentMasterKey(workspaceId, docId, docVersion));

            DocumentMasterDTO docMsDTO = mapper.map(docM, DocumentMasterDTO.class);
            docMsDTO.setPath(docM.getLocation().getCompletePath());
            docMsDTO.setLifeCycleState(docM.getLifeCycleState());

            return docMsDTO;

        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    @PUT
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    @Path("{docKey}/move")
    public DocumentMasterDTO moveDocument(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey, DocumentCreationDTO docCreationDTO) {
        try {

            int lastDash = docKey.lastIndexOf('-');
            String docId = docKey.substring(0, lastDash);
            String docVersion = docKey.substring(lastDash + 1, docKey.length());
            String parentFolderPath = docCreationDTO.getPath();
            String newCompletePath = Tools.stripTrailingSlash(workspaceId + "/" + parentFolderPath);

            DocumentMasterKey docMsKey = new DocumentMasterKey(workspaceId, docId, docVersion);
            DocumentMaster movedDocumentMaster = commandService.moveDocumentMaster(newCompletePath, docMsKey);

            DocumentMasterDTO docMsDTO = mapper.map(movedDocumentMaster, DocumentMasterDTO.class);
            docMsDTO.setPath(movedDocumentMaster.getLocation().getCompletePath());
            docMsDTO.setLifeCycleState(movedDocumentMaster.getLifeCycleState());

            return docMsDTO;

        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    @PUT
    @Path("{docKey}/notification/iterationChange/subscribe")
    public Response subscribeToIterationChangeEvent(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {
        try {
            int lastDash = docKey.lastIndexOf('-');
            String docId = docKey.substring(0, lastDash);
            String docVersion = docKey.substring(lastDash + 1, docKey.length());

                commandService.subscribeToIterationChangeEvent(new DocumentMasterKey(workspaceId, docId, docVersion));

            return Response.ok().build();
        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }
    
     @PUT
    @Path("{docKey}/notification/iterationChange/unsubscribe")
    public Response unsubscribeToIterationChangeEvent(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {
        try {
            int lastDash = docKey.lastIndexOf('-');
            String docId = docKey.substring(0, lastDash);
            String docVersion = docKey.substring(lastDash + 1, docKey.length());

                commandService.unsubscribeToIterationChangeEvent(new DocumentMasterKey(workspaceId, docId, docVersion));


            return Response.ok().build();
        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }   

    @PUT
    @Path("{docKey}/notification/stateChange/subscribe")
    public Response subscribeToStateChangeEvent(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {
        try {
            int lastDash = docKey.lastIndexOf('-');
            String docId = docKey.substring(0, lastDash);
            String docVersion = docKey.substring(lastDash + 1, docKey.length());

            commandService.subscribeToStateChangeEvent(new DocumentMasterKey(workspaceId, docId, docVersion));

            return Response.ok().build();
        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }
    
    @PUT
    @Path("{docKey}/notification/stateChange/unsubscribe")
    public Response unsubscribeToStateChangeEvent(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {
        try {
            int lastDash = docKey.lastIndexOf('-');
            String docId = docKey.substring(0, lastDash);
            String docVersion = docKey.substring(lastDash + 1, docKey.length());

            commandService.unsubscribeToStateChangeEvent(new DocumentMasterKey(workspaceId, docId, docVersion));

            return Response.ok().build();
        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }    

    @PUT
    @Consumes("application/json;charset=UTF-8")
    @Path("{docKey}/iterations/{docIteration}")
    public DocumentMasterDTO updateDocMs(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey, @PathParam("docIteration") String docIteration, DocumentDTO data) {
        try {

            int lastDash = docKey.lastIndexOf('-');
            String pID = docKey.substring(0, lastDash);
            String pVersion = docKey.substring(lastDash + 1, docKey.length());
            String pWorkspaceId = workspaceId;
            String pRevisionNote = data.getRevisionNote();
            int pIteration = Integer.parseInt(docIteration);

            List<DocumentDTO> linkedDocs = data.getLinkedDocuments();
            DocumentIterationKey[] links = null;
            if (linkedDocs != null) {
                links = createDocumentIterationKey(linkedDocs);
            }


            List<InstanceAttributeDTO> instanceAttributes = data.getInstanceAttributes();
            InstanceAttribute[] attributes = null;
            if (instanceAttributes != null) {
                attributes = createInstanceAttribute(instanceAttributes);
            }

            DocumentMaster docM = commandService.updateDocument(new DocumentIterationKey(pWorkspaceId, pID, pVersion, pIteration), pRevisionNote, attributes, links);
            DocumentMasterDTO docMsDTO = mapper.map(docM, DocumentMasterDTO.class);

            return docMsDTO;
        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }

    }

    @PUT
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    @Path("{docKey}/newVersion")
    public DocumentMasterDTO[] createNewVersion(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey, DocumentCreationDTO docCreationDTO) {

        int lastDash = docKey.lastIndexOf('-');
        String pID = docKey.substring(0, lastDash);
        String pVersion = docKey.substring(lastDash + 1, docKey.length());
        String pWorkspaceId = workspaceId;
        String pTitle = docCreationDTO.getTitle();
        String pDescription = docCreationDTO.getDescription();
        String pWorkflowModelId = docCreationDTO.getWorkflowModelId();


        /*
         * Null value for test purpose only
         */
        ACLDTO acl = null;

        try {

            ACLUserEntry[] userEntries = null;
            ACLUserGroupEntry[] userGroupEntries = null;
            if (acl != null) {
                userEntries = new ACLUserEntry[acl.getUserEntries().size()];
                userGroupEntries = new ACLUserGroupEntry[acl.getGroupEntries().size()];
                int i = 0;
                for (Map.Entry<String, ACLDTO.Permission> entry : acl.getUserEntries().entrySet()) {
                    userEntries[i] = new ACLUserEntry();
                    userEntries[i].setPrincipal(new User(new Workspace(pWorkspaceId), entry.getKey()));
                    userEntries[i++].setPermission(ACL.Permission.valueOf(entry.getValue().name()));
                }
                i = 0;
                for (Map.Entry<String, ACLDTO.Permission> entry : acl.getGroupEntries().entrySet()) {
                    userGroupEntries[i] = new ACLUserGroupEntry();
                    userGroupEntries[i].setPrincipal(new UserGroup(new Workspace(pWorkspaceId), entry.getKey()));
                    userGroupEntries[i++].setPermission(ACL.Permission.valueOf(entry.getValue().name()));
                }
            }
            DocumentMaster[] docM = commandService.createVersion(new DocumentMasterKey(pWorkspaceId, pID, pVersion), pTitle, pDescription, pWorkflowModelId, userEntries, userGroupEntries);
            DocumentMasterDTO[] dtos = new DocumentMasterDTO[docM.length];

            for (int i = 0; i < docM.length; i++) {
                dtos[i] = mapper.map(docM[i], DocumentMasterDTO.class);
                dtos[i].setPath(docM[i].getLocation().getCompletePath());
                dtos[i].setLifeCycleState(docM[i].getLifeCycleState());
                dtos[i] = Tools.createLightDocumentMasterDTO(dtos[i]);
            }

            return dtos;

        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    @PUT
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    @Path("{docKey}/tags")
    public DocumentMasterDTO saveDocTags(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey, TagDTO[] tagDtos) {

        int lastDash = docKey.lastIndexOf('-');
        String id = docKey.substring(0, lastDash);
        String version = docKey.substring(lastDash + 1, docKey.length());

        String[] tagsLabel = new String[tagDtos.length];
        for (int i = 0; i < tagDtos.length; i++) {
            tagsLabel[i] = tagDtos[i].getLabel();
        }

        try {
            DocumentMaster docMs = commandService.saveTags(new DocumentMasterKey(workspaceId, id, version), tagsLabel);
            DocumentMasterDTO docMsDto = mapper.map(docMs, DocumentMasterDTO.class);
            docMsDto.setPath(docMs.getLocation().getCompletePath());
            docMsDto.setLifeCycleState(docMs.getLifeCycleState());

            return docMsDto;
        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    /**
     *
     * POST method
     *
     */
    @POST
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    public DocumentMasterDTO createRootDocumentMaster(@PathParam("workspaceId") String workspaceId, DocumentCreationDTO docCreationDTO) {

        String pDocMID = docCreationDTO.getReference();
        String pTitle = docCreationDTO.getTitle();
        String pDescription = docCreationDTO.getDescription();
        String pParentFolder = Tools.stripTrailingSlash(workspaceId);
        String pWorkflowModelId = docCreationDTO.getWorkflowModelId();
        String pDocMTemplateId = docCreationDTO.getTemplateId();



        /*
         * Null value for test purpose only
         */
        ACLDTO acl = null;


        try {
            ACLUserEntry[] userEntries = null;
            ACLUserGroupEntry[] userGroupEntries = null;
            if (acl != null) {
                userEntries = new ACLUserEntry[acl.getUserEntries().size()];
                userGroupEntries = new ACLUserGroupEntry[acl.getGroupEntries().size()];
                int i = 0;
                for (Map.Entry<String, ACLDTO.Permission> entry : acl.getUserEntries().entrySet()) {
                    userEntries[i] = new ACLUserEntry();
                    userEntries[i].setPrincipal(new User(new Workspace(workspaceId), entry.getKey()));
                    userEntries[i++].setPermission(ACL.Permission.valueOf(entry.getValue().name()));
                }
                i = 0;
                for (Map.Entry<String, ACLDTO.Permission> entry : acl.getGroupEntries().entrySet()) {
                    userGroupEntries[i] = new ACLUserGroupEntry();
                    userGroupEntries[i].setPrincipal(new UserGroup(new Workspace(workspaceId), entry.getKey()));
                    userGroupEntries[i++].setPermission(ACL.Permission.valueOf(entry.getValue().name()));
                }
            }

           DocumentMaster createdDocMs =  commandService.createDocumentMaster(pParentFolder, pDocMID, pTitle, pDescription, pDocMTemplateId, pWorkflowModelId, userEntries, userGroupEntries);
           DocumentMasterDTO docMsDTO = mapper.map(createdDocMs, DocumentMasterDTO.class);
           docMsDTO.setPath(createdDocMs.getLocation().getCompletePath());
           docMsDTO.setLifeCycleState(createdDocMs.getLifeCycleState());

            return docMsDTO;


        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    /**
     * DELETE method for deleting an instance of DocumentResource
     *
     * @param parent folder path
     * @return the array of the documents that have also been deleted
     */
    @DELETE
    @Path("{docKey}")
    @Produces("application/json;charset=UTF-8")
    public Response deleteDocument(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey) {

        int lastDash = docKey.lastIndexOf('-');
        String id = docKey.substring(0, lastDash);
        String version = docKey.substring(lastDash + 1, docKey.length());

        try {
            commandService.deleteDocumentMaster(new DocumentMasterKey(workspaceId, id, version));
            return Response.status(Response.Status.OK).build();
        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    @DELETE
    @Consumes("application/json;charset=UTF-8")
    @Path("{docKey}/iterations/{docIteration}/files/{fileName}")
    public Response removeAttachedFile(@PathParam("workspaceId") String workspaceId, @PathParam("docKey") String docKey, @PathParam("docIteration") String docIteration, @PathParam("fileName") String fileName) {
        try {
            int lastDash = docKey.lastIndexOf('-');
            String id = docKey.substring(0, lastDash);
            String version = docKey.substring(lastDash + 1, docKey.length());

            String fileFullName = workspaceId + "/documents/" + id + "/" + version + "/" + docIteration + "/" + fileName;
            System.out.println("fileFullName : " + fileFullName);

            commandService.removeFileFromDocument(fileFullName);
            return Response.ok().build();

        } catch (com.docdoku.core.services.ApplicationException ex) {
            throw new RestApiException(ex.toString(), ex.getMessage());
        }
    }

    private InstanceAttribute[] createInstanceAttribute(InstanceAttributeDTO[] dtos) {
        if (dtos == null) {
            return null;
        }
        InstanceAttribute[] data = new InstanceAttribute[dtos.length];

        for (int i = 0; i < dtos.length; i++) {
            data[i] = createObject(dtos[i]);
        }

        return data;
    }
    
    private InstanceAttribute[] createInstanceAttribute(List<InstanceAttributeDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        InstanceAttribute[] data = new InstanceAttribute[dtos.size()];
        int i=0;
        for (InstanceAttributeDTO dto:dtos) {
            data[i++] = createObject(dto);
        }

        return data;
    }

    private InstanceAttribute createObject(InstanceAttributeDTO dto) {
        if (dto.getType().equals(InstanceAttributeDTO.Type.BOOLEAN)) {
            InstanceBooleanAttribute attr = new InstanceBooleanAttribute();
            attr.setName(dto.getName());
            attr.setBooleanValue(Boolean.parseBoolean(dto.getValue()));
            return attr;
        } else if (dto.getType().equals(InstanceAttributeDTO.Type.TEXT)) {
            InstanceTextAttribute attr = new InstanceTextAttribute();
            attr.setName(dto.getName());
            attr.setTextValue((String) dto.getValue());
            return attr;
        } else if (dto.getType().equals(InstanceAttributeDTO.Type.NUMBER)) {
            InstanceNumberAttribute attr = new InstanceNumberAttribute();
            attr.setName(dto.getName());
            attr.setNumberValue(Float.parseFloat(dto.getValue()));
            return attr;
        } else if (dto.getType().equals(InstanceAttributeDTO.Type.DATE)) {
            InstanceDateAttribute attr = new InstanceDateAttribute();
            attr.setName(dto.getName());
            attr.setDateValue(new Date(Long.parseLong(dto.getValue())));
            return attr;
        } else if (dto.getType().equals(InstanceAttributeDTO.Type.URL)) {
            InstanceURLAttribute attr = new InstanceURLAttribute();
            attr.setName(dto.getName());
            attr.setUrlValue(dto.getValue());
            return attr;
        } else {
            throw new IllegalArgumentException("Instance attribute not supported");
        }
    }

    private DocumentIterationKey[] createDocumentIterationKey(DocumentDTO[] dtos) {
        DocumentIterationKey[] data = new DocumentIterationKey[dtos.length];

        for (int i = 0; i < dtos.length; i++) {
            data[i] = createObject(dtos[i]);
        }

        return data;
    }
    
        private DocumentIterationKey[] createDocumentIterationKey(List<DocumentDTO> dtos) {
        DocumentIterationKey[] data = new DocumentIterationKey[dtos.size()];
        int i= 0;
        for (DocumentDTO dto:dtos) {
            data[i++] = createObject(dto);
        }

        return data;
    }

    private DocumentIterationKey createObject(DocumentDTO dto) {
        return new DocumentIterationKey(dto.getWorkspaceId(), dto.getDocumentMasterId(), dto.getDocumentMasterVersion(), dto.getIteration());
    }
}
