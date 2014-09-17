/*global define*/
'use strict';
define([
    'backbone',
    'common-objects/collections/file/attached_file_collection',
    'common-objects/utils/date'
], function (Backbone, AttachedFileCollection, Date) {
    var Template = Backbone.Model.extend({

        className: 'PartTemplate',

        initialize: function () {
            this.resetAttachedFile();
        },

        resetAttachedFile: function () {
            var fullName = this.get('attachedFile');
            if (fullName) {
                var attachedFile = {
                    fullName: fullName,
                    shortName: _.last(fullName.split('/')),
                    created: true
                };
                this._attachedFile = new AttachedFileCollection(attachedFile);
            } else {
                this._attachedFile = new AttachedFileCollection();
            }
        },

        parse: function (response) {
            return response;
        },

        toJSON: function () {
            return this.clone().set({attributeTemplates: _.reject(this.get('attributeTemplates'),
                function (attribute) {
                    return attribute.name === '';
                }
            )}, {silent: true}).attributes;
        },

        getUploadBaseUrl: function () {
            return APP_CONFIG.contextPath + '/files/' + this.get('workspaceId') + '/part-templates/' + this.get('id') + '/';
        },

        getId: function () {
            return this.get('id');
        },

        getMask: function () {
            return this.get('mask');
        },

        isIdGenerated: function () {
            return this.get('idGenerated');
        },

        getAuthorName: function () {
            return this.get('author').name;
        },

        getAuthorLogin: function () {
            return this.get('author').login;
        },

        getCreationDate: function () {
            return this.get('creationDate');
        },

        getFormattedCreationDate: function () {
            return Date.formatTimestamp(
                APP_CONFIG.i18n._DATE_FORMAT,
                this.getCreationDate()
            );
        },

        getPartType: function () {
            return this.get('partType');
        },

        url: function () {
            if (this.get('id')) {
                return APP_CONFIG.contextPath + '/api/workspaces/' + APP_CONFIG.workspaceId + '/part-templates/' + this.get('id');
            } else {
                return APP_CONFIG.contextPath + '/api/workspaces/' + APP_CONFIG.workspaceId + '/part-templates';
            }
        },

        generateIdUrl: function () {
            return APP_CONFIG.contextPath + '/api/workspaces/' + APP_CONFIG.workspaceId + '/part-templates/' + this.get('id') + '/generate_id';
        },

        getBaseName: function () {
            return APP_CONFIG.workspaceId + '/part-templates/' + this.get('id');
        },

        isAttributesLocked: function () {
            return this.get('attributesLocked');
        }

    });
    return Template;
});