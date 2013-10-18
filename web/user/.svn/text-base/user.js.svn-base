Ext.require([
    'Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.state.*'
]);

Ext.onReady(function () {
	/** @namespace Ext.QuickTips */

	Ext.QuickTips.init();

	Ext.define('Week', {
		extend: 'Ext.data.Model',
		fields: [
			{ name: 'date', type: 'string' },
			{ name: 'number', type: 'string' },
			{ name: 'label', type: 'string' }
		]
		//validations
	});
	var weekStore = Ext.create('Ext.data.Store', {
		// store configs
		model: 'Week',
		//autoLoad: true,
		//autoSync: true
		// reader configs
		proxy: {
			type: 'ajax',
			api: {
				//create: '../../DaVinci/EntryInfo,
				//read: '../../DaVinci/EntryInfo'
				//update: '../../DaVinci/EntryInfo?updateuser',
				//destroy: '../../DaVinci/EntryInfo?removeuser'
			},
			model: 'Week',
			reader: {
				type: 'json',
				root: 'weeks',
				successProperty: 'success'
			}
		},
		listeners: {
			load: function (sender, node, records) {
				//var json = sender.proxy.reader.jsonData;
				//fieldStore.loadRawData(json);
				//regionStore.loadRawData(json);
				//departmentStore.loadRawData(json);
			}

		}
	});

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
    for(i = 0; i < 52; i++) {
        var this_field = new Ext.data.Field({
            name: 'week' + (i + 1),
            type: 'string'
        });
        myFields.push(this_field);
    }
    Ext.define('Entry', {
        extend: 'Ext.data.Model',
        fields: myFields
    });
    var dataStore = Ext.create('Ext.data.Store', {
        model: 'Entry',
        autoLoad: true,
        proxy: {
            type: 'ajax',
            api: {
                read: '../../DaVinci/EntryData'
            },
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

    var entryGrid = Ext.create('Ext.grid.Panel', {
        region: 'west',
        store: dataStore,
        border: false,
        stateful: true,
        //stateId: 'stateEntryGrid',
        columnLines: true,
        columns: [
            {
                text: 'Entry',
                dataIndex: 'entry',
                width: 200,
                //locked: true,
                sortable: false,
                renderer: function (value, metadata, record) {
                    if(record.data.header === 'department') {
                        metadata.css = 'header-grey';
                        return "<b>" + value + "</b>"
                    } else if(record.data.header === 'region') {
                        metadata.css = 'header-grey2';
                        return "<i>" + value + "</i>"
                    } else {
                        return value;
                    }
                }
            }
        ], viewConfig: {
            stripeRows: true
        }
    });
    // create the columns dynamically.
    var dataColumns = [];
    for(var i = 0; i < 52; i++) {
        var this_column = new Ext.grid.column.Column({
            text: 'Week ' + (i + 1),
            dataIndex: 'week' + (i + 1), sortable: false
            /*,renderer: function(value, metadata, record) {
             if(record.data.header === 'department') {
             metadata.css = 'header-grey';
             } else if(record.data.header === 'region') {
             metadata.css = 'header-grey2'
             }
             }*/, editor: {
                xtype: 'textfield'
            }
        });
        dataColumns.push(this_column);
    }
    //var rowEditing = Ext.create('Ext.grid.plugin.RowEditing');
    var dataGrid = Ext.create('Ext.grid.Panel', {
        region: 'center',
        store: dataStore
        ,border: false
        ,stateful: true
        //,stateId: 'stateDataGrid'
        ,columnLines: true, columns: dataColumns, flex: 1
        ,viewConfig: {
            stripeRows: true
        }
        ,selType: 'cellmodel'
        ,plugins: [
            Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1, listeners: {
                    edit: function (editor, e) {
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
                                        data: Ext.encode(dataStore.getAt(e.rowIdx).data), value: e.value, originalValue: e.originalValue, field: e.field
                                    },
                                    scope: this,
                                    success: function (response, opts) {
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
                                    failure: function (err) {
                                        Ext.MessageBox.alert('Status', 'Error occured during update');
                                    }
                                });
                            }
                        }
                    }, beforeedit: function (obj, opts) {
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
            viewready: function () {
                var c = this.columns[getWeekNumber()];
                var p = c.getPosition();

                this.scrollByDeltaX(p[0]);
            }
        }
    });
    // synchronize grid scrolling functions
    dataGrid.getView().on('bodyscroll', function (event, target) {
        entryGrid.scrollByDeltaY(target.scrollTop - entryGrid.getView().getEl().getScroll().top);
    });
    entryGrid.getView().on('bodyscroll', function (event, target) {
        dataGrid.scrollByDeltaY(target.scrollTop - dataGrid.getView().getEl().getScroll().top);
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


    var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
    var weekComboBox = Ext.create('Ext.form.field.ComboBox', {
        name: 'week',
        fieldLabel: 'Select a single week',
        beforeLabelTextTpl: required,
        displayField: 'label',
        //width: 150,
        //labelWidth: 300,
        store: weekStore,
        queryMode: 'local',
        typeAhead: true
    });

    var weekEntryForm = Ext.create('Ext.form.Panel', {
        region: 'center',
        layout: 'form',
        collapsible: false,
        //id: 'week-entry-form',
        url: '../../DaVinci/EntryData',
        frame: true,
        //title:'Add Field',
        bodyPadding: '5 5 0',
        width: 700,
        fieldDefaults: {
            msgTarget: 'side'
            ,labelWidth: '260'
	        ,labelAlign: 'right'
        },
        defaultType: 'textfield',
        items: [ weekComboBox ],
        buttons: [{
            text: 'Add',
            formBind: true,
            handler: function () {
                var form = this.up('form').getForm();
                if (form.isValid()) {
                    form.submit({
                        success: function (form, action) {
                            Ext.Msg.alert('Success');
                            form.reset();
                        }
                        ,failure: function (form, action) {
                            Ext.Msg.alert('Failure', action.response.responseText);
                        }
                    });
                }
            }
        }, {
            text: 'Cancel',
            handler: function () { this.up('form').getForm().reset(); }
        }]
    });

	Ext.Ajax.request({
		url: '../../DaVinci/EntryInfo',
		success: function(response, opts) {
			var obj = Ext.decode(response.responseText);
			//console.dir(obj);
			Ext.each(obj.inputs, function(input) {
				//var input_name;
				//var input_type = 'TextField';
				//var input_definition = new Array();

				if(input.name != undefined) {
					var field = new Ext.form.TextField({
						//id: 'form-zombie-' + zombie_ip + '-field-' + input_name,
						fieldLabel: input.name,
						beforeLabelTextTpl: required,
						name: input.name + " --- " + input.fieldId,
						//width: 175,
						renderer: 'usMoney',
						allowBlank:false
					});
					weekEntryForm.add(field);
				}
				/*else if(typeof input == 'object') {
				 //input_name = array_key(input);

				 for(definition in input) {
				 if(typeof definition == 'string') {

				 }
				 }
				 }*/ else {
					return;
				}
			});
			weekStore.loadRawData(obj.weeks);
		},
		failure: function(response, opts) {
			console.log('server-side failure with status code ' + response.status);
		}
	});

    //viewport
    Ext.create('Ext.Viewport', {
        padding: 10,
        layout: 'border',
        items: [{
                xtype: 'box',
                id: 'header',
                region: 'north',
	            html: '<table width=100%><tr><td><h1>Da  Vinci</h1></td><td align="right" style="padding-right: 10px; padding-top: 2px"><img src="../resources/logo-white.png" height=25/></td></tr></table>',
	            height: 30
            }
            ,{ xtype: 'panel', region: 'center', border: true ,layout: { type: 'vbox', align: 'center', pack: 'center' } ,items: weekEntryForm }
            ,{ xtype: 'panel', region: 'south', border: false, layout: 'hbox' ,items: [ entryGrid ,dataGrid ] }
        ],
        renderTo:Ext.getBody()
    });
});