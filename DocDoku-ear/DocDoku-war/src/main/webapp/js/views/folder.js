var FolderView = Backbone.View.extend({
	tagName: "li",
	events: {
		"click .name": "toggle",
		"click  .actions .delete": "delete",
	},
	initialize: function () {
		_.bindAll(this,
			"template", "render",
			"onFolderListReset", "createFolderView",
			"open", "close", "toggle",
			"mouseenter", "mouseleave",
			"delete");
		this.folderViews = [];
		this.documentViews = [];
		this.render();
		this.model.folders.bind("reset", this.onFolderListReset);
	},
	onFolderListReset: function () {
		_.each(this.folderViews, function(view) {
			view.remove();
		});
		this.model.folders.each(this.createFolderView);
		$(this.el).children(".subfolders").first().collapse("show");
	},
	createFolderView: function (folder) {
		folder.folders = new FolderList();
		folder.folders.url = "/api/folders/" + folder.get("completePath");
		folder.parent = this.model;
		view = new FolderView({model:folder});
		$(this.el).children(".subfolders").first().append(view.el);
		this.folderViews.push(view);
	},
	createDocumentView: function (doc) {
	},
	template: function(data) {
		data.view_cid = this.cid;
		return Mustache.render(
			$("#folder-tpl").html(),
			data
		);
	},
	render: function () {
		$(this.el).html(this.template(this.model.toJSON()));
		$(this.el).addClass("folder");
		$(this.el).children('.actions').hide();
		$(this.el).hover(this.mouseenter, this.mouseleave);
		if (this.model.get("isHome")) {
			$(this.el).addClass("home");
			// no delete action on home
			$(this.el).find(".actions .delete").remove();
		}
		if (this.model.parent == undefined) {
			// no delete action on root folder
			$(this.el).find(".actions .delete").remove();
		}
		if ($(this.el).find('.dropdown-menu').children().length < 1) {
			$(this.el).find(".actions").remove();
		}
		return this;
	},
	open: function () {
		$(this.el).addClass("open");
		this.model.folders.fetch();
	},
	close: function () {
		$(this.el).children(".subfolders").collapse("hide");
		$(this.el).removeClass("open");
	},
	toggle : function () {
		$(this.el).hasClass("open") ? this.close() : this.open();
		return false;
	},
	mouseenter: function () {
		$(this.el).children('.actions').show();
		return false;
	},
	mouseleave: function () {
		$(this.el).children('.actions').hide();
		if ($(this.el).find(".actions").hasClass("open")) {
			$(this.el).find(".actions").click();
		}
		return false;
	},
	delete: function () {
		if (confirm("Supprimer le dossier " + this.model.name + "?")) {
			this.model.destroy();
			this.remove();
		}
		return false;
	}
});
