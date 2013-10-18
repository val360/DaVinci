Ext.Loader.setConfig({
	enabled : true
});
Ext.Loader.setPath('Ext.ux', 'http://cdn.sencha.io/ext-4.0.7-gpl/examples/ux');
Ext.require([ 'Ext.grid.*', 'Ext.data.*', 'Ext.ux.grid.FiltersFeature', 'Ext.toolbar.Paging' ]);

//Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

Ext.define('User', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
	fields: [
		{name:'name', type:'string'},
		{name:'email', type:'string'},
		{name:'region', type:'string'},
		{name:'department', type:'string'}
	]
});

var regionPrefs = {};
regionPrefs.south = true;
regionPrefs.north = true;

var deptPrefs = {};
deptPrefs.sales = true;
deptPrefs.dev = true;

Ext.onReady(function() {

	Ext.QuickTips.init();

	// functions to display feedback
	function onButtonClick(btn) {
		console.log('Button Click', 'You clicked the "{0}" button.', btn.displayText || btn.text);
	}

	function onItemClick(item) {
		console.log('Menu Click', 'You clicked the "{0}" menu item.', item.text);
	}

	function onRegionItemCheck(item, checked) {
		console.log('Item Check', 'You {1} the "{0}" menu item.', item.text, checked ? 'checked' : 'unchecked');
		for(var r in regionPrefs) {
			if(regionPrefs.hasOwnProperty(r)) {
				if(item.text.toLowerCase().indexOf(r) != -1) {
					regionPrefs[r] = checked;
				}
			}
		}
		store.clearFilter();
		store.filter({
			filterFn: function(value) {
				var region = regionPrefs[value.data.region.toLowerCase()];
				var department = deptPrefs[value.data.department.toLowerCase()];
				return region && department;
			}
		});
	}

	function onDepartmentItemCheck(item, checked) {
		console.log('Item Check', 'You {1} the "{0}" menu item.', item.text, checked ? 'checked' : 'unchecked');
		for(var d in deptPrefs) {
			if(deptPrefs.hasOwnProperty(d)) {
				if(item.text.toLowerCase().indexOf(d) != -1) {
					deptPrefs[d] = checked;
				}
			}
		}
		store.clearFilter();
		store.filter({
			filterFn: function(value) {
				var region = regionPrefs[value.data.region.toLowerCase()];
				var department = deptPrefs[value.data.department.toLowerCase()];
				return region && department;
			}
		});
	}

	function onItemToggle(item, pressed) {
		console.log('Button Toggled', 'Button "{0}" was toggled to {1}.', item.text, pressed);
	}

	var store = Ext.create('Ext.data.Store', {
		autoLoad: true,
		model: 'User',
		storeId: 'UserStore',
        data: {
            success: true,
            users: [
                {
                    id: 1,
                    name: 'Ed',
                    region: 'South',
                    department: 'Sales',
                    email: 'ed@sencha.com'
                },
                {
                    id: 2,
                    name: 'Tommy',
                    region: 'North',
                    department: 'Dev',
                    email: 'tommy@sencha.com'
                }
            ]
        },
		proxy: {
			type: 'memory',
			/*type: 'ajax',
			url: '../users.json',*/
			reader: {
				type: 'json',
				root: 'users'
			}
		}
	});

	var filters = {
		ftype : 'filters',
		encode : true,
		local : false
	};

	var createColumns = function() {
		var columns = [
			{
				dataIndex : 'id',
				text : 'Id',
				filterable : true,
				width : 50
			},
			{
				dataIndex : 'name',
				text : 'Name',
				id : 'name',
				flex : 1,
				filter : {
					type : 'string'
				}
			},
			{
				dataIndex : 'email',
				text : 'Email',
				flex : 1
				,filter: { type: 'string' }
			}
			,
			{
				dataIndex : 'region',
				text : 'Region',
				flex : 1
				,filter: { type: 'string' }
			}
			,
			{
				dataIndex : 'department',
				text : 'Dept.',
				flex : 1
				,filter: { type: 'string' }
			}
		];
		return columns;
	};

	var regionMenu = Ext.create('Ext.menu.Menu', {
		id: 'regionMenu'
		,style: {
			overflow: 'visible'     // For the Combo popup
		}
		,items: [
			{
				text: 'North',
				checked: true,       // when checked has a boolean value, it is assumed to be a CheckItem
				checkHandler: onRegionItemCheck
			}
			,
			{
				text: 'South',
				checked: true,       // when checked has a boolean value, it is assumed to be a CheckItem
				checkHandler: onRegionItemCheck
			}
		]
	});
	var departmentMenu = Ext.create('Ext.menu.Menu', {
		id: 'departmentMenu'
		,style: {
			overflow: 'visible'     // For the Combo popup
		}
		,items: [
			{
				text: 'Sales',
				checked: true,       // when checked has a boolean value, it is assumed to be a CheckItem
				checkHandler: onDepartmentItemCheck
			}
			,
			{
				text: 'Dev',
				checked: true,       // when checked has a boolean value, it is assumed to be a CheckItem
				checkHandler: onDepartmentItemCheck
			}
		]
	});
	var grid = Ext.create('Ext.grid.Panel', {
		border : false,
		store : store,
		columns : createColumns(),
		loadMask : true
        ,selType: 'cellmodel'
        , plugins: [
            Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1
            })
        ]
		,dockedItems: [Ext.create('Ext.toolbar.Toolbar', {
			dock: 'top'
			,items: [{
				text: "Regions"
				,menu: regionMenu
			}
			,{
				text: "Departments"
				,menu: departmentMenu
			}]
		})]
		//,emptyText: 'No data'
	});

	Ext.create('Ext.Window', {
		title : 'Grid Filters Example',
		height : 400,
		width : 700,
		layout : 'fit',
		items : grid
	}).show();
});