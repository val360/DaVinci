/**
 * Created with IntelliJ IDEA.
 * User: val
 * Date: 2/1/13
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', '../extjs/examples/ux');
Ext.require([ 'Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.state.*' ]);

//app require
/*Ext.require([
    'Ext.data.*', 'Ext.grid.*', 'Ext.ux.CheckColumn'
]);*/

/*Ext.application({
    name: 'davinci.admin',
    appFolder: 'app',
    controllers: [ 'Entries' ],

    launch: function () {
        Ext.create('Ext.container.Viewport', {
            items: [{ xtype: 'entryGrid' }]
        });
    }
});*/

Ext.onReady(function () {
	Ext.QuickTips.init();

	// models
	var myFields = [];
    myFields.push(new Ext.data.Field({
		name: 'entry',
		type: 'string'
	}));
	myFields.push(new Ext.data.Field({
		name: 'header',
		type: 'string'
	}));
    myFields.push(new Ext.data.Field({
        name: 'total',
        type: 'string'
    }));
	myFields.push(new Ext.data.Field({
		name: 'region',
		type: 'string'
	}));
	myFields.push(new Ext.data.Field({
		name: 'department',
		type: 'string'
	}));
    myFields.push(new Ext.data.Field({
        name: 'fieldId',
        type: 'string'
    }));
    myFields.push(new Ext.data.Field({
        name: 'type' ,
        type: 'string'
    }));
	for(i=0;i<52;i++) {
		var this_field = new Ext.data.Field({
			name: 'week' + (i+1),
			type: 'string'
		});
		myFields.push(this_field);
	}
	Ext.define('Entry', {
		extend: 'Ext.data.Model',
		fields: myFields
		//validations
	});
	Ext.define('Department', {
		extend: 'Ext.data.Model',
		fields: [ { name: 'name', type: 'string'} ]
	});
	Ext.define('Region', {
		extend: 'Ext.data.Model',
		fields: [ { name: 'name', type: 'string'} ]
	});
	Ext.define('User', {
		extend: 'Ext.data.Model',
		fields: [
			{ name: 'username', type: 'string'},
			{ name: 'password', type: 'string'},
			{ name: 'region', type: 'string'},
			{ name: 'department', type: 'string'},
			{ name: 'email', type: 'string'},
			{ name: 'admin', type: 'boolean', defaultValue: true, convert: null}
		]
	});
	Ext.define('Field', {
		extend: 'Ext.data.Model',
		fields: [
			{ name: 'name', type: 'string'},
			{ name: 'type', type: 'string'},
			{ name: 'region', type: 'string'},
			{ name: 'department', type: 'string'}
		]
	});
    Ext.define('Calc', {
        extend: 'Ext.data.Model'
        ,fields: [
            {name: 'name', type: 'string'}
            ,{name: 'definition', type: 'string'}
            ,{name: 'type', type: 'string'}
            ,{name: 'region', type: 'string'}
            ,{name: 'department', type: 'string'}
            ,{name: 'scope', type: 'string'}
        ]
    });
    Ext.define('Metric', {
        extend: 'Ext.data.Model', fields: [
            {name: 'name', type: 'string'}
            ,{name: 'value', type: 'string'}
        ]
    });

	// stores
	var dataStore = Ext.create('Ext.data.Store', {
		model: 'Entry',
		autoLoad: true,
		proxy: {
			type: 'ajax',
			api: { read: '../../DaVinci/EntryData' },
			model: 'Entry',
			reader: {
				type: 'json',
				root: 'entries',
				successProperty: 'success'
			}
		},
		listeners: {
			'exception': function (proxy, response, operation) {  //unused: eOpts
				Ext.Msg.alert('', operation.error.statusText + ': ' + response.responseText);
			}
		}
	});
    var departmentStore = Ext.create('Ext.data.ArrayStore', {
        model: 'Department',
        autoLoad: false,
        proxy: {
            type: 'ajax',
            model: 'Department',
            reader: {
                type: 'json',
                root: 'departments',
                successProperty: 'success'
            },
            listeners: {
                'exception': function (proxy, response, operation) {
                    Ext.Msg.alert('', operation.error.statusText + ': ' + response.responseText);
                }
            }
        }
    });
    var regionStore = Ext.create('Ext.data.ArrayStore', {
        model: 'Region',
        autoLoad: false,
        proxy: {
            type: 'ajax',
            model: 'Region',
            reader: {
                type: 'json',
                root: 'regions',
                successProperty: 'success'
            },
            listeners: {
                'exception': function (proxy, response, operation) {
                    Ext.Msg.alert('', operation.error.statusText + ': ' + response.responseText);
                }
            }
        }
    });
    var	fieldStore = Ext.create('Ext.data.ArrayStore', {
        model: 'Field',
        autoLoad: false,
        autoSync: true,
        proxy: {
            type: 'ajax',
            api: {
                create: '../../DaVinci/SetupInfo?addfield'
            },
            reader: {
                type: 'json',
                root: 'fields',
                successProperty: 'success'
            },
            listeners: {
                'exception': function (proxy, response, operation) {
                    Ext.Msg.alert('', operation.error.statusText + ': ' + response.responseText);
                }
            }
        }
    });
    var userStore = Ext.create('Ext.data.Store', {
        model: 'User',
        autoLoad: true,
        autoSync: true,
        proxy: {
            type: 'ajax',
            api: {
                create: '../../DaVinci/SetupInfo?adduser',
                read: '../../DaVinci/SetupInfo',
                update: '../../DaVinci/SetupInfo?updateuser',
                destroy: '../../DaVinci/SetupInfo?removeuser'
            },
            model: 'User',
            reader: {
                type: 'json',
                root: 'users',
                successProperty: 'success'
            }
        },
        listeners: {
            load: function (sender, node, records) {
                var json = sender.proxy.reader.jsonData;
                fieldStore.loadRawData(json);
                regionStore.loadRawData(json);
                departmentStore.loadRawData(json);
                calcStore.loadRawData(json);
                metricStore.loadRawData(json);
                createDeptMenuItemsAndPrefs();
                createRegionMenuItemsAndPrefs();
            }

        }
    });
    var typeStore = Ext.create('Ext.data.Store', {
        fields: ['name'],
        data: [
            {"name": "dollar"},
            {"name": "number"},
            {"name": "text"}
        ]
    });
    var scopeStore = Ext.create('Ext.data.Store', {
        fields: ['name'],
        data: [
            {"name": "region"},
            {"name": "department"},
            {"name": "both"}
        ]
    });
    var calcStore = Ext.create('Ext.data.ArrayStore', {
        model: 'Calc',
        autoLoad: false,
        autoSync: true,
        proxy: {
            type: 'ajax',
            api: {
                create: '../../DaVinci/SetupInfo?addcalc'
            },
            reader: {
                type: 'json',
                root: 'calculations',
                successProperty: 'success'
            },
            listeners: {
                'exception': function (proxy, response, operation) {
                    Ext.Msg.alert('', operation.error.statusText + ': ' + response.responseText);
                }
            }
        }
    });
    var metricStore = Ext.create('Ext.data.ArrayStore', {
        model: 'Metric',
        autoLoad: false,
        autoSync: true,
        proxy: {
            type: 'ajax',
            api: {
                create: '../../DaVinci/SetupInfo?addmetric'
            },
            reader: {
                type: 'json',
                root: 'metrics',
                successProperty: 'success'
            },
            listeners: {
                'exception': function (proxy, response, operation) {
                    Ext.Msg.alert('', operation.error.statusText + ': ' + response.responseText);
                }
            }
        }
    });

	// layout
	//DATA TAB
	function onDepartmentItemCheck(item, checked) {
		console.log('Item Check', 'You {1} the "{0}" menu item.', item.text, checked ? 'checked' : 'unchecked');
		for(var d in deptPrefs) {
			if(deptPrefs.hasOwnProperty(d)) {
				if(item.text.indexOf(d) != -1) {
					deptPrefs[d] = checked;
				}
			}
		}
		dataStore.clearFilter();
		dataStore.filter({
			filterFn: function(value) {
				var region = regionPrefs[value.data.region];
				var department = deptPrefs[value.data.department];
				return region && department;
			}
		});
	}
	var deptPrefs = {};
	function createDeptMenuItemsAndPrefs() {
		var array = departmentStore.data.items;
		var size = array.length;
		for(var i = 0; i < size; i++) {
			var obj = array[i];
			departmentMenu.add({
				text: obj.data.name
				,checked: true
				,checkHandler: onDepartmentItemCheck
			});
			deptPrefs[obj.data.name] = true;
		}
	}
	var departmentMenu = Ext.create('Ext.menu.Menu', {
		id: 'departmentMenu'
		,style: {
			overflow: 'visible'
		}
	});
	function onRegionItemCheck(item, checked) {
		console.log('Item Check', 'You {1} the "{0}" menu item.', item.text, checked ? 'checked' : 'unchecked');
		for(var r in regionPrefs) {
			if(regionPrefs.hasOwnProperty(r)) {
				if(item.text.indexOf(r) != -1) {
					regionPrefs[r] = checked;
				}
			}
		}
		dataStore.clearFilter();
		dataStore.filter({
			filterFn: function(value) {
				var region = regionPrefs[value.data.region];
				var department = deptPrefs[value.data.department];
				return region && department;
			}
		});
	}
	var regionPrefs = {};
	function createRegionMenuItemsAndPrefs() {
		var array = regionStore.data.items;
		var size = array.length;
		for(var i = 0; i < size; i++) {
			var obj = array[i];
			regionMenu.add({
				text: obj.data.name
				,checked: true
				,checkHandler: onRegionItemCheck
			});
			regionPrefs[obj.data.name] = true;
		}
	}
	var regionMenu = Ext.create('Ext.menu.Menu', {
		id: 'regionMenu'
		,style: {
			overflow: 'visible'
		}
	});
	var exportButton = Ext.create('Ext.Button', {
		text: 'Export'
		,iconCls: 'icon-excel'
		,handler: function() {
			hiddenForm.getForm().submit();
		}
	});
	var dataToolbar = Ext.create('Ext.toolbar.Toolbar', {
		region: 'north'
		,items: [
			{
				text : 'Departments'
				,menu: departmentMenu
			}
			,{
				text: 'Regions'
				,menu: regionMenu
			}
			,'->'   // begin using the right-justified button container
			,exportButton
		]
	});
	var entryGrid = Ext.create('Ext.grid.Panel', {
		region: 'west',
		store: dataStore,
		border: false,
		stateful: true,
		stateId: 'stateEntryGrid',
		columnLines: true,
		columns: [{
			text: 'Entry',
			dataIndex: 'entry',
			width: 200,
			//locked: true,
			sortable: false,
			renderer: function(value, metadata, record) {
				if(record.data.header === 'department') {
                    if (value.indexOf("Total") !== -1) {
                        metadata.css = 'header-blue';
                    }
                    else {
					    metadata.css = 'header-grey';
                    }
                    return "<b>" + value + "</b>"
				} else if(record.data.header === 'region') {
					metadata.css = 'header-grey2'
					return "<i>" + value + "</i>"
				} else {
                    return value;

				}
			}
		}]
		,viewConfig: {
			stripeRows: true
		}
	});
	// create the columns dynamically.
	var dataColumns = [];
	for(var i = 0; i < 52; i++) {
		var this_column = new Ext.grid.column.Column({
			text: 'Week ' + (i + 1),
			dataIndex: 'week' + (i + 1)
			,sortable: false
			/*,renderer: function(value, metadata, record) {
				if(record.data.header === 'department') {
					metadata.css = 'header-grey';
				} else if(record.data.header === 'region') {
					metadata.css = 'header-grey2'
				}
			}*/
            ,editor: {
                xtype: 'textfield'
            },
            renderer: function(value, metadata, record) {
                if (record.data.type == 'dollar' && value != '') {
                    return Ext.util.Format.usMoney(value);
                }
                return value;
            }
		});
		dataColumns.push(this_column);
	}
	//var rowEditing = Ext.create('Ext.grid.plugin.RowEditing');
	var dataGrid = Ext.create('Ext.grid.Panel', {
		//region: 'center',
		//plugins: [rowEditing]
		store: dataStore
		,border: false
		,stateful: true
		,stateId: 'stateDataGrid'
		,columnLines: true
		,columns: dataColumns
		,flex: 1
		,viewConfig: {
			stripeRows: true
		}
        ,selType: 'cellmodel'
        ,plugins: [
            Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1
                ,listeners: {
                    edit: function(editor, e) {
                        if(e.record.dirty) {
                            console.log(e.record.validate());
                            if(e.record.validate().isValid()) {
                                // mode = (e.record.get('name') === "") ? 'insert' : 'update';
                                mode = e.originalValue === '' || e.originalValue == null ? 'insert' : 'update';
                                //this.syncData(e.rowIdx, mode);
                                Ext.Ajax.request({
                                    url: '../DaVinci/EntryData',
                                    params: {
                                        //store_id: 1,
                                        action: mode,
                                        rowIndex: e.rowIdx,
                                        data: Ext.encode(dataStore.getAt(e.rowIdx).data)
                                        ,value: e.value
                                        ,originalValue: e.originalValue
                                        ,field: e.field
                                    },
                                    scope: this,
                                    success: function(response, opts) {
                                        //Remove dirty
                                        var ajaxResponse = Ext.decode(response.responseText);
                                        if(ajaxResponse.success) {
                                            /*if(opts.params.action === 'insert') {
                                                customerId = ajaxResponse.customerId;
                                                dataStore.getAt(opts.params.rowIndex).set('customerId', customerId);
                                            }*/
                                            dataStore.getAt(opts.params.rowIndex).commit();
                                        } else {
                                            Ext.MessageBox.alert('Status', 'Error occured during update');
                                        }
                                    },
                                    failure: function(err) {
                                        Ext.MessageBox.alert('Status', 'Error occured during update');
                                    }
                                });
                            }
                        }
                    }
                    , beforeedit: function (obj, opts) {
                        console.log(dataStore.getAt(opts.rowIdx).raw);
                        var obj = dataStore.getAt(opts.rowIdx).raw;
                        if(obj.editable == undefined) {
                            return false;
                        } else return true;
                        //return obj.record.get('status');
                        //you can update the above logic to something else based on your criteria send false to stop editing

                    }
                }
            })
        ]
		,listeners: {
			viewready: function() {
				var c = this.columns[getWeekNumber()];
				var p = c.getPosition();

				this.scrollByDeltaX(p[0]);
			}
		}
	});
	// synchronize grid scrolling functions
	dataGrid.getView().on('bodyscroll', function (event,target) {
		entryGrid.scrollByDeltaY(target.scrollTop - entryGrid.getView().getEl().getScroll().top);
	});
	entryGrid.getView().on('bodyscroll', function (event, target) {
		dataGrid.scrollByDeltaY(target.scrollTop - dataGrid.getView().getEl().getScroll().top);
	});
	var dataGridPanel = Ext.create('Ext.panel.Panel', {
		region: 'center',
		border: false,
		layout: 'fit',
		items: dataGrid
	});
	function getWeekNumber() {
		var date = new Date();
		date.setFullYear(date.getFullYear(), date.getMonth(), date.getDate());
		var D = date.getDay();
		if(D == 0) D = 7;
		date.setDate(date.getDate() + (4 - D));
		var YN = date.getFullYear();
		var ZBDoCY = Math.floor((date.getTime() - new Date(YN, 0, 1, -6)) / 86400000);
		return 1 + Math.floor(ZBDoCY / 7);
	}
	var hiddenForm = Ext.widget({
		xtype: 'form',
		title: 'hiddenForm',
		url: '../../DaVinci/EntryData?download',
		standardSubmit: true,
		width: 0,
		height: 0,
		hidden: true,
		items: {
			defaultType: 'hiddenfield',
			items: [{name: 'field1',value: 'value1'}]
		}
	});
	var dataPanel = Ext.create('Ext.panel.Panel', {
		title: 'Data',
		border: false,
		layout: 'border',
		items: [dataToolbar,entryGrid,dataGridPanel]
	});

	//SETUP TAB
	function addUser(addUserForm,win) {
		var form = addUserForm.getForm();
		var values = form.getValues();
		if(form.isValid()) {
			var user = new User(values);
			userStore.add(user);
			form.reset();
			win.close();
		}
	}
	var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>',
			addUserForm = Ext.widget({
				xtype: 'form',
				collapsible: false,
				frame: false,
				border: false,
				bodyPadding: '5 5 0',
				items: {
					xtype: 'panel',
					frame: false,
					border: false,
					fieldDefaults: {msgTarget: 'side'}
                    ,defaultType: 'textfield',
					items: [ {
						columnWidth: '.2',
						fieldLabel: 'Username',
						labelAlign: 'right',
						beforeLabelTextTpl: required,
						name: 'username',
						allowBlank: false
					}, {
						columnWidth: '.2',
						labelAlign: 'right',
						fieldLabel: 'Password',
						beforeLabelTextTpl: required,
						name: 'password',
						allowBlank: false
					}, {
						xtype: 'combobox',
						forceSelection: true,
						store: regionStore,
						queryMode: 'local',
						displayField: 'name',
						valueField: 'name',
						columnWidth: '.2',
						labelAlign: 'right',
						fieldLabel: 'Region',
						beforeLabelTextTpl: required,
						name: 'region',
						allowBlank: false
					}, {
						xtype: 'combobox',
						forceSelection: true,
						store: departmentStore,
						queryMode: 'local',
						displayField: 'name',
						valueField: 'name',
						columnWidth: '.2',
						labelAlign: 'right',
						fieldLabel: 'Department',
						beforeLabelTextTpl: required,
						name: 'department',
						allowBlank: false
					}, {
						columnWidth: '.2',
						labelAlign: 'right',
						fieldLabel: 'Email',
						beforeLabelTextTpl: required,
						name: 'email',
						allowBlank: false,
						vtype: 'email',
						listeners: {
							specialkey: function(field, e) {
								if(e.getKey() == e.ENTER) {
									var win = this.findParentByType('window');
									addUser(addUserForm, win);
								}
							}
						}
					}, {
						xtype: 'checkbox',
						labelAlign: 'right',
						fieldLabel: 'Admin',
						name: 'admin',
						listeners: {
							specialkey: function(field, e) {
								if(e.getKey() == e.ENTER) {
									var win = this.findParentByType('window');
									addUser(addUserForm, win);
								}
							}
						}
					}]
				},
				buttons: [{
					formBind: true,
					text: 'Add',
					handler: function () {
						var win = this.findParentByType('window');
						addUser(addUserForm, win);
					}
				}, {
					text: 'Cancel',
					handler: function () {
						addUserForm.getForm().reset();
						var win = this.findParentByType('window');
						win.close();
					}
				}]
			}),
			addUserAction = Ext.create('Ext.Action', {
				iconCls: 'add-button',
				text: 'Add User',
				//disabled: true,
				handler: function(widget, event) {
					var userEditWindow = Ext.widget({
						xtype: 'window',
						title: 'Add User',
						layout: 'fit',
						autoShow: true,
						items: addUserForm,
						closeAction: 'hide'
					});
				}
			}),
			userGrid = Ext.create('Ext.grid.Panel', {
				//title:'Users',
				region: 'center',
				store: userStore,
				border: false,
				columns: [
					{text: 'User', dataIndex: 'username', flex: '.5'},
					{text: 'Email', dataIndex: 'email', flex: 1},
					{text: 'Department', dataIndex: 'department', flex: 1},
					{text: 'Region', dataIndex: 'region', flex: 1}
				],
				dockedItems: [
					{
						xtype: 'toolbar'
						,dock: 'top'
						,items: [ addUserAction ]
					}
				]
			}),
			userPanel = Ext.create('Ext.panel.Panel', {
				title: 'Users',
				border: false,
				layout: 'fit',
				flex: 1,
				items: [userGrid]
			});
	function addField(addFieldForm,win) {
		var form = addFieldForm.getForm();
		var values = form.getValues();
		if(form.isValid()) {
			var field = new Field(values);
			fieldStore.add(field);
			form.reset();
			win.close();
		}
	}
    var addFieldForm = Ext.widget({
        xtype: 'form',
        collapsible: false,
        frame: false,
        border: false,
        items: {
            xtype: 'panel',
            frame: false,
            border: false,
            defaultType: 'textfield',
            items: [{
                columnWidth: '.25',
                fieldLabel: 'Name',
                labelAlign: 'right',
                beforeLabelTextTpl: required,
                name: 'name',
                allowBlank: false
            }, {
                xtype: 'combobox',
                forceSelection: true,
                store: typeStore,
                //queryMode: 'local',
                displayField: 'name',
                valueField: 'name',
                columnWidth: '.25',
                labelAlign: 'right',
                fieldLabel: 'Type',
                beforeLabelTextTpl: required,
                name: 'type',
                allowBlank: false
            }, {
                xtype: 'combobox',
                forceSelection: true,
                store: regionStore,
                queryMode: 'local',
                displayField: 'name',
                valueField: 'name',
                columnWidth: '.25',
                labelAlign: 'right',
                fieldLabel: 'Region',
                beforeLabelTextTpl: required,
                name: 'region',
                allowBlank: false
            }, {
                xtype: 'combobox', forceSelection: true, displayField: 'name', valueField: 'name',
                store: departmentStore,
                queryMode: 'local',
                columnWidth: '.25', labelAlign: 'right', fieldLabel: 'Department', beforeLabelTextTpl: required,
                name: 'department',
                allowBlank: false
            }]
        },
        buttons: [{
            text: 'Add',
            formBind: true,
            handler: function () {
                var win = this.findParentByType('window');
                addField(addFieldForm,win);
            }
        }, {
            text: 'Cancel',
            handler: function () {
                addFieldForm.getForm().reset();
                var win = this.findParentByType('window');
                win.close();
            }
        }]
    }),
    addFieldAction = Ext.create('Ext.Action', {
        iconCls: 'add-button',
        text: 'Add Field',
        //disabled: true,
        handler: function(widget, event) {
            var fieldEditWindow = Ext.widget({
                xtype: 'window',
                title: 'Add Field',
                layout: 'fit',
                autoShow: true,
                items: addFieldForm,
                closeAction: 'hide'
            });
        }
    });
    var fieldGrid = Ext.create('Ext.grid.Panel', {
        store: fieldStore,
        border: false,
        columnLines: true,
        columns: [
            {text: 'Field', dataIndex: 'name', flex: 1},
            {text: 'Type', dataIndex: 'type', flex: 1},
            {text: 'Region', dataIndex: 'region', flex: 1},
            {text: 'Department', dataIndex: 'department', flex: 1}
        ]
        ,dockedItems: [
            {
                xtype: 'toolbar'
                ,dock: 'top'
                ,items: [ addFieldAction ]
            }
        ]
    });
    var fieldPanel = Ext.create('Ext.panel.Panel', {
        title: 'Fields',
        border: false,
        layout: 'fit',
        flex: 1,
        items: [ fieldGrid ]
    });

    function addMetric(addMetricForm, win) {
        var form = addMetricForm.getForm();
        var values = form.getValues();
        if(form.isValid()) {
            var field = new Metric(values);
            metricStore.add(field);
            form.reset();
            win.close();
        }
    }

    var addMetricForm = Ext.widget({
        xtype: 'form',
        collapsible: false,
        frame: false,
        border: false,
        items: {
            xtype: 'panel',
            frame: false,
            border: false,
            defaultType: 'textfield',
            items: [
                {
                    columnWidth: '.5',
                    fieldLabel: 'Name',
                    labelAlign: 'right',
                    beforeLabelTextTpl: required,
                    name: 'name',
                    allowBlank: false
                },
                {
                    columnWidth: '.5', fieldLabel: 'Value', labelAlign: 'right', beforeLabelTextTpl: required, name: 'value'
                }
            ]
        },
        buttons: [
            {
                text: 'Add',
                formBind: true,
                handler: function () {
                    var win = this.findParentByType('window');
                    addMetric(addMetricForm, win);
                }
            },
            {
                text: 'Cancel',
                handler: function () {
                    addMetricForm.getForm().reset();
                    var win = this.findParentByType('window');
                    win.close();
                }
            }
        ]
    });
    var addMetricAction = Ext.create('Ext.Action', {
        iconCls: 'add-button',
        text: 'Add Metric',
        handler: function (widget, event) {
            var fieldEditWindow = Ext.widget({
                xtype: 'window',
                title: 'Add Metric',
                layout: 'fit',
                autoShow: true,
                items: addMetricForm,
                closeAction: 'hide'
            });
        }
    });
    var metricGrid = Ext.create('Ext.grid.Panel', {
        store: metricStore,
        border: false,
        columnLines: true,
        columns: [
            {text: 'Name', dataIndex: 'name', flex: 1},
            {text: 'Value', dataIndex: 'value', flex: 1}
        ], dockedItems: [
            { xtype: 'toolbar', dock: 'top', items: [ addMetricAction ] }
        ]
    });
    var metricPanel = Ext.create('Ext.panel.Panel', {
        title: 'Metrics',
        border: false,
        layout: 'fit',
        flex: 1,
        items: [ metricGrid ]
    });

    function addCalc(addCalcForm, win) {
        var form = addCalcForm.getForm();
        var values = form.getValues();
        if(form.isValid()) {
            var field = new Calc(values);
            calcStore.add(field);
            form.reset();
            win.close();
        }
    }
    var addCalcForm = Ext.widget({
        xtype: 'form',
        collapsible: false,
        frame: false,
        border: false,
        items: {
            xtype: 'panel',
            frame: false,
            border: false,
            defaultType: 'textfield',
            items: [{
                columnWidth: '.15',
                fieldLabel: 'Name',
                labelAlign: 'right',
                beforeLabelTextTpl: required,
                name: 'name',
                allowBlank: false
            },{
                columnWidth: '.25'
                ,fieldLabel: 'Definition'
                ,labelAlign: 'right'
                ,beforeLabelTextTpl: required
                ,name: 'definition'
            },{
                xtype: 'combobox',
                forceSelection: true,
                store: typeStore,
                //queryMode: 'local',
                displayField: 'name',
                valueField: 'name',
                columnWidth: '.15',
                labelAlign: 'right',
                fieldLabel: 'Type',
                beforeLabelTextTpl: required,
                name: 'type',
                allowBlank: false
            },{
                xtype: 'combobox',
                forceSelection: true,
                store: regionStore,
                queryMode: 'local',
                displayField: 'name',
                valueField: 'name',
                columnWidth: '.15',
                labelAlign: 'right',
                fieldLabel: 'Region',
                beforeLabelTextTpl: required,
                name: 'region',
                allowBlank: false
            },{
                xtype: 'combobox', forceSelection: true, displayField: 'name', valueField: 'name',
                store: departmentStore,
                queryMode: 'local',
                columnWidth: '.15', labelAlign: 'right', fieldLabel: 'Department', beforeLabelTextTpl: required,
                name: 'department',
                allowBlank: false
            },
                {
                    xtype: 'combobox', forceSelection: true, displayField: 'name', valueField: 'name',
                    store: scopeStore,
                    //queryMode: 'local',
                    columnWidth: '.15', labelAlign: 'right', fieldLabel: 'Scope', beforeLabelTextTpl: required,
                    name: 'scope',
                    allowBlank: false
                }
            ]
        },
        buttons: [{
            text: 'Add',
            formBind: true,
            handler: function () {
                var win = this.findParentByType('window');
                addCalc(addCalcForm, win);
            }
        },{
            text: 'Cancel',
            handler: function () {
                addCalcForm.getForm().reset();
                var win = this.findParentByType('window');
                win.close();
            }
        }
        ]
    });
    var addCalcAction = Ext.create('Ext.Action', {
        iconCls: 'add-button',
        text: 'Add Calculation',
        handler: function (widget, event) {
            var fieldEditWindow = Ext.widget({
                xtype: 'window',
                title: 'Add Field',
                layout: 'fit',
                autoShow: true,
                items: addCalcForm,
                closeAction: 'hide'
            });
        }
    });
    var calcGrid = Ext.create('Ext.grid.Panel', {
        store: calcStore,
        border: false,
        columnLines: true,
        columns: [
            {text: 'Name', dataIndex: 'name', flex: 1},
            {text: 'Definition', dataIndex: 'definition', flex: 1},
            {text: 'Type', dataIndex: 'type', flex: 1},
            {text: 'Region', dataIndex: 'region', flex: 1},
            {text: 'Department', dataIndex: 'department', flex: 1},
            {text: 'Scope', dataIndex: 'scope', flex: 1}
        ], dockedItems: [{ xtype: 'toolbar', dock: 'top', items: [ addCalcAction ] }]
    });
    var calcPanel = Ext.create('Ext.panel.Panel', {
        title: 'Calculated Fields',
        border: false,
        layout: 'fit',
        flex: 1,
        items: [ calcGrid ]
    });

    var setupPanel = Ext.create('Ext.panel.Panel', {
        title: 'Setup',
        border: false,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items: [ userPanel,fieldPanel,metricPanel,calcPanel ]
    });
    var tabs = Ext.widget('tabpanel', {
        id: 'tab-panel',
        padding: 10,
        region: 'center',
        activeTab: 0,
        plain: true,
        defaults: { bodyPadding: 0 },
        items: [ dataPanel, setupPanel ]
    });

	// root
	Ext.create('Ext.Viewport', {
		padding: 10,
		layout: 'border',
		items: [{
				xtype: 'box',
				id: 'header',
				region: 'north',
				//html: '<h1> DaVinci</h1>',
				html: '<table width=100%><tr><td><h1> DaVinci</h1></td><td align="right" style="padding-right: 10px; padding-top: 2px"><img src="../resources/logo-white.png" height=25/></td></tr></table>',
				height: 30
			},tabs
		],
		renderTo: Ext.getBody()
	});
});